app.controller('usersController', function usersController($scope, $http){
	$scope.loading=false;
	$scope.users=[];
	$scope.getUsers=function(){
		$scope.loading=true;
		$scope.users.push({id:0, nombre:"Cecilia", estado:"Activo", rol:"administrador", departamento:"rrhh"});
		$scope.users.push({id:1, nombre:"Roberto", estado:"Inactivo", rol:"usuarioA", departamento:"rrhh"});
		$scope.users.push({id:2, nombre:"Carlos", estado:"Activo", rol:"administrador", departamento:"rrhh"});
		$scope.users.push({id:3, nombre:"Shuberto", estado:"Inactivo", rol:"administrado", departamento:"rrhh"});
		$scope.users.push({id:1000, nombre:"Federico", estado:"Inactivo", rol:"administrador", departamento:"rrhh"});
		$scope.loading=false;
	};
	$scope.getUsers();
});
