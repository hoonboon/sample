var myApp = angular.module('hello', ['ui.router']);

myApp.config(function($stateProvider) {
	var helloState = {
		name : 'hello',
		url : '/hello',
		component: 'helloComponent'
	};
	$stateProvider.state(helloState);
	
	var aboutState = {
		name : 'about',
		url : '/about',
		component : 'aboutComponent'
	};
	$stateProvider.state(aboutState);
	
	var peopleState = {
		name : 'people',
		url : '/people',
		component : 'peopleComponent',
		resolve : {
			people : function(PeopleService) {
				return PeopleService.getAllPeople();
			}
		}
	};
	$stateProvider.state(peopleState);
	
	var personState = {
		name : 'person',
		url : '/people/{personId}',
		component : 'personComponent',
		resolve : {
			person : function(PeopleService, $transition$) {
				return PeopleService.getPerson($transition$.params().personId);
			}
		}
	};
	$stateProvider.state(personState);
	
});
