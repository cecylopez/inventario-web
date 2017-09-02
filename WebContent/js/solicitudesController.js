app.controller('solicitudesController', function usersController($scope, $http){
	$scope.loading=false;
	$scope.filter="";
	$scope.currentPage=1;
	$scope.total=0;
	$scope.pageSize=0;
	$scope.solicitudes=[];
	$scope.cantidad;
	$scope.selectedItem={};
	$scope.editMode=false;
	
	$scope.getSolicitudes=function(){
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/inventario-web/SolicitudesServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getSolicitudes", nombreItem:$scope.filter, page:$scope.currentPage}),
			responseType:"json"
		}).then(function(response){
			
			$scope.loading=false;
			$scope.solicitudes=response.data.contenido.solicitudes;
			$scope.total=response.data.contenido.total;
			$scope.pageSize=response.data.contenido.pageSize;
			},function(){
			});
	};
	$scope.getSolicitudes();

	$scope.doFilter = function($event) {
		var keyCode = $event.keyCode; 
		if (keyCode == 13) { 
			$scope.getSolicitudes();
		}
	};
	$scope.isNextPage=function(){
		return ($scope.total/$scope.pageSize)> $scope.currentPage;
	};
	$scope.isPrevPage=function(){
		return $scope.currentPage>1;
	};
	$scope.nextPage=function(){
		if($scope.isNextPage()){
			$scope.currentPage+=1;
			$scope.getSolicitudes();
		}
	};
	$scope.prevPage=function(){
		if($scope.isPrevPage()){
			$scope.currentPage-=1;
			$scope.getSolicitudes();
		}
	};
	 $scope.showAcciones=function(solicitud){
		 return solicitud.estado=="Pendiente";
	 };
	 
	 $scope.selectSolicitud=function(solicitud){
			$scope.getSelectedSolicitud().forEach(function(s) {
				s.selected=false;
			});
			
			solicitud.selected=true;
			
	};
	$scope.getSelectedSolicitud=function(){
		return $scope.solicitudes.filter(s => s.selected);
	};
	$scope.rechazarSolicitud=function(solicitud){
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/inventario-web/SolicitudesServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"rechazarSolicitud", idSolicitud: solicitud.id}),
			responseType:"json"
		}).then(function(response){
			if(response.data.codigo===0){
				$scope.loading=false;
				$scope.mensaje={titulo:'OK', mensaje:response.data.razon, visible:true, clase: 'alert alert-success'};
				$scope.getSolicitudes();
			}else{
				$scope.loading=false;
				$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
				$scope.getSolicitudes();
			}

		},function(){});
		
	};
	
	$scope.aprobarSolicitud=function(solicitud){
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/inventario-web/SolicitudesServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"aprobarSolicitud", idSolicitud: solicitud.id}),
			responseType:"json"
		}).then(function(response){
			if(response.data.codigo===0){
				$scope.loading=false;
				$scope.mensaje={titulo:'OK', mensaje:response.data.razon, visible:true, clase: 'alert alert-success'};
				$scope.getSolicitudes();
			}else{
				$scope.loading=false;
				$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
				$scope.getSolicitudes();
			}
		},function(){});	
	};
	
	$scope.deleteSolicitud=function(){
		if(!confirm("Esta seguro de que quiere eliminar esta solicitud? ")){
			return;
		}
		var idSolicitud=0;
		$scope.loading=true;
		var arreglo=$scope.getSelectedSolicitud();
		if(arreglo.length===0){
			$scope.loading=false;
			$scope.mensaje={titulo:'Error', mensaje:"debe seleccionar una solicitud ", visible:true, clase: 'alert alert-danger'};
			return;
		}else{
			idSolicitud=arreglo[0].id;
			$scope.mensaje={};
		}
		$http({
			method:'POST',
			url:'/inventario-web/SolicitudesServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"deleteSolicitud", idSolicitud: idSolicitud}),
			responseType:"json"
		}).then(function(response){
		$scope.loading=false;
		if(response.data.codigo===0){
			$scope.mensaje={titulo:'OK', mensaje:'Solicitud eleminada satisfactoriamente', visible:true, clase: 'alert alert-success'};
			$scope.getSolicitudes();
		}else{
			$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
		}
		
		},function(){
		});
	};
	$scope.showModal=function(mode){
		$scope.editMode=(mode=='edit');
		var item={};
		$scope.loading=true;
		$scope.tituloModal="Agregar Solicitud";
		$scope.items=[];
		$http({
			method:'POST',
			url:'/inventario-web/ItemsServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getAllItems"}),
			responseType:"json"
		}).then(function(response){
			$scope.loading=false;
			$scope.items = response.data.contenido.items;
			if(mode=='edit'){
				item=$scope.getSelectedSolicitud()[0];
				for(var i=0; i<$scope.items.length; i++){
					if($scope.items[i].id==item.itemId){
						$scope.selectedItem=$scope.items[i];
						break;
					}
				}
			}
			//$('#cmbItems').select2();
		},function(){
		});
		var solicitud=$scope.getSelectedSolicitud()[0];		
		if(mode=='edit'){
				$scope.tituloModal="Modificar Solicitud";
				var arreglo=$scope.getSelectedSolicitud();
				if(arreglo.length===0){
					$scope.loading=false;
					$scope.mensaje={titulo:'Error', mensaje:"debe seleccionar una aplicacion", visible:true, clase: 'alert alert-danger'};
					return;
				}else{
					item=arreglo[0];
					$scope.mensaje={};
				}
				$scope.cantidad=item.cantidad;
			}
			
		$("#agregarModal").modal("show");
	};
	
	$scope.addSolicitud=function(){
		var operacion="addSolicitud";
		var param=jQuery.param({opt:operacion, cantidad: $scope.cantidad, itemId: $scope.selectedItem.id});
		if($scope.editMode){
			var solicitud=$scope.getSelectedSolicitud()[0];
			operacion="modifySolicitud";
			param=jQuery.param({opt:operacion, solicitudId: solicitud.id, cantidad: $scope.cantidad});
		}
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/inventario-web/SolicitudesServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:param,
			responseType:"json"
		}).then(function(response){
			$scope.loading=false;
			if(response.data.codigo===0){
				$scope.mensaje={titulo:'OK', mensaje:'Solicitud agregada satisfactoriamente', visible:true, clase: 'alert alert-success'};
				$scope.cantidad="";
				$scope.selectedItem="";
				$scope.getSolicitudes();
				$scope.cerrar();

			}else{
				$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
			}
			
		},function(){
		});
	};
	

	$scope.cerrar=function(){
		$scope.selectedItem="";
		$scope.cantidad="";
	};
});