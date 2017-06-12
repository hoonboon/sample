
angular.module('hello')
.component('npeopleComponent', {
	bindings : { people : '<' },
	
	templateUrl : '/people/npeople.template.html'
});
