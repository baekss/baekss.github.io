//delegatedAddEventListener of EventTarget has a addEventListener of EventTarget
EventTarget.prototype.delegatedAddEventListener = EventTarget.prototype.addEventListener;

//addEventListener of EventTarget has a function which call delegatedAddEventListener
EventTarget.prototype.addEventListener = function(triggerEvent, listener, useCapture){
	this.triggerEventAndListener = this.triggerEventAndListener||{};
	this.triggerEventAndListener[triggerEvent] = listener;
	this.delegatedAddEventListener(triggerEvent, listener, useCapture);
};
var eventProperties = {
						  blur : "blur"
						, focus : "focus"
						, focusin : "focusin"
						, focusout : "focusout"
						, resize : "resize"
						, scroll : "scroll"
						, click : "click"
						, dblclick : "dblclick"
						, mousedown : "mousedown"
						, mouseup : "mouseup"
						, mousemove : "mousemove"
						, mouseover : "mouseover"
						, mouseout : "mouseout"
						, mouseenter : "mouseenter"
						, mouseleave : "mouseleave"
						, change : "change"
						, select : "select"
						, submit : "submit"
						, keydown : "keydown"
						, keypress : "keypress"
						, keyup : "keyup"
						, contextmenu : "contextmenu"
					  };

function ElementScan(elements){
	this.elements = elements;
	
	this.getElements = function(){
		return this.elements;
	}
}

function AopScan(elementScan, eventPropertyNames, eventAdviceFunctions){
	var context = this;
	context.adviceFunctions = eventAdviceFunctions||{before:undefined, after:undefined, eFunc:undefined, finallyFunc:undefined};
	var targetElements = elementScan.getElements();
	
	context.injectProxy = function(targetElement){
		var triggerEventAndListener = targetElement["triggerEventAndListener"];
		eventPropertyNames.forEach(function(propertyName, i){
			var proceed = triggerEventAndListener[propertyName];
			if(typeof proceed === "function"){
				targetElement.removeEventListener(propertyName, proceed);
				targetElement.addEventListener(propertyName, function(event){
					context.advice.call(context, proceed.bind(this, event));
				});
			}
		});
	}
	
	context.advice = function(targetFunction){
		try{
			if(typeof context.adviceFunctions.before === "function"){
				context.adviceFunctions.before();
			}
			if(typeof targetFunction === "function"){
				targetFunction();
			}
			if(typeof context.adviceFunctions.after === "function"){
				context.adviceFunctions.after();
			}
		}catch(e){
			if(typeof context.adviceFunctions.eFunc === "function"){
				context.adviceFunctions.eFunc(e);
			}
		}finally{
			if(typeof context.adviceFunctions.finallyFunc === "function"){
				context.adviceFunctions.finallyFunc();
			}
		}
	}
	targetElements.forEach(function(v, i){
		context.injectProxy(v);
	});
}