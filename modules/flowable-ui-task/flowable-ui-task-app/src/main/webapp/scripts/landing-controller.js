/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
flowableApp.controller('LandingController', ['$scope','$window', '$location', '$http', '$translate', '$modal', 'RuntimeAppDefinitionService', '$rootScope',
    function ($scope, $window, $location, $http, $translate, $modal, RuntimeAppDefinitionService, $rootScope) {

        $scope.model = {
          loading: true,
		  dashList:[]
        };

        $translate('APP.ACTION.DELETE').then(function(message) {
            $scope.appActions = [
                {
                    text: message,
                    click: 'deleteApp(app); '
                }
            ];
        });

        $scope.loadApps = function() {
            $scope.model.customAppsFetched = false;
            RuntimeAppDefinitionService.getApplications().then(function(result){
                $scope.model.apps = result.defaultApps.concat(result.customApps);
                $scope.model.customAppsFetched = true;
                $scope.model.customApps = result.customApps.length > 0;

                // Determine the full url with a context root (if any)
                var baseUrl = $location.absUrl();
                var index = baseUrl.indexOf('/#');
                if (index >= 0) {
                    baseUrl = baseUrl.substring(0, index);
                }
                index = baseUrl.indexOf('?');
                if (index >= 0) {
                    baseUrl = baseUrl.substring(0, index);
                }
                if (baseUrl[baseUrl.length - 1] == '/') {
                    baseUrl = baseUrl.substring(0, baseUrl.length - 1);
                }

                $scope.urls = {
                    workflow: baseUrl + '/workflow/'
                };

				
				//GET INFO FOR DASH
				var idx = 0;
				$scope.labels = [];
				$scope.data = [];
				$scope.type = "pie";
				$scope.options = {
					title: {
						display: true,
						text: 'Processos Activos'
					},
					legend: {
						display: true,
						position: 'bottom'
					}
				};
				
				for(;idx<$scope.model.apps.length;idx++){
					if(!!$scope.model.apps[idx].deploymentKey){
						$scope.appGetProcessInstance($scope.model.apps[idx].deploymentKey);	
					}
				}

            })
        };
		
		$scope.appGetProcessInstance = function(_deploymentKey) {
			var params = {sort: "created-desc", page: 0, deploymentKey: _deploymentKey, state: "running"};
            $http({method: 'POST', url: FLOWABLE.CONFIG.contextRoot + '/app/rest/query/process-instances',data:params})
			.success(function(response, status, headers, config) {
				if(response.total > 0){
					var idx = 0, _appName="";
					for(;idx<response.total;idx++){
						if(!!response.data[idx].id){
							$scope.appGetTask(response.data[idx].id,response.data[idx].name,_deploymentKey);
							_appName = response.data[idx].processDefinitionName;
						}
					}
					$scope.labels.push(_appName);
					$scope.data.push(response.total);
				}
			}).error(function(response, status, headers, config) {
					console.log('Something went wrong: ' + response);
			});
        };
		
		$scope.appGetTask = function(_processInstanceId, _processName,_deploymentKey) {
			var params = {processInstanceId: _processInstanceId};
            $http({method: 'POST', url: FLOWABLE.CONFIG.contextRoot + '/app/rest/query/tasks',data:params})
			.success(function(response, status, headers, config) {
				if(response.total > 0){
					var i = 0, _etapas = "";
					for(;i < response.total; i++){
						$scope.model.dashList.push({processName:_processName,processDefinitionName:response.data[i].processDefinitionName, deploymentKey:_deploymentKey, name:response.data[i].name, assignee:(response.data[i].assignee==null)?"??":response.data[i].assignee.fullName, created:response.data[i].created});
					}
				}
			}).error(function(response, status, headers, config) {
					console.log('Something went wrong: ' + response);
			});
        };
		

        $scope.appSelected = function(app) {
            if(app.fixedUrl) {
                $window.location.href = app.fixedUrl;
            }
        };

        $scope.addAppDefinition = function() {

            _internalCreateModal({
                template: 'views/modal/add-app-definition-modal.html',
                scope: $scope
            }, $modal, $scope);
        };


        $scope.deleteApp = function(app) {
            if(app && app.id) {
                RuntimeAppDefinitionService.deleteAppDefinition(app.id).then(function() {
                    $rootScope.addAlertPromise($translate('APP.MESSAGE.DELETED'), 'info')

                    // Remove app from list
                    var index = -1;
                    for(var i=0; i< $scope.model.apps.length; i++) {
                        if($scope.model.apps[i].id == app.id) {
                            index = i;
                            break;
                        }
                    }

                    if(index >= 0) {
                        $scope.model.apps.splice(index, 1);
                    }
                });
            }
        };

        $scope.loadApps();
    }]
);
