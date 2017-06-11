angular.module('hello')
.component('helloComponent', {
	templateUrl : '/hello/hello.template.html',
	
	controller : function() {
		this.greeting = 'Hello!';
		
		this.toggleGreeting = function() {
			this.greeting = (this.greeting == 'Hello!') ? 'What\'s Up' : 'Hello!';
		} 
	}
});