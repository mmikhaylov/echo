/**
 * @class Default wait indicator implementation.
 */
CustomWaitIndicator = Core.extend(Echo.Client.WaitIndicator, {

    $static: {
    
        boot: function(client) {
            client.setWaitIndicator(new CustomWaitIndicator());
        }
    },
    
    $load: function() {
        Echo.Boot.addInitMethod(this.boot);
    },

    $construct: function() {
        this._divElement = document.createElement("div");
        this._divElement.style.cssText = "display: none; z-index: 32000; position: absolute; top: 30px; right: 30px; width: 200px;"
                 + " padding: 20px; border: 1px outset #000000; background-color: #000000; color: #00ff00; text-align: center;";
        this._divElement.appendChild(document.createTextNode("LOADING"));
        this._fadeRunnable = new Core.Web.Scheduler.MethodRunnable(Core.method(this, this._tick), 50, true);
        document.body.appendChild(this._divElement);
    },
    
    activate: function() {
        this._divElement.style.display = "block";
        Core.Web.Scheduler.add(this._fadeRunnable);
        this._opacity = 0;
    },
    
    deactivate: function() {
        this._divElement.style.display = "none";
        Core.Web.Scheduler.remove(this._fadeRunnable);
    },
    
    _tick: function() {
        ++this._opacity;
        // Formula explained:
        // this._opacity starts at 0 and is incremented forever.
        // First operation is to modulo by 40 then subtract 20, result ranges from -20 to 20.
        // Next take the absolute value, result ranges from 20 to 0 to 20.
        // Divide this value by 30, so the range goes from 2/3 to 0 to 2/3.
        // Subtract that value from 1, so the range goes from 1/3 to 1 and back.
        var opacityValue = 1 - ((Math.abs((this._opacity % 40) - 20)) / 30);
        if (!Core.Web.Env.PROPRIETARY_IE_OPACITY_FILTER_REQUIRED) {
            this._divElement.style.opacity = opacityValue;
        }
    }
});
