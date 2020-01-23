function ElementScan(elements){
	this.elements = elements;
	
	this.getElements = function(){
		return this.elements;
	}
}

function AopScan(elementScan, before, after, e, final){
	var context = this;
	var targetElements = elementScan.getElements();
	
	context.injectProxy = function(targetElement){
		var events = $._data(targetElement.get(0), "events");
		for(var property in events){
			switch(property){
				case 'change' : 
					//This logic use a loop syntax because a element may has many event handler in same property name
					//But it assume that a element has one event handler in same property name in this study
					(events[property]).forEach(function(v,i){
						var proceed = v.handler;
						targetElement.off(property);
						targetElement.on(property, function(event){
							context.advice.call(context, before, proceed.bind(this, event), after, e, final);
						});
					});
					break;
				case 'keyup' : 
					(events[property]).forEach(function(v,i){
						var proceed = v.handler;
						targetElement.off(property);
						targetElement.on(property, function(event){
							context.advice.call(context, before, proceed.bind(this, event), after, e, final);
						});
					});
					break;
				//You can add more case syntax with any event
			}
		}
	}
	
	context.advice = function(beforeFunction, targetFunction, afterFunction, exceptionFunction, finallyFunction){
		try{
			if(typeof beforeFunction === 'function'){
				beforeFunction();
			}
			if(typeof targetFunction === 'function'){
				targetFunction();
			}
			if(typeof afterFunction === 'function'){
				afterFunction();
			}
		}catch(e){
			if(typeof exceptionFunction === 'function'){
				exceptionFunction();
			}
		}finally{
			if(typeof finallyFunction === 'function'){
				finallyFunction();
			}
		}
	}
	targetElements.forEach(function(v,i){
		context.injectProxy(v);
	})
}