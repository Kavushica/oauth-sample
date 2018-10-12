/**
 * 
 */
app.controller('ProfileController',['$scope','fbService',function($scope,fbService){
 
	$scope.feeds=[];
	
	var loadFeeds = function(){
		fbService.getFeeds().then(function(responce){
			$scope.profile=responce.data[0];
		},function(error){
			console.log("error while retreiving");
		})
	}
	
	loadFeeds();
	
}]);