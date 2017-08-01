app.controller('solicitudesController', function usersController($scope, $http){
	$scope.loading=false;
	$scope.filter="";
	$scope.currentPage=1;
	$scope.total=0;
	$scope.pageSize=0;
	$scope.solicitudes=[];
	
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

});