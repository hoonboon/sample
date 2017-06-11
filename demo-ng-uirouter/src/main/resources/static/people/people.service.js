angular.module('hello')
.service('PeopleService', function($http) {
	var service = {
		getAllPeople : function() {
			// notes: do not break line immediately after "return"
			return $http.get('/data/people.json', {cache : true}) 
					.then(function(response) {
						return response.data;
					});
		},
		
		getPerson : function(id) {
			function personMatchesParam(person) {
				return person.id === id;
			}
			
			// notes: do not break line immediately after "return"
			return service.getAllPeople()
					.then(function(people) {
						return people.find(personMatchesParam);
					});
		}
	};
	
	return service;
});