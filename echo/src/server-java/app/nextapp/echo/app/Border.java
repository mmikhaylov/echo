/*
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2009 NextApp, Inc.
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

package nextapp.echo.app;

import java.io.Serializable;

/**
 * A representation of a simple border.
 */
public class Border
implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 20070101L;

    /** Index of top side in returned array of sides. */
    public static final int SIDE_TOP = 0;

    /** Index of right side in returned array of sides. */
    public static final int SIDE_RIGHT = 1;

    /** Index of bottom side in returned array of sides. */
    public static final int SIDE_BOTTOM = 2;

    /** Index of left side in returned array of sides. */
    public static final int SIDE_LEFT = 3;

    /** Index of the top-left border radius in returned array of border radii. */
    public static final int RADIUS_TOP_LEFT = 0;

    /** Index of the top-right border radius in returned array of border radii. */
    public static final int RADIUS_TOP_RIGHT = 1;

    /** Index of the bottom-left border radius in returned array of border radii. */
    public static final int RADIUS_BOTTOM_LEFT = 2;

    /** Index of the bottom-right border radius in returned array of border radii. */
    public static final int RADIUS_BOTTOM_RIGHT = 3;

    /**
     * A border style that causes no border to be rendered.
     */
    public static final int STYLE_NONE = 0;

    /**
     * A border style that causes a single solid monochrome border around an
     * object.
     */
    public static final int STYLE_SOLID = 1;

    /**
     * A border style that causes a simulated 3D border to be rendered, such
     * that an object appears recessed.
     */
    public static final int STYLE_INSET = 2;

    /**
     * A border style that causes a simulated 3D border to be rendered, such
     * that an object appears raised.
     */
    public static final int STYLE_OUTSET = 3;

    /**
     * A border style that causes a simulated 3D border to be rendered, such
     * that the border appears to have been carved out.
     */
    public static final int STYLE_GROOVE = 4;

    /**
     * A border style that causes a simulated 3D border to be rendered, such
     * that the border appears as a ridge around an object.
     */
    public static final int STYLE_RIDGE = 5;

    /**
     * A border style that creates two solid monochrome borders around an
     * object.
     */
    public static final int STYLE_DOUBLE = 6;

    /**
     * A border style that appears as a series of dots.
     */
    public static final int STYLE_DOTTED = 7;

    /**
     * A border style that appears as a series of short line segments.
     */
    public static final int STYLE_DASHED = 8;

    /**
     * A representation of one or more sides of a border.
     */
    public static class Side
    implements Serializable {

        /** Serial Version UID. */
        private static final long serialVersionUID = 20070101L;

        private Extent size;
        private Color color;
        private int style;

        /**
         * Creates a new border <code>Side</code> with a pixel-based size.
         *
         * @param sizePx the size of the border side, in pixels
         * @param color the color of the border side
         * @param style the style of the border side, one of the following constant values:
         *        <ul>
         *         <li><code>STYLE_NONE</code></li>
         *         <li><code>STYLE_SOLID</code></li>
         *         <li><code>STYLE_INSET</code></li>
         *         <li><code>STYLE_OUTSET</code></li>
         *         <li><code>STYLE_GROOVE</code></li>
         *         <li><code>STYLE_RIDGE</code></li>
         *         <li><code>STYLE_DOUBLE</code></li>
         *         <li><code>STYLE_DOTTED</code></li>
         *         <li><code>STYLE_DASHED</code></li>
         *        </ul>
         */
        public Side(int sizePx, Color color, int style) {
            this(new Extent(sizePx), color, style);
        }

        /**
         * Creates a new border <code>side</code>.
         *
         * @param size the size of the border side (this property only supports
         *        <code>Extent</code>s with fixed (i.e., not percent) units)
         * @param color the color of the border side
         * @param style the style of the border side, one of the following constant
         *        values:
         *        <ul>
         *        <li><code>STYLE_NONE</code></li>
         *        <li><code>STYLE_SOLID</code></li>
         *        <li><code>STYLE_INSET</code></li>
         *        <li><code>STYLE_OUTSET</code></li>
         *        <li><code>STYLE_GROOVE</code></li>
         *        <li><code>STYLE_RIDGE</code></li>
         *        <li><code>STYLE_DOUBLE</code></li>
         *        <li><code>STYLE_DOTTED</code></li>
         *        <li><code>STYLE_DASHED</code></li>
         *        </ul>
         */
        public Side(Extent size, Color color, int style) {
            super();
            this.size = size;
            this.color = color;
            this.style = style;
        }

        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Side)) {
                return false;
            }
            Side that = (Side) o;
            if (this.style != that.style) {
                return false;
            }
            if (color == null) {
                if (that.color != null) {
                    return false;
                }
            } else {
                if (!this.color.equals(that.color)) {
                    return false;
                }
            }
            if (size == null) {
                if (that.size != null) {
                    return false;
                }
            } else {
                if (!this.size.equals(that.size)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns the border side color.
         *
         * @return the color
         */
        public Color getColor() {
            return color;
        }

        /**
         * Returns the border side size.
         *
         * @return the size
         */
        public Extent getSize() {
            return size;
        }

        /**
         * Returns the border side style.
         *
         * @return the style
         */
        public int getStyle() {
            return style;
        }
    }

    private Side[] sides;

    /**
     * The class, representing border radius.
     */
    public static class Radius
    implements Serializable {

		/**
		 * Serial version UID
		 */
		private static final long serialVersionUID = 1497957647962847088L;

		/**
		 * Horizontal radius value.
		 */
		private Extent horizontal;

		/**
		 * Vertical radius value. If present, 'elliptical' radius will be drawn.
		 * If not present, the value of horizontal value will be used for both sides (rounded).
		 */
		private Extent vertical;

		/**
		 * Constructs rounded border radius with value of given extent.
		 * @param radius extent to use.
		 */
		public Radius(Extent radius) {
			super();
			this.horizontal = radius;
		}

		/**
		 * Constructs elliptical border radius with value of given horizontal and vertical extents.
		 * @param horizontal horizontal extent to use.
		 * @param vertical vertical extent to use.
		 */
		public Radius(Extent horizontal, Extent vertical) {
			super();
			this.horizontal = horizontal;
			this.vertical = vertical;
		}

		/**
		 * Returns the horizontal radius value.
		 * @return the radius value.
		 */
		public Extent getHorizontalRadius() {
			return horizontal;
		}

		/**
		 * Returns the vertical radius value.
		 * @return the radius value.
		 */
		public Extent getVerticalRadius() {
			return vertical;
		}

		/**
		 * Returns default radius value or null,
		 * if either no radius values was set or this radius is 'elliptical' (has two values).
		 * @return single default radius value.
		 */
		public Extent getRadius() {

			if (isRounded()) {
				return getHorizontalRadius();
			}

			return null;
		}

		/**
		 * Checks, if this radius value is rounded one.
		 * @return true if rounded, false otherwise.
		 */
		public boolean isRounded () {
			return horizontal != null && vertical == null;
		}

		/**
		 * Checks, if this radius value is elliptical one.
		 * @return true if elliptical, false otherwise.
		 */
		public boolean isElliptical() {
			return horizontal != null && vertical != null;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object o) {

			if (this == o) {
				return true;
			}
			if(!(o instanceof Radius)) {
				return false;
			}

			Radius that = (Radius) o;

			if (this.horizontal == null) {
				if (that.horizontal != null) {
					return false;
				}
			} else {
				if (!this.horizontal.equals(that)) {
					return false;
				}
			}
			if (this.vertical == null) {
				if (that.vertical != null) {
					return false;
				}
			} else {
				if (!this.vertical.equals(that.vertical)) {
					return false;
				}
			}

			return true;
		}
    }

    /**
     * Radius properties.
     */
    private Radius[] radii;

    /**
     * Creates a new <code>Border</code> with a pixel-based size.
     *
     * @param sizePx the size of the border, in pixels
     * @param color the color of the border
     * @param style the style of the border, one of the following constant values:
     *        <ul>
     *         <li><code>STYLE_NONE</code></li>
     *         <li><code>STYLE_SOLID</code></li>
     *         <li><code>STYLE_INSET</code></li>
     *         <li><code>STYLE_OUTSET</code></li>
     *         <li><code>STYLE_GROOVE</code></li>
     *         <li><code>STYLE_RIDGE</code></li>
     *         <li><code>STYLE_DOUBLE</code></li>
     *         <li><code>STYLE_DOTTED</code></li>
     *         <li><code>STYLE_DASHED</code></li>
     *        </ul>
     */
    public Border(int sizePx, Color color, int style) {
        this(new Extent(sizePx), color, style);
    }

    /**
     * Creates a new <code>Border</code>.
     *
     * @param size the size of the border (this property only supports
     *        <code>Extent</code>s with fixed (i.e., not percent) units)
     * @param color the color of the border
     * @param style the style of the border, one of the following constant
     *        values:
     *        <ul>
     *        <li><code>STYLE_NONE</code></li>
     *        <li><code>STYLE_SOLID</code></li>
     *        <li><code>STYLE_INSET</code></li>
     *        <li><code>STYLE_OUTSET</code></li>
     *        <li><code>STYLE_GROOVE</code></li>
     *        <li><code>STYLE_RIDGE</code></li>
     *        <li><code>STYLE_DOUBLE</code></li>
     *        <li><code>STYLE_DOTTED</code></li>
     *        <li><code>STYLE_DASHED</code></li>
     *        </ul>
     */
    public Border(Extent size, Color color, int style) {
        this(new Side[] { new Side(size, color, style) });
    }

    /**
     * Constructs a new <code>Border</code> using given sides array.
     * For side definition see {@link Side#Side(Extent, Color, int)}.
     * @param sides the sides array to use.
     */
    public Border(Side[] sides) {
        this (sides, null);
    }

    /**
     * Creates a new <code>Border</code>.
     *
     * @param size the size of the border (this property only supports
     *        <code>Extent</code>s with fixed (i.e., not percent) units)
     * @param color the color of the border
     * @param style the style of the border, one of the following constant
     *        values:
     *        <ul>
     *        <li><code>STYLE_NONE</code></li>
     *        <li><code>STYLE_SOLID</code></li>
     *        <li><code>STYLE_INSET</code></li>
     *        <li><code>STYLE_OUTSET</code></li>
     *        <li><code>STYLE_GROOVE</code></li>
     *        <li><code>STYLE_RIDGE</code></li>
     *        <li><code>STYLE_DOUBLE</code></li>
     *        <li><code>STYLE_DOTTED</code></li>
     *        <li><code>STYLE_DASHED</code></li>
     *        </ul>
     * @param radius default border radius value to use
     */
    public Border(Extent size, Color color, int style, Extent radius) {
    	this(	new Side[] { new Side(size, color, style) },
    			new Radius[] { new Radius(radius) }	);
    }

    /**
     * The full and most flexible border constructor.
     * Constructs a new <code>Border</code> using given sides and radii arrays.
     * For side definition see {@link Side#Side(Extent, Color, int)}
     * For radius definition see {@link Radius#Radius(Extent)}, {@link Radius#Radius(Extent, Extent)}
     *
     * @param sides sides array to use
     * @param radii radii array to use
     */
    public Border(Side[] sides, Radius[] radii) {
    	super();

    	if (sides.length < 1 || sides.length > 4) {
            throw new IllegalArgumentException("Invalid number of border sides: " + sides.length);
        }

        this.sides = sides;

        if (radii != null) {
        	if (radii.length < 1 || radii.length > 4) {
        		throw new IllegalArgumentException("Invalid number of border radius specs: " + radii.length);
        	}

        	this.radii = radii;
        }
    }

    /**
     * Note that this implementation of equals will return FALSE if two borders
     * have a different number of sides but are nevertheless equivalent.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Border)) {
            return false;
        }
        Border that = (Border) o;
        if (this.sides.length != that.sides.length) {
            return false;
        }
        for (int i = 0; i < this.sides.length; ++i) {
            if (!(this.sides[i] == that.sides[i] || (this.sides[i] != null && this.sides[i].equals(that.sides[i])))) {
                return false;
            }
        }
        if (this.radii == null && that.radii != null) {
        	return false;
        } else if (this.radii != null && that.radii == null) {
        	return false;
        } else {

        	if (this.radii != null && that.radii != null) {
	        	if (this.radii.length != that.radii.length) {
	        		return false;
	        	}
	        	for (int i = 0; i < this.radii.length; i++) {
	        		if (!(this.radii[i] == that.radii[i] || (this.radii[i] != null && this.radii[i].equals(that.radii[i])))) {
	        			return false;
	        		}
	        	}
        	}
        }

        return true;
    }

    /**
     * Returns the border color.
     *
     * @return the color
     */
    public Color getColor() {
        return sides[0].getColor();
    }

    /**
     * Returns the border size.
     * This property only supports <code>Extent</code>s with
     * fixed (i.e., not percent) units.
     *
     * @return the size
     */
    public Extent getSize() {
        return sides[0].getSize();
    }

    /**
     * Returns the border style.
     *
     * @return the style, one of the following values:
     *         <ul>
     *          <li><code>STYLE_NONE</code></li>
     *          <li><code>STYLE_SOLID</code></li>
     *          <li><code>STYLE_INSET</code></li>
     *          <li><code>STYLE_OUTSET</code></li>
     *          <li><code>STYLE_GROOVE</code></li>
     *          <li><code>STYLE_RIDGE</code></li>
     *          <li><code>STYLE_DOUBLE</code></li>
     *          <li><code>STYLE_DOTTED</code></li>
     *          <li><code>STYLE_DASHED</code></li>
     *         </ul>
     */
    public int getStyle() {
        return sides[0].getStyle();
    }

    /**
     * Gets default single border radius.
     * @return radius value or null if no radius is set, or radius is not a rounded one. See {@link Radius#getRadius()} for details.
     */
    public Extent getRadius() {
    	if (this.radii != null) {
    		this.radii[0].getRadius();
    	}

    	return null;
    }
    /**
     * Returns the sides of the border. Do not modify; modification of returned array will result in indeterminate behavior.
     *
     * @return the array of sides
     */
    public Side[] getSides() {
        return sides;
    }

    /**
     * Returns the radii of the border. Do not modify; modification of returned array will result in indeterminate behavior.
     *
     * @return the array of radii
     */
    public Radius[] getRadii() {
    	return radii;
    }

    /**
     * Determines if the border is multisided.
     *
     * @return true if the border is multisided
     */
    public boolean isMultisided() {
        return sides.length > 1;
    }

    /**
     * Tests if the border has multiple radius values.
     *
     * @return true if so, false otherwise.
     */
    public boolean isMultiRadius() {
    	return radii != null && radii.length > 1;
    }

    /**
     * Checks if the border has a valid radius.
     * @return true if so, false otherwise
     */
    public boolean hasRadius() {

    	if (radii != null) {
    		for (Radius r : radii) {
    			if (r != null && (r.isRounded() || r.isElliptical())) {
    				return true;
    			}
    		}
    	}

    	return false;
    }
}
