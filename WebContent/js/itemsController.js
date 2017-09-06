app.controller('itemsController', function itemsController($scope, $http){
	$scope.loading=false;
	$scope.items=[];
	$scope.currentPage=1;
	$scope.filter="";
	$scope.total=0;
	$scope.pageSize=0;
	
	$scope.getItems=function(){
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/inventario-web/ItemsServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getItems", nombreItem:$scope.filter, page:$scope.currentPage}),
			responseType:"json"
		}).then(function(response){
		$scope.loading=false;
		$scope.items=response.data.contenido.items;
		$scope.total=response.data.contenido.total;
		$scope.pageSize=response.data.contenido.pageSize;
		},function(){
		});
	};
	$scope.getItems();
	
	
	$scope.doFilter = function($event) {
		var keyCode = $event.keyCode; 
		if (keyCode == 13) { 
			$scope.getItems();
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
			$scope.getItems();
		}
	};
	$scope.prevPage=function(){
		if($scope.isPrevPage()){
			$scope.currentPage-=1;
			$scope.getItems();
		}
	};
	$scope.getItemColor=function(item){
		if(item.cantidadDepto <= item.cantidadMinima){
			return "item-insuficiente";
		}else{
			return "";
		}
	};
	
	 $scope.selectItem=function(item){
			$scope.getSelectedItem().forEach(function(i) {
				i.selected=false;
			});
			
			item.selected=true;
			
	};
	$scope.getSelectedItem=function(){
		return $scope.items.filter(i => i.selected);
	};
	$scope.deleteItem=function(){
		if(!confirm("Esta seguro de que quiere borrar este item?")){
			return;
		}
		$scope.loading=true;
		var idItem=0;
		var arreglo=$scope.getSelectedItem();
		if(arreglo.length===0){
			$scope.loading=false;
			$scope.mensaje={titulo:'Error', mensaje:"debe seleccionar un item ", visible:true, clase: 'alert alert-danger'};
			return;
		}else{
			idItem=arreglo[0].id;
			$scope.mensaje={};
		}
		$http({
			method:'POST',
			url:'/inventario-web/ItemsServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"deleteItem", idItem: idItem}),
			responseType:"json"
		}).then(function(response){
		$scope.loading=false;
		if(response.data.codigo===0){
			$scope.mensaje={titulo:'OK', mensaje:'Item eleminado satisfactoriamente', visible:true, clase: 'alert alert-success'};
			$scope.getItems();
		}else{
			$scope.mensaje={titulo:'Error', mensaje:response.data.razon, visible:true, clase: 'alert alert-danger'};
		}
		
		},function(){
		});	
	};
});