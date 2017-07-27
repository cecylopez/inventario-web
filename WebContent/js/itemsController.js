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
});