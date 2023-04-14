'use strict'

angular.module('lithium').controller('GoGameDebugResultsAddController', ['$translate', '$uibModal', '$userService', '$filter', '$state', '$scope', 'errors', 'notify', '$q', 'GoGameGamesRest',
	function($translate, $uibModal, $userService, $filter, $state, $scope, errors, notify, $q, gogameGamesRest) {
		var controller = this;
		controller.model = {engineId: undefined, mathModelRevisionId: undefined};
		var engineMathModelRevisions=[];
		controller.fields = [
			{
				key: 'debugResultId',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Debug Result ID',
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '1',
					max: '',
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.ID.TITLE" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.ID.DESC" | translate'
				},
			}, {
				key: 'engineId',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Engine',
					description: 'Choose the engine that you\'re creating the debug result for',
					required: true,
					valueProp: 'id',
					labelProp: 'id',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder: '',
					options: [],
					onChange: function() {
						engineMathModelRevisions=[];
						gogameGamesRest.findMathModelRevisionsByEngine(controller.model.engineId).then(function (response) {
							let r = response.plain();
							for (let i = 0; i < r.length; i++) {
								engineMathModelRevisions.push({id: r[i].id, name: r[i].id + " - " + r[i].name});
							}
							controller.model.mathModelRevisionId=undefined;
						});

					},
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.ENGINE.TITLE" | translate',
					'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.ENGINE.PLACEHOLDER" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.ENGINE.DESC" | translate'
				},
				controller: ['$scope', function($scope) {
					gogameGamesRest.findAllEngines().then(function(response) {
						$scope.to.options = response.plain();
					});
				}]
			}, {
				key: 'mathModelRevisionId',
				type: 'ui-select-single',
				templateOptions: {
					label: 'Math Model Revision',
					description: 'Choose the math model revision. The purpose of this is to populate the debug result with appropriate reels setup and other configuration properties',
					required: true,
					valueProp: 'id',
					labelProp: 'name',
					optionsAttr: 'ui-options', "ngOptions": 'ui-options',
					placeholder: '',
					options: [],
					focus: true
				},
				expressionProperties: {
					// 'templateOptions.label': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.MATH_MODEL.TITLE" | translate',
					// 'templateOptions.placeholder': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.MATH_MODEL.PLACEHOLDER" | translate',
					// 'templateOptions.description': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.MATH_MODEL.DESC" | translate',
					'templateOptions.options': function() {
						return engineMathModelRevisions;
					},
				}
			}, {
				key: "totalPlay",
				type: "ui-money-mask",
				optionsTypes: ['editable'],
				templateOptions : {
					label: "Total Play",
					description: "",
					addFormControlClass: true,
					required: true
				},
				expressionProperties: {
				'templateOptions.label': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.TOTAL_PLAY.TITLE" | translate',
				'templateOptions.description': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.TOTAL_PLAY.DESC" | translate',
				}
			}, {
				key: 'winCents',
				type: 'ui-number-mask',
				optionsTypes: ['editable'],
				templateOptions: {
					label: 'Win Cents',
					description: "Total win cents in the result below",
					decimals: 0,
					hidesep: true,
					neg: false,
					min: '0',
					max: '',
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.WIN_CENTS.TITLE" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.WIN_CENTS.DESC" | translate'
				}
			}, {
				key: "result",
				type: "textarea",
				templateOptions: {
					label: 'Result',
					description: "The JSON response that needs to be passed to the game",
					placeholder: "",
					required: true
				},
				expressionProperties: {
					'templateOptions.label': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.RESULT.TITLE" | translate',
					'templateOptions.description': '"UI_NETWORK_ADMIN.GOGAME.DEBUG_RESULTS.ADD.FIELDS.RESULT.DESC" | translate'
				}
			}
		];
		
		controller.onSubmit = function() {
			if (controller.form.$invalid) {
				angular.element("[name='" + controller.form.$name + "']").find('.ng-invalid:visible:first').focus();
				notify.warning("GLOBAL.RESPONSE.FORM_ERRORS");
				return false;
			}
			
			gogameGamesRest.addDebugResult(controller.model).then(function(response) {
				console.log(response);
				if (response._status === 0) {
					notify.success('Successfully added debug result');
					$state.go("dashboard.gogamegames.debugresults.debugresult", {
						id:response.debugResultId.debugResultId,
						engineId:response.debugResultId.engine.id,
						mathModelRevisionId:response.debugResultId.mathModelRevision.id
					});
				} else {
					notify.warning(response._message);
				}
			}).catch(function(error) {
				notify.error("Could not save debug result");
				errors.catch('', false)(error)
			});
		}
	}
]);