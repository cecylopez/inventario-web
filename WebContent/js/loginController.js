var app = angular.module('app', []);

app.controller('loginController', function loginController($scope, $http){
	$scope.usr={id:0, nombre:'', clave:''};
	$scope.mensajer={titulo:'', mensaje:'', visible:false, clase: 'alert alert-danger'};
	$scope.loading=false;
	$scope.cambiarClave=function(){
		$scope.loading = true;
		$http({
			method:'POST',
			url:'/inventario-web/LoginServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"cambiarClave", claveActual: $scope.claveActual, nuevaClave: $scope.nuevaClave, repetirNuevaClave: $scope.repetirNuevaClave}),
			responseType:"json"
		}).then(function(response){
			$scope.loading = false;
			if(response.data.codigo===0){
				$scope.mensaje={titulo:'OK', mensaje:'Clave actualizada satisfactoriamente', visible:true, clase: 'alert alert-success'};
			}else{
				$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
			}
			$scope.claveActual = "";
			$scope.nuevaClave = "";
			$scope.repetirNuevaClave = "";
		},
		function(){
			$scope.loading = false;
			$scope.claveActual = "";
			$scope.nuevaClave = "";
			$scope.repetirNuevaClave = "";
		});
	};
	$scope.getUser=function(){
		$http({
			method:'POST',
			url:'/inventario-web/LoginServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getUser"}),
			responseType:"json"
		}).then(function(response){
			if(response.data.codigo===0){
				$scope.usr.id=response.data.contenido.id;
				$scope.usr.nombre=response.data.contenido.nombre;
				
			}else if(response.data.codigo===0){
					window.location.href='inventario-web/login.html'
			}else{
					$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
			}
			$scope.loading=false;
		},
		function(){
			$scope.loading=false;

		});
	};
	$scope.logOut=function(){
		$http({
			method:'POST',
			url: '/inventario-web/LoginServlet',
		    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"logOut"}),
			responseType:"json"
		}).then(function(response){
			window.location.href='inventario-web/login.html';
		}, function(){
			
		});
	}
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
				$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
			}
			$scope.loading=false;
		}, function(){
			$scope.loading=false;
		});
	
	};
	
});
