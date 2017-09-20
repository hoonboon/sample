angular.module('hello')
.component('aboutComponent', {
    templateUrl : '/about/about.template.html'
})
.directive('myDialog', function() {
    return {
        restrict: 'E',
        transclude: true,
        scope: {
            'close': '&onClose'
        },
        templateUrl: '/about/about.my-dialog.html'
    };
})
.directive('entering', function() {
    return function(scope, element) {
        element.bind('mouseenter', function() {
            scope.$apply("about.mouseIn()");
        });
    };

})
.directive('clicking', function() {
    return function(scope, element) {
        element.bind('click', function() {
            scope.$apply("about.mouseClick()");
        });
    };

});

function AboutCtrl ($timeout) {
    var self = this;
    
    self.name = 'Tobias';
    self.message = '';
    self.hideDialog = function(message) {
        self.message = message;
        self.dialogIsHidden = true;
        $timeout(function() {
            self.message = '';
            self.dialogIsHidden = false;
        }, 2000);
    };
    
    self.mouseIn = function() {
        alert('Mouse in!');
    };
    
    self.mouseClick = function() {
        alert('Mouse clicked!');
    };
}

angular.module('hello')
.controller('AboutCtrl', AboutCtrl)
;
