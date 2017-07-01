var app = angular.module('app', []);

app.controller('loginController', function loginController($scope, $http){
	$scope.usr={nombre:'', clave:''};
	$scope.error={titulo:'', mensaje:'', visible:false};
	$scope.loading=false
	$scope.login=function(){
		$scope.loading=true;
		$http({
			method:'POST',
			url: '/inventario-web/LoginServlet',
		    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"logIn",usuario: $scope.usr.nombre, clave: $scope.usr.clave}),
			responseType:"json"
		}).then(function(response){ 
			if(response.data.codigo===0){
				window.location.href='/inventario-web/site/index.html';
			}else{
				$scope.error={titulo:'Error', mensaje:response.data.razon, visible:true};
			}
			$scope.loading=false;
		}, function(){
			$scope.loading=false;
		});
	
	};
	
});
