app.controller('usersController', function usersController($scope, $http){
	$scope.loading=false;
	$scope.users=[];
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