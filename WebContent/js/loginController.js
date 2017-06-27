var app = angular.module('app', []);

app.controller('loginController', function loginController($scope, $http){
	$scope.usr={nombre:'', clave:''};
	$scope.error={titulo:'', mensaje:'', visible:false};
	$scope.login=function(){
		$http({
			method:'POST',
			url: '/inventario-web/LoginServlet',
		    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"logIn",usuario: $scope.usr.nombre, clave: $scope.usr.clave}),
			responseType:"json"
		}).then(function(response){ console.log(response.data);}, function(){});
	
	};
	
});
