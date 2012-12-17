/* 
 * This file is part of the Echo Extras Project.
 * Copyright (C) 2005-2009 NextApp, Inc.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

package nextapp.echo.extras.webcontainer.sync.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nextapp.echo.app.Component;
import nextapp.echo.app.serial.SerialException;
import nextapp.echo.app.serial.SerialPropertyPeer;
import nextapp.echo.app.update.ClientUpdateManager;
import nextapp.echo.app.update.ServerComponentUpdate;
import nextapp.echo.app.util.Context;
import nextapp.echo.extras.app.Tree;
import nextapp.echo.extras.app.event.TreeExpansionEvent;
import nextapp.echo.extras.app.event.TreeExpansionListener;
import nextapp.echo.extras.app.serial.property.SerialPropertyPeerConstants;
import nextapp.echo.extras.app.tree.TreeModel;
import nextapp.echo.extras.app.tree.TreePath;
import nextapp.echo.extras.app.tree.TreeSelectionModel;
import nextapp.echo.extras.webcontainer.CommonResources;
import nextapp.echo.extras.webcontainer.service.CommonService;
import nextapp.echo.webcontainer.AbstractComponentSynchronizePeer;
import nextapp.echo.webcontainer.ContentType;
import nextapp.echo.webcontainer.RenderState;
import nextapp.echo.webcontainer.ResourceRegistry;
import nextapp.echo.webcontainer.ServerMessage;
import nextapp.echo.webcontainer.Service;
import nextapp.echo.webcontainer.UserInstance;
import nextapp.echo.webcontainer.WebContainerServlet;
import nextapp.echo.webcontainer.service.JavaScriptService;
import nextapp.echo.webcontainer.util.MultiIterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TreePeer 
extends AbstractComponentSynchronizePeer {

    /**
     * Holds a selection update from the client
     */
    private static class TreeSelectionUpdate {
        boolean clear = false;
        List addedSelections = new LinkedList();
        List removedSelections = new LinkedList();
    }
    
    /**
     * Translates a selection update directive from the client to 
     * a TreeSelectionUpdatePeer object.
     */
    public static class TreeSelectionUpdatePeer 
    implements SerialPropertyPeer {

        public Object toProperty(Context context, Class objectClass,
                Element propertyElement) throws SerialException {
            TreeSelectionUpdate update = new TreeSelectionUpdate();
            String cStr = propertyElement.getAttribute("c");
            update.clear = Boolean.valueOf(cStr).booleanValue();
            if (propertyElement.hasAttribute("r")) {
                String rStr = propertyElement.getAttribute("r");
                String[] rTokens = rStr.split(",");
                for (int i = 0; i < rTokens.length; i++) {
                    update.removedSelections.add(Integer.valueOf(rTokens[i]));
                }
            }
            if (propertyElement.hasAttribute("a")) {
                String aStr = propertyElement.getAttribute("a");
                String[] aTokens = aStr.split(",");
                for (int i = 0; i < aTokens.length; i++) {
                    update.addedSelections.add(Integer.valueOf(aTokens[i]));
                }
            }
            return update;
        }

        public void toXml(Context context, Class objectClass,
                Element propertyElement, Object propertyValue)
                throws SerialException {
            throw new UnsupportedOperationException();
        }
        
    }
    
    /**
     * Holds the state of the client side tree.
     */
    private static class TreeRenderState 
    implements RenderState {
        private static final long serialVersionUID = 1L;
        /**
         * Holds all paths that are sent down to the client. When a path has children it
         * is only added to this set if all it's children are sent down too.
         */
        private Set sentPaths = new HashSet();
        /**
         * Holds paths that are changed on the server since the last synchronization
         */
        private Set changedPaths = new HashSet();
        /**
         * Holds all selection paths that have not been sent to the client
         */
        private Set unsentSelections = new HashSet();
        /**
         * The path that changed the expansion state of as a result
         * of a client update.
         */
        private TreePath clientPath;
        /**
         * Indicates whether a full render is necessary
         */
        private boolean fullRender = true;
        private final Tree tree;
        
        /**
         * Listens for changes in the expansion state. All changed paths are added
         * to the <code>changedPaths</code> list.
         */
        private TreeExpansionListener expansionListener = new TreeExpansionListener() {
            public void treeCollapsed(TreeExpansionEvent event) {
                if (!event.getPath().equals(clientPath)) {
                    changedPaths.add(event.getPath());
                }
            }
            
            public void treeExpanded(TreeExpansionEvent event) {
                if (!event.getPath().equals(clientPath)) {
                    changedPaths.add(event.getPath());
                }
            }
        };
        
        public TreeRenderState(Tree tree) {
            this.tree = tree;
            tree.addTreeExpansionListener(expansionListener);
        }
        
        public void setClientPath(TreePath path) {
            this.clientPath = null;
            if (isSent(path)) {
                this.clientPath = path;
            }
        }
        
        public boolean isFullRender() {
            return fullRender;
        }
        
        public void setFullRender(boolean newValue) {
            fullRender = newValue;
        }
        
        public void addSentPath(TreePath path) {
            sentPaths.add(path);
        }
        
        public void removeSentPath(TreePath path) {
            sentPaths.remove(path);
        }
        
        public boolean isSent(TreePath path) {
            return sentPaths.contains(path);
        }
        
        /**
         * Returns all paths of which the expansion state has been changed since
         * the last synchronization. The paths are sorted by row index.
         * 
         * @return all paths that are changed since last synchronization
         */
        public Iterator changedPaths() {
            ArrayList list = new ArrayList(changedPaths);
            Collections.sort(list, new Comparator() {
                public int compare(Object obj1, Object obj2) {
                    TreePath path1 = (TreePath) obj1;
                    TreePath path2 = (TreePath) obj2;
                    if (path1 == path2 || path1.equals(path2)) {
                        return 0;
                    }
                    int path1Count = path1.getPathCount();
                    int path2Count = path2.getPathCount();
                    if (path1Count == 1) { // path1 has only root element
                        return -1;
                    } else if (path2Count == 1) { // path2 has only root element
                        return 1;
                    }
                    int end = Math.min(path1Count, path2Count);
                    int i = 1;
                    for (; i < end; i++) {
                        Object comp1 = path1.getPathComponent(i);
                        Object comp2 = path2.getPathComponent(i);
                        if (comp1 != comp2) {
                            return compareNodes(i, path1, path2);
                        }
                    }
                    if (path1Count == i) { // path1 has only root element
                        return -1;
                    } else if (path2Count == i) { // path2 has only root element
                        return 1;
                    }
                    return compareNodes(i, path1, path2);
                }
                
                private int compareNodes(int index, TreePath path1, TreePath path2) {
                    Object commonParent = path1.getPathComponent(index - 1);
                    int index1 = tree.getModel().getIndexOfChild(commonParent, path1.getPathComponent(index));
                    int index2 = tree.getModel().getIndexOfChild(commonParent, path2.getPathComponent(index));
                    if (index1 < index2) {
                        return -1;
                    } else if (index1 > index2) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            return list.iterator();
        }
        
        public void clearChangedPaths() {
            clientPath = null;
            changedPaths.clear();
        }
        
        public boolean hasChangedPaths() {
            return clientPath != null || !changedPaths.isEmpty();
        }
        
        public boolean isPathChanged(TreePath path) {
            return changedPaths.contains(path);
        }
        
        public boolean hasUnsentSelections() {
            return !unsentSelections.isEmpty();
        }
        
        public void addUnsentSelection(TreePath path) {
            unsentSelections.add(path);
        }
        
        public void removeUnsentSelection(TreePath path) {
            unsentSelections.remove(path);
        }
    }
    
    private class TreeStructure {
        
        Tree tree;
        
        TreeStructure(Tree tree) {
            this.tree = tree;
        }
    }
    
    /**
     * Uses TreeStructureRenderer to send down the current tree structure to the client.
     */
    public static class TreeStructurePeer 
    implements SerialPropertyPeer {
    
        /**
         * @see nextapp.echo.app.serial.SerialPropertyPeer#toProperty(nextapp.echo.app.util.Context, 
         *      java.lang.Class, org.w3c.dom.Element)
         */
        public Object toProperty(Context context, Class objectClass, Element propertyElement) 
        throws SerialException {
            throw new UnsupportedOperationException();
        }
    
        /**
         * @see nextapp.echo.app.serial.SerialPropertyPeer#toXml(nextapp.echo.app.util.Context, 
         *      java.lang.Class, org.w3c.dom.Element, java.lang.Object)
         */
        public void toXml(Context context, Class objectClass, Element propertyElement, Object propertyValue) 
        throws SerialException {
            Tree tree = ((TreeStructure) propertyValue).tree;
            TreeStructureRenderer renderer = new TreeStructureRenderer(propertyElement, tree);
            propertyElement.setAttribute("t", SerialPropertyPeerConstants.PROPERTY_TYPE_PREFIX + "TreeStructure");
            UserInstance userInstance = (UserInstance) context.get(UserInstance.class);
            TreeRenderState renderState = (TreeRenderState) userInstance.getRenderState(tree);
            if (renderState == null) {
                renderState = new TreeRenderState(tree);
                userInstance.setRenderState(tree, renderState);
            }
            renderer.render(context, renderState);
            renderState.clearChangedPaths();
        }
    }
    
    /**
     * Sends down the tree structure to the client. If a render state exists and a full render
     * is not required, an update will be sent.
     */
    private static class TreeStructureRenderer {
        private Tree tree;
        private TreeModel model;
        private int columnCount;
        private Element propertyElement;
        private Document document;
        private Set renderedPaths = new HashSet();
        private TreeRenderState renderState;
        
        TreeStructureRenderer(Element propertyElement, Tree tree) {
            this.propertyElement = propertyElement;
            document = propertyElement.getOwnerDocument();
            this.tree = tree;
            columnCount = getColumnCount(tree);
            model = tree.getModel();
        }
        
        private void render(Context context, TreeRenderState renderState) {
            this.renderState = renderState;
            if (renderState.isFullRender()) {
                if (tree.isHeaderVisible()) {
                    // header
                    renderNode(context, null, null, false);
                }
                Object value = model.getRoot();
                renderNode(context, value, new TreePath(value), true);
                renderState.setFullRender(false);
                propertyElement.setAttribute("fr", "1");
            } else if (renderState.hasChangedPaths()) {
                for (Iterator iterator = renderState.changedPaths(); iterator.hasNext();) {
                    TreePath path = (TreePath) iterator.next();
                    renderNode(context, path.getLastPathComponent(), path, true);
                    renderedPaths.add(path);
                }
            }
        }
        
        private void renderNode(Context context, Object value, TreePath path, boolean root) {
            if (renderedPaths.contains(path)) {
                return;
            }
            if (renderState.isSent(path) && !renderState.isPathChanged(path)) {
                return;
            }
            
            UserInstance userInstance = (UserInstance) context.get(UserInstance.class);
            
            renderedPaths.add(path);
            Component component = tree.getComponent(path, 0);
            boolean expanded = tree.isExpanded(path);
            String id = userInstance.getClientRenderId(component);
            Element eElement = document.createElement("e");
            eElement.setAttribute("i", id);
            if (path != null) {
                TreePath parentPath = path.getParentPath();
                if (parentPath != null) {
                    eElement.setAttribute("p", userInstance.getClientRenderId(tree.getComponent(parentPath, 0)));
                }
            }
            boolean leaf = value != null && model.isLeaf(value);
            if (path == null) {
                eElement.setAttribute("h", "1");
            } else {
                if (expanded) {
                    eElement.setAttribute("ex", "1");
                } else {
                    if (leaf) {
                        eElement.setAttribute("l", "1");
                    }
                }
                if (root) {
                    eElement.setAttribute("r", "1");
                }
            }
            
            if (!renderState.isSent(path)) {
                for (int i = 1; i < columnCount; ++i) {
                    Component columnComponent = tree.getComponent(path, i);
                    Element columnElement = document.createElement("c");
                    columnElement.setAttribute("i", userInstance.getClientRenderId(columnComponent));
                    eElement.appendChild(columnElement);
                }
            }
            
            propertyElement.appendChild(eElement);
            
            if (value == null) {
                return;
            }
            if (expanded) {
                int childCount = model.getChildCount(value);
                for (int i = 0; i < childCount; ++i) {
                    Object childValue = model.getChild(value, i);
                    renderNode(context, childValue, path.pathByAddingChild(childValue), false);
                }
            }
            if (expanded || leaf) {
                renderState.addSentPath(path);
            }
        }
    }
    
    private static int getColumnCount(Tree tree) {
        return tree.getColumnModel().getColumnCount();
    }
    
    /**
     * Translates the current selection model to a comma separated string. This string
     * holds the render ids of the nodes. If a node is not yet sent to the client, the
     * selection state will be kept on the client (using the unset selections list of 
     * the render state object).
     * 
     * @param context
     * @param selectionModel
     * @param tree
     * @return the selection string
     */
    private static String getSelectionString(Context context, TreeSelectionModel selectionModel, Tree tree) {
        UserInstance userInstance = (UserInstance) context.get(UserInstance.class);
        TreeRenderState renderState = (TreeRenderState) userInstance.getRenderState(tree);
        
        StringBuffer selection = new StringBuffer();
        TreePath[] paths = selectionModel.getSelectionPaths();
        for (int i = 0; i < paths.length; i++) {
            TreePath path = paths[i];
            Component component = tree.getComponent(path, 0);
            if (component == null) {
                if (renderState != null) {
                    renderState.addUnsentSelection(path);
                }
            } else {
                String id = userInstance.getClientRenderId(component);
                if (renderState != null) {
                    renderState.removeUnsentSelection(path);
                }
                if (selection.length() > 0) {
                    selection.append(",");
                }
                selection.append(id);
            }
        }
        return selection.toString();
    }

    private static final String PROPERTY_TREE_STRUCTURE = "treeStructure";
    private static final String PROPERTY_COLUMN_COUNT = "columnCount";
    private static final String PROPERTY_COLUMN_WIDTH = "columnWidth";
    private static final String PROPERTY_SELECTION_MODE = "selectionMode";
    
    private static final String EXPANSION_PROPERTY = "expansion"; 
    private static final String SELECTION_PROPERTY = "selectionUpdate";
    
    private static final String[] MODEL_CHANGED_UPDATE_PROPERTIES = new String[] { PROPERTY_TREE_STRUCTURE,
            PROPERTY_COLUMN_COUNT};
    
    private static final Service TREE_SERVICE = JavaScriptService.forResources("EchoExtras.RemoteTree",  
            new String[]{ "nextapp/echo/extras/webcontainer/resource/Application.RemoteTree.js",
                    "nextapp/echo/extras/webcontainer/resource/Serial.RemoteTree.js",
                    "nextapp/echo/extras/webcontainer/resource/Sync.RemoteTree.js" });

    static {
        WebContainerServlet.getServiceRegistry().add(TREE_SERVICE);
        CommonResources.install();
        ResourceRegistry resources = WebContainerServlet.getResourceRegistry();
        resources.add("Extras", "image/tree/Transparent.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/Closed.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/Open.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/JoinSolid.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/JoinBottomSolid.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/VerticalSolid.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/ClosedSolid.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/ClosedBottomSolid.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/OpenSolid.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/OpenBottomSolid.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/JoinDotted.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/JoinBottomDotted.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/VerticalDotted.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/ClosedDotted.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/ClosedBottomDotted.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/OpenDotted.gif", ContentType.IMAGE_GIF);
        resources.add("Extras", "image/tree/OpenBottomDotted.gif", ContentType.IMAGE_GIF);
    }

    public TreePeer() {
        super();
        addOutputProperty(PROPERTY_TREE_STRUCTURE);
        addOutputProperty(PROPERTY_COLUMN_COUNT);
        addOutputProperty(PROPERTY_COLUMN_WIDTH, true);
        addOutputProperty(PROPERTY_SELECTION_MODE);
        addOutputProperty(Tree.SELECTION_CHANGED_PROPERTY);
        
        addEvent(new AbstractComponentSynchronizePeer.EventPeer(Tree.INPUT_ACTION, Tree.ACTION_LISTENERS_CHANGED_PROPERTY));
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getComponentClass()
     */
    public Class getComponentClass() {
        return Tree.class;
    }
    
    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getClientComponentType(boolean)
     */
    public String getClientComponentType(boolean mode) {
        return "Extras.RemoteTree";
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getOutputProperty(
     *      nextapp.echo.app.util.Context, nextapp.echo.app.Component, java.lang.String, int)
     */
    public Object getOutputProperty(Context context, Component component, String propertyName, int propertyIndex) {
        Tree tree = (Tree) component;
        if (PROPERTY_TREE_STRUCTURE.equals(propertyName)) {
            return new TreeStructure(tree);
        } else if (PROPERTY_COLUMN_COUNT.equals(propertyName)) {
            return new Integer(getColumnCount(tree));
        } else if (PROPERTY_COLUMN_WIDTH.equals(propertyName)) {
            return tree.getColumnModel().getColumn(propertyIndex).getWidth();
        } else if (PROPERTY_SELECTION_MODE.equals(propertyName)) {
            return new Integer(tree.getSelectionModel().getSelectionMode());
        } else if (Tree.SELECTION_CHANGED_PROPERTY.equals(propertyName)) {
            return getSelectionString(context, tree.getSelectionModel(), tree);
        }
        return super.getOutputProperty(context, component, propertyName, propertyIndex);
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getOutputPropertyIndices(nextapp.echo.app.util.Context,
     *      nextapp.echo.app.Component, java.lang.String)
     */
    public Iterator getOutputPropertyIndices(Context context, Component component, String propertyName) {
        if (PROPERTY_COLUMN_WIDTH.equals(propertyName)) {
            final Iterator columnIterator = ((Tree) component).getColumnModel().getColumns();
            return new Iterator() {
                private int i = 0;
            
                public boolean hasNext() {
                    return columnIterator.hasNext();
                }
            
                public Object next() {
                    columnIterator.next();
                    return new Integer(i++);
                }
            
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
            return super.getOutputPropertyIndices(context, component, propertyName);
        }
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#getUpdatedOutputPropertyNames(
     *      nextapp.echo.app.util.Context,
     *      nextapp.echo.app.Component,
     *      nextapp.echo.app.update.ServerComponentUpdate)
     */
    public Iterator getUpdatedOutputPropertyNames(Context context, Component component, 
            ServerComponentUpdate update) {
        UserInstance userInstance = (UserInstance) context.get(UserInstance.class);
        
        Iterator normalPropertyIterator = super.getUpdatedOutputPropertyNames(context, component, update);
        HashSet extraProperties = new HashSet();
        
        if (update.hasRemovedChildren() || update.hasRemovedDescendants()) {
            userInstance.removeRenderState(component);
            extraProperties.add(PROPERTY_TREE_STRUCTURE);
            extraProperties.add(Tree.SELECTION_CHANGED_PROPERTY);
        }
        
        if (update.hasUpdatedProperty(Tree.MODEL_CHANGED_PROPERTY)) {
            extraProperties.addAll(Arrays.asList(MODEL_CHANGED_UPDATE_PROPERTIES));
        } 
        if (update.hasUpdatedProperty(Tree.EXPANSION_STATE_CHANGED_PROPERTY)) {
            TreeRenderState renderState = (TreeRenderState) userInstance.getRenderState(component);
            if (renderState == null || renderState.hasChangedPaths()) {
                extraProperties.add(PROPERTY_TREE_STRUCTURE);
            }
            if (renderState == null || renderState.hasUnsentSelections()) {
                extraProperties.add(Tree.SELECTION_CHANGED_PROPERTY);
            }
        }
        return new MultiIterator(new Iterator[] { normalPropertyIterator, extraProperties.iterator() });
    }
    
    /**
     * @see nextapp.echo.webcontainer.AbstractComponentSynchronizePeer#storeInputProperty(nextapp.echo.app.util.Context, 
     *      nextapp.echo.app.Component, java.lang.String, int, java.lang.Object)
     */
    public void storeInputProperty(Context context, Component component,
            String propertyName, int index, Object newValue) {
        Tree tree = (Tree) component;
        if (EXPANSION_PROPERTY.equals(propertyName)) {
            int row = ((Integer)newValue).intValue();
            UserInstance userInstance = (UserInstance) context.get(UserInstance.class);
            TreeRenderState renderState = (TreeRenderState) userInstance.getRenderState(component);
            if (renderState == null) {
                renderState = new TreeRenderState(tree);
                userInstance.setRenderState(component, renderState);
            }
            TreePath path = tree.getPathForRow(row);
            renderState.setClientPath(path);
            renderState.removeSentPath(path);
            
            ClientUpdateManager clientUpdateManager = (ClientUpdateManager) context.get(ClientUpdateManager.class);
            clientUpdateManager.setComponentProperty(component, Tree.EXPANSION_STATE_CHANGED_PROPERTY, newValue);
        } else if (SELECTION_PROPERTY.equals(propertyName)) {
            TreeSelectionUpdate update = (TreeSelectionUpdate) newValue;
            TreeSelectionModel selectionModel = tree.getSelectionModel();
            // process deselections
            if (!update.removedSelections.isEmpty()) {
                TreePath[] paths = new TreePath[update.removedSelections.size()];
                int i = 0;
                for (Iterator iterator = update.removedSelections.iterator(); iterator.hasNext();) {
                    Integer row = (Integer) iterator.next();
                    paths[i++] = tree.getPathForRow(row.intValue());
                }
                selectionModel.removeSelectionPaths(paths);
            }
            // process selections
            if (!update.addedSelections.isEmpty()) {
                TreePath[] paths = new TreePath[update.addedSelections.size()];
                int i = 0;
                for (Iterator iterator = update.addedSelections.iterator(); iterator.hasNext();) {
                    Integer row = (Integer) iterator.next();
                    paths[i++] = tree.getPathForRow(row.intValue());
                }
                if (update.clear) {
                    selectionModel.setSelectionPaths(paths);
                } else {
                    selectionModel.addSelectionPaths(paths);
                }
            }
        } else {
            super.storeInputProperty(context, component, propertyName, index, newValue);
        }
    }
    
    public Class getInputPropertyClass(String propertyName) {
        if (EXPANSION_PROPERTY.equals(propertyName)) {
            return Integer.class;
        } else if (SELECTION_PROPERTY.equals(propertyName)) {
            return TreeSelectionUpdate.class;
        }
        return super.getInputPropertyClass(propertyName);
    }
    
    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#init(nextapp.echo.app.util.Context, Component)
     */
    public void init(Context context, Component component) {
        super.init(context, component);
        ServerMessage serverMessage = (ServerMessage) context.get(ServerMessage.class);
        serverMessage.addLibrary(CommonService.INSTANCE.getId());
        serverMessage.addLibrary(TREE_SERVICE.getId());
    }
}
