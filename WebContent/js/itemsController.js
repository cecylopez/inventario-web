app.controller('itemsController', function itemsController($scope, $http){
	$scope.loading=false;
	$scope.items=[];
	$scope.currentIndex=0;
	$scope.filter="";
	
	$scope.geItems=function(){
		$scope.loading=true;
		$http({
			method:'POST',
			url:'/inventario-web/ItemsServlet',
			headers:{'Content-Type': 'application/x-www-form-urlencoded'},
			data:jQuery.param({opt:"getItems", nombreItem:$scope.filter, startIndex:$scope.currentIndex}),
			responseType:"json"
		}).then(function(response){
		$scope.loading=false;
		$scope.items=response.data.contenido.items;
		},function(){
		});
	};
	$scope.geItems();
});