app.controller('itemsController', function itemsController($scope, $http) {
	$scope.loading = false;
	$scope.items = [];
	$scope.currentPage = 1;
	$scope.filter = "";
	$scope.total = 0;
	$scope.pageSize = 0;

	$scope.getItems = function() {
		$scope.loading = true;
		$http({
			method : 'POST',
			url : '/inventario-web/ItemsServlet',
			headers : {
				'Content-Type' : 'application/x-www-form-urlencoded'
			},
			data : jQuery.param({
				opt : "getItems",
				nombreItem : $scope.filter,
				page : $scope.currentPage
			}),
			responseType : "json"
		}).then(function(response) {
			$scope.loading = false;
			$scope.items = response.data.contenido.items;
			$scope.total = response.data.contenido.total;
			$scope.pageSize = response.data.contenido.pageSize;
		}, function() {
		});
	};
	$scope.getItems();

	$scope.doFilter = function($event) {
		var keyCode = $event.keyCode;
		if (keyCode == 13) {
			$scope.getItems();
		}
	};
	$scope.isNextPage = function() {
		return ($scope.total / $scope.pageSize) > $scope.currentPage;
	};
	$scope.isPrevPage = function() {
		return $scope.currentPage > 1;
	};
	$scope.nextPage = function() {
		if ($scope.isNextPage()) {
			$scope.currentPage += 1;
			$scope.getItems();
		}
	};
	$scope.prevPage = function() {
		if ($scope.isPrevPage()) {
			$scope.currentPage -= 1;
			$scope.getItems();
		}
	};
	$scope.getItemColor = function(item) {
		if (item.cantidadDepto <= item.cantidadMinima) {
			return "item-insuficiente";
		} else {
			return "";
		}
	};

	$scope.showSolicitudModal = function(mode, item) {
		$scope.solicitudMode = (mode == 's');
		$scope.tituloModal = "Solicitar Item";
		$scope.itemSolicitado=item;
		if (!$scope.solicitudMode) {
			$scope.tituloModal = "Descartar cantidad de Item";
		}
		$("#solicitarModal").modal("show");
	};
	
	$scope.cerrar=function(){
		$scope.cantidad="";
		$("#solicitarModal").modal("hide");

	};
	
	$scope.aplicarSolicitud=function(){
		var operacion="addSolicitud";
		$scope.mensaje={titulo:'OK', mensaje:'Solicitud agregada satisfactoriamente', visible:true, clase: 'alert alert-success'};
		if(!$scope.solicitudMode){
		operacion="descartarCantidadItem";
		$scope.mensaje={titulo:'OK', mensaje:'Items descartados satisfactoriamente', visible:true, clase: 'alert alert-success'};
		}
		$http({
			method:'POST',
			url:'/inventario-web/SolicitudesServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt: operacion, itemId:$scope.itemSolicitado.id, cantidad:$scope.cantidad}),
			responseType:"json"
		}).then(function(response){
			$scope.loading=false;
			if(response.data.codigo===0){
				$scope.mensaje;
				$scope.getItems();
				$scope.cerrar();

			}else{
				$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
			}
			
		},function(){
		});
		
	};
	
	
	
	

});