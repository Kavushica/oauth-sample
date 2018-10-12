/**
 * 
 */

app.config(function($stateProvider,$urlRouterProvider,$locationProvider){
	
	$urlRouterProvider.otherwise('/');
	
	$stateProvider
	.state('login',{
		url:"/login",
		templateUrl:"templates/login.html",
		controller:'LoginController'
		
	})
	.state('validate',{
		url:"/validate?code",
		templateUrl:"templates/validate.html",
		controller:'ValidateController'
	})
	.state('profile',{
		url:"/profile",
		templateUrl:"templates/profile.html",
		controller:'ProfileController'
	})
	
	
	$locationProvider.html5Mode(true);
});

