var app = angular.module('app', []);

app.controller('loginController', function loginController($scope){
	$scope.usr={nombre:'', clave:''};
	$scope.error={titulo:'', mensaje:'', visible:false};
	$scope.login=function(){
		$http({
			method:'POST',
			url: '/inventario-web/LoginServlet/login',
			data:{usuario: $scope.usr.nombre, clave: $scope.usr.clave}
		}).then(function(){}, function(){});
	
	};
	
});
