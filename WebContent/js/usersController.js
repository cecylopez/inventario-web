app.controller('usersController', function usersController($scope, $http){
	$scope.loading=false;
	$scope.rol={}
	$scope.departamento={};
	$scope.users=[];
	$scope.roles=[];
	$scope.departamentos=[];
	$scope.filtroUsuarios="";
	$scope.tituloModal="";
	$scope.menu="";
	
	$scope.showModal=function(mode){
		$scope.tituloModal="Agregar Usuario";
		var user={};
		$scope.roles=[];
		$scope.departamentos=[];
		$http({
			method:'POST',
			url:'/UsuariosServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getRoles"}),
			responseType:"json"
		}).then(function(response){
		$scope.roles = response.data.contenido.roles;
		if(mode=='edit'){
			user=$scope.getSelectedUser()[0];
			for(var i=0; i<$scope.roles.length; i++){
				if($scope.roles[i].nombre==user.rol){
					$scope.rol=$scope.roles[i];
					break;
				}
			}
		}
		},function(){
		});
		$http({
			method:'POST',
			url:'/UsuariosServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getDepartamentos"}),
			responseType:"json"
		}).then(function(response){
		$scope.departamentos = response.data.contenido.departamentos;
		if(mode=='edit'){
			user=$scope.getSelectedUser()[0];
			for(var i=0; i<$scope.departamentos.length; i++){
				if($scope.departamentos[i].nombre==user.departamento){
					$scope.departamento=$scope.departamentos[i];
					break;
				}
			}
		}
		},function(){
		});
		if(mode=='edit'){
			$scope.tituloModal="Modificar Usuario";
			
			var arreglo=$scope.getSelectedUser();
			if(arreglo.length===0){
				$scope.loading=false;
				$scope.mensaje={titulo:'Error', mensaje:"debe seleccionar un usuario", visible:true, clase: 'alert alert-danger'};
				return;
			}else{
				user=arreglo[0];
				$scope.mensaje={};
			}
			$scope.nombre=user.nombre;
			$scope.clave=user.clave;	
		}
		$("#agregarModal").modal("show");
	};
	
	
	$scope.getUsers=function(){
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/UsuariosServlet',
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
		var operacion="addUsuario";
		var param=jQuery.param({opt:operacion, nombre: $scope.nombre, clave: $scope.clave, rol: $scope.rol.id, departamento: $scope.departamento.id});
		if($scope.tituloModal=="Modificar Usuario"){
			var user=$scope.getSelectedUser()[0];
			operacion="modifyUsuario";
			param=jQuery.param({opt:operacion,id: user.id, nombre: $scope.nombre, clave: $scope.clave, rol: $scope.rol.id, departamento: $scope.departamento.id});
		}
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/UsuariosServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:param,
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
			$scope.mensaje={};
		}
		$http({
			method:'POST',
			url:'/UsuariosServlet',
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

