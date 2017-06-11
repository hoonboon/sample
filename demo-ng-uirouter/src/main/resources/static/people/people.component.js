
angular.module('hello')
.component('peopleComponent', {
	bindings : { people : '<' },
	
	templateUrl : '/people/people.template.html'
});
