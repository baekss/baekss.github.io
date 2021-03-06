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
		var events = $._data(targetElement.get(0), "events");
		eventPropertyNames.forEach(function(propertyName, i){
			
			if(typeof events[propertyName] !== "undefined"){
				(events[propertyName]).forEach(function(v,i){
					var proceed = v.handler;
					targetElement.off(propertyName);
					targetElement.on(propertyName, function(event){
						context.advice.call(context, proceed.bind(this, event));
					});
				});
			}
			
		});
	}
	
	context.advice = function(targetFunction){
		try{
			if(typeof context.adviceFunctions.before === 'function'){
				context.adviceFunctions.before();
			}
			if(typeof targetFunction === 'function'){
				targetFunction();
			}
			if(typeof context.adviceFunctions.after === 'function'){
				context.adviceFunctions.after();
			}
		}catch(e){
			if(typeof context.adviceFunctions.eFunc === 'function'){
				context.adviceFunctions.eFunc(e);
			}
		}finally{
			if(typeof context.adviceFunctions.finallyFunc === 'function'){
				context.adviceFunctions.finallyFunc();
			}
		}
	}
	targetElements.forEach(function(v, i){
		context.injectProxy(v);
	});
}