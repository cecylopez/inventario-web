app.controller('usersController', function usersController($scope, $http){
	$scope.loading=false;
	$scope.rol={}
	$scope.departamento={};
	$scope.users=[];
	$scope.roles=[];
	$scope.departamentos=[];
	
	$scope.showModal=function(){
		$scope.roles=[];
		$scope.departamentos=[];
		$http({
			method:'POST',
			url:'/inventario-web/UsuariosServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getRoles"}),
			responseType:"json"
		}).then(function(response){
		$scope.roles = response.data.contenido.roles;
		},function(){
		});
		$http({
			method:'POST',
			url:'/inventario-web/UsuariosServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getDepartamentos"}),
			responseType:"json"
		}).then(function(response){
		$scope.departamentos = response.data.contenido.departamentos;
		},function(){
		});
		$("#agregarModal").modal("show");
	};
	
	
	$scope.getUsers=function(){
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/inventario-web/UsuariosServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getUsers"}),
			responseType:"json"
		}).then(function(response){
		$scope.loading=false;
		$scope.users = response.data.contenido.usuarios;
		},function(){
		});
	};
	
	$scope.getUsers();
});