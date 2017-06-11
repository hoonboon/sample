angular.module('hello')
.component('personComponent', {
	bindings : { person : '<' },
	
	templateUrl : '/people/person.template.html'
});
