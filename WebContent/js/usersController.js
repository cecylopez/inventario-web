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
	$scope.addUsuario=function(){
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/inventario-web/UsuariosServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"addUsuario", nombre: $scope.nombre, clave: $scope.clave, rol: $scope.rol.id, departamento: $scope.departamento.id}),
			responseType:"json"
		}).then(function(response){
		$scope.loading=false;
		if(response.data.codigo===0){
			$scope.mensaje={titulo:'OK', mensaje:'Usuario fue agregado satisfactoriamente', visible:true, clase: 'alert alert-success'};
			$scope.nombre="";
			$scope.clave="";
		}else{
			$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
		}
				
		},function(){
		});
	};
	$scope.cerrar=function(){
		$scope.nombre="";
		$scope.clave="";
	};
	$scope.selectUser=function(usuario){
		$scope.getSelectedUser().forEach(function(u) {
			u.selected=false;
		});
		
		usuario.selected=true;
		
	};
	$scope.getSelectedUser=function(){
		return $scope.users.filter(u => u.selected);
	};
	$scope.getUsers();
	
	$scope.deleteUsuario=function(){
		if(!confirm("Esta seguro que desea eliminar este usuario? ")){
			return;
		}
		var userId=0;
		$scope.loading=true;
		var arreglo=$scope.getSelectedUser();
		if(arreglo.length===0){
			$scope.loading=false;
			$scope.mensaje={titulo:'Error', mensaje:"debe seleccionar un usuario", visible:true, clase: 'alert alert-danger'};
			return;
		}else{
			userId=arreglo[0].id;
		}
		$http({
			method:'POST',
			url:'/inventario-web/UsuariosServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"deleteUsuario", userId: userId}),
			responseType:"json"
		}).then(function(response){
		$scope.loading=false;
		if(response.data.codigo===0){
			$scope.mensaje={titulo:'OK', mensaje:'Usuario fue eliminado satisfactoriamente', visible:true, clase: 'alert alert-success'};
			$scope.getUsers();
		}else{
			$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
		}
		
		},function(){
		});
	};
});

