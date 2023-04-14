'use strict';

angular.module('lithium').directive('casinohistory', function() {
  return {
    templateUrl:'scripts/directives/casino/history/history.html',
    scope: {
      inputPlayer: "=?",
      domain: "=?",
      playerTable: "=?"
    },
    restrict: 'E',
    replace: true,
    controllerAs: 'controller',
    controller: [
      '$state', '$filter', '$dt', 'UserRest', 'rest-casino', 'rest-games', '$translate', '$compile', 'DTOptionsBuilder',
      '$scope','$rootScope','$stateParams','DocumentGenerationRest', 'notify',
      function($state, $filter, $dt, userRest, casinoRest, gamesRest, $translate, $compile, DTOptionsBuilder, $scope,
               $rootScope, $stateParams, documentRest, notify) {
        var controller = this;

        controller.selectedUser = $scope.inputPlayer;
        controller.domain = $scope.domain;
        controller.legendCollapsed = true;
        controller.model = {};
        controller.playerTable = $scope.playerTable
        if (!controller.playerTable) {
          controller.model.defaultStartDate = new Date(new Date().setDate(new Date().getDate() - 31));
          controller.model.defaultEndDate = new Date();
        }
        controller.fields = [
          {
            className: 'col-md-4 col-xs-12',
            key: 'betRoundGuid',
            type: 'input',
            expressionProperties: {
              'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETROUNDGUID.LABEL" | translate',
            }
          },
          {
            className: 'col-md-4 col-xs-12',
            key: 'dateRangeStart',
            type: 'datepicker',
            defaultValue: controller.model.defaultStartDate,
            optionsTypes: ['editable'],
            templateOptions: {
              label: 'Date: Range Start',
              required: false,
              datepickerOptions: {
                format: 'dd/MM/yyyy'
              },
              onChange: function() {
                controller.fields[2].templateOptions.datepickerOptions.minDate = controller.model.dateRangeStart;
                if (controller.model.dateRangeEnd !== null & controller.model.dateRangeStart !== null) {
                  var diff = controller.model.dateRangeEnd.getTime() - controller.model.dateRangeStart.getTime();
                  var daydiff = diff / (1000 * 60 * 60 * 24);
                  if (daydiff > 31) {
                    notify.error("Date exceeds 31 days please select a date within a 31 day range");
                    controller.model.dateRangeStart = null
                  }
                }
              }
            },
            expressionProperties: {
              'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.FILTER.START.LABEL" | translate',
            }
          },{
            className: 'col-md-4 col-xs-12',
            key: 'dateRangeEnd',
            type: 'datepicker',
            optionsTypes: ['editable'],
            defaultValue: controller.model.defaultEndDate,
            templateOptions: {
              label: 'Date: Range End',
              required: false,
              datepickerOptions: {
                format: 'dd/MM/yyyy'
              },
              onChange: function() {
                controller.fields[1].templateOptions.datepickerOptions.maxDate = controller.model.dateRangeEnd;
                if (controller.model.dateRangeEnd !== null & controller.model.dateRangeStart !== null) {
                  var diff = controller.model.dateRangeEnd.getTime() - controller.model.dateRangeStart.getTime();
                  var daydiff = diff / (1000 * 60 * 60 * 24);
                  if (daydiff > 31) {
                    notify.error("Date exceeds 31 days please select a date within a 31 day range");
                    controller.model.dateRangeEnd = null
                  }
                }
              }
            },
            expressionProperties: {
              'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.FILTER.END.LABEL" | translate',
            }
          },{
            className: 'col-md-4 col-xs-12',
            key: 'providers',
            type: "ui-select-multiple",
            templateOptions: {
              label: 'Provider',
              description: '',
              placeholder: '',
              valueProp: 'guid',
              labelProp: 'guid',
              optionsAttr: 'ui-options',
              ngOptions: 'ui-options',
              options: []
            },
            expressionProperties: {
              'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.FILTER.PROVIDER.LABEL" | translate',
            },
            controller: ['$scope', function ($scope) {
              if (controller.playerTable) {
                controller.model.domainName = controller.selectedUser.domain.name;
              }
              else {
                controller.model.domainName = controller.domain;
              }
              casinoRest.findProviders(controller.model.domainName).then(function (response) {
                $scope.options.templateOptions.options = response;
                return response;
              });
            }]
          }, {
            className: 'col-md-4 col-xs-12',
            key: 'statuses',
            type: "ui-select-multiple",
            templateOptions: {
              label: 'Status',
              description: '',
              placeholder: '',
              valueProp: 'code',
              labelProp: 'code',
              optionsAttr: 'ui-options',
              ngOptions: 'ui-options',
              options: []
            },
            expressionProperties: {
              'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.FILTER.STATUS.LABEL" | translate',
            },
            controller: ['$scope', function ($scope) {
              casinoRest.findStatus().then(function (response) {
                $scope.options.templateOptions.options = response;
                return response;
              });
            }]
          }, {
            className: 'col-md-4 col-xs-12',
            key: 'games',
            type: "ui-select-multiple",
            templateOptions: {
              label: 'Game',
              description: '',
              placeholder: '',
              valueProp: 'guid',
              labelProp: 'commercialName',
              optionsAttr: 'ui-options',
              ngOptions: 'ui-options',
              options: []
            },
            expressionProperties: {
              'templateOptions.label': '"UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.FILTER.GAME.LABEL" | translate',
            },
            controller: ['$scope', function ($scope) {
              if (controller.playerTable) {
                controller.model.domainName = controller.selectedUser.domain.name;
              }
              else {
                controller.model.domainName = controller.domain;
              }
              gamesRest.list(controller.model.domainName).then(function (response) {
                $scope.options.templateOptions.options = response;
                return response;
              });
            }]
          },
          {
            className: 'col-md-4 col-xs-12',
            key: 'userGuid',
            type: 'input',
            expressionProperties: {
              'templateOptions.label': '"UI_NETWORK_ADMIN.BET_HISTORY.FILTER.USER_GUD.LABEL" | translate',
              'templateOptions.disabled' : function(viewValue, modelValue, scope) {
                return (controller.playerTable);
              },
            }
          }
        ];

        function arrayAsString(arr, fieldName) {
          var str = "";
          angular.forEach(arr, function (d) {
            str += d[fieldName] + ",";
          });
          return str;
        }

        controller.formatDate = function (date) {
          if (!date) {
            return null;
          }
          return $filter('date')(date, 'yyyy-MM-dd');
        }

        controller.toggleLegendCollapse = function() {
          controller.legendCollapsed = !controller.legendCollapsed;
        }

        controller.resetFilter = function(collapse) {
          if (collapse) {
            controller.toggleLegendCollapse();
          }
          controller.model.dateRangeStart = null;
          controller.fields[0].value();
          controller.model.dateRangeEnd = null;
          controller.fields[1].value();
          controller.model.statuses = null;
          controller.model.games = null;
          controller.model.providers = null;
          controller.model.userGuid = null;
          controller.applyFilter(true);
        }

        var filterApplied = false;
        controller.applyFilter = function(toggle) {
          if (toggle === true) {
            controller.toggleLegendCollapse();
          }
          filterApplied = true;
          controller.refresh();
        }
        var baseUrl = "services/service-casino-search/backoffice/bethistory/table?1=1";
        var dtOptions = DTOptionsBuilder.newOptions().withOption('stateSave', false).withOption('order', [[0, 'desc']]).withOption('bFilter', false);
        controller.betsTable = $dt.builder()
          // .column($dt.column('bet.betTransactionId').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETTRANID.LABEL')))
          .column($dt.columnformatdatetime('createdDate').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETDATE.LABEL')))
          .column($dt.column('game.name').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.GAME.LABEL')).notSortable())
          .column($dt.column('game.supplier').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.GAMESUPPLIER.LABEL')).notSortable())
          .column($dt.column('game.category').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.GAMECATEGORY.LABEL')).notSortable())
          .column(
              $dt.labelcolumn(
                  $translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETSTATUS.LABEL'),
                  [{lclass: function(data) {
                      if (data.lastBetResult !== undefined && data.lastBetResult !== null) {
                        switch (data.lastBetResult.betResultKind.code) {
                          case 'WIN':
                          case 'FREE_WIN':
                            return 'danger';
                          case 'LOSS':
                          case 'FREE_LOSS':
                            return 'success';
                          case 'VOID':
                            return 'primary';
                        }
                      } else if (data.complete) {
                        return 'default';
                      } else {
                        return 'warning';
                      }
                    },
                    text: function(data) {
                      if (data.lastBetResult !== undefined && data.lastBetResult !== null) {
                        return data.lastBetResult.betResultKind.code;
                      } else if (data.complete) {
                        return 'CLOSED';
                      } else {
                        return 'OPEN';
                      }
                    },
                    uppercase: true
                  }]
              )
          )
          .column($dt.linkscolumn('',
            [{
              permission: "PLAYER_CASINO_HISTORY_VIEW",
              permissionType: "ANY",
              permissionDomain: controller.domain,
              title: "GLOBAL.ACTION.VIEW",
              href: function(data) {
                return data.roundDetailUrl;
              },
              condition: function(data) {
                return (data.roundDetailUrl != undefined && data.roundDetailUrl !== '');
              },
              target: "_blank"
            }
            ]))
          .column($dt.linkscolumn('',
              [{
                  title: "GLOBAL.ACTION.VIEW",
                  click: function (event){
                      let gameKey = "";
                      let playerId = event.user.guid.split("/")[1];
                      let roundId = event.guid;
                      let domainName = event.provider.domain.name;
                      let providerId = event.provider.guid.split("/")[1];
                      if(event.game.guid.lastIndexOf("_") > -1)
                      {
                        gameKey = event.game.guid.substring(event.game.guid.lastIndexOf("_") + 1);
                      }
                      casinoRest.getRoundReplay(domainName, providerId, gameKey, roundId, playerId).then(function (response) {
                        window.open(response.replayUrl?response.replayUrl:"", "_blank");
                      });
                },
                target: "_blank",
                condition: function(data) {
                  return ((data.roundDetailUrl == undefined || data.roundDetailUrl == '') && data.guid.length > 0);
                },
                permission: "PLAYER_CASINO_HISTORY_VIEW",
                permissionType: "ANY",
                permissionDomain: controller.domain,
              }
          ]))
          .column($dt.columncurrencywithsymbol('betAmount', controller.domain.currency, 2).withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.STAKE.LABEL')).notSortable())
          .column($dt.columncurrencywithsymbol('roundReturnsTotal', controller.domain.currency, 2).withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.RETURN.LABEL')))
          .column(
              $dt.labelcolumn(
                  $translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.ROUNDSTATUS.LABEL'),
                  [{lclass: function(data) {
                      if (data.lastBetResult !== undefined && data.lastBetResult !== null) {
                        if (data.lastBetResult.roundComplete) {
                          return 'success';
                        } else {
                          return 'danger';
                        }
                      } else if (data.complete) {
                        return 'success';
                      } else {
                        return 'danger';
                      }
                    },
                    text: function(data) {
                      if (data.lastBetResult !== undefined && data.lastBetResult !== null) {
                        if (data.lastBetResult.roundComplete) {
                          return 'COMPLETE';
                        } else {
                          return 'NOT COMPLETE';
                        }
                      } else if (data.complete) {
                        return 'COMPLETE';
                      } else {
                        return 'NOT COMPLETE';
                      }
                    },
                    uppercase: true
                  }]
              )
          )
          .column($dt.columnformatdatetime('lastBetResult.transactionTimestamp').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETSETTLEDDATE.LABEL')))
          .column($dt.column('guid').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.BETROUNDGUID.LABEL')))
          .column($dt.column('game.providerGuid').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINOHISTORY.TABLE.GAMEPROVIDER.LABEL')).notSortable())
          .column($dt.column('provider.domain.name').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINO_HISTORY.TABLE.DOMAIN_NAME.LABEL')).notSortable())
          .column($dt.column('playerID').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINO_HISTORY.TABLE.USER_ID.LABEL')).notSortable())
          .column($dt.column('playerGUID').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINO_HISTORY.TABLE.USER_GUID.LABEL')).notSortable())
          .column($dt.column('userName').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINO_HISTORY.TABLE.USER_NAME.LABEL')).notSortable())
          .column($dt.column('lastBetResult.currency.code').withTitle($translate('UI_NETWORK_ADMIN.PLAYER.CASINO_HISTORY.TABLE.CURRENCY_CODE.LABEL')).notSortable())
          .options({
            url:baseUrl,
            type:"POST",
            data: function(d) {
              if (filterApplied) {
                d.start = 0;
                filterApplied = false;
              }
              d.betRoundGuid = controller.model.betRoundGuid;
              if (controller.playerTable) {
                d.userGuid = controller.selectedUser.guid;
                d.domainName = controller.domain.name;
              } else {
                d.userGuid = controller.model.userGuid;
                d.domainName = controller.domain;
              }
              d.dateRangeStart = (controller.model.dateRangeStart !== undefined && controller.model.dateRangeStart !== null) ? controller.formatDate(controller.model.dateRangeStart) : null;
              d.dateRangeEnd = (controller.model.dateRangeEnd !== undefined && controller.model.dateRangeEnd !== null) ? controller.formatDate(controller.model.dateRangeEnd) : null;
              d.statuses = arrayAsString(controller.model.statuses, 'code');
              d.games = arrayAsString(controller.model.games, 'guid');
              d.providers = arrayAsString(controller.model.providers, 'guid');
            }
          }, null, dtOptions, null)
          .build();

        controller.refresh = function() {
          controller.betsTable.instance.reloadData(function(){}, false);
        }
        controller.reference = () => {
          const reference = localStorage.getItem(`export_reference_${$scope.inputPlayer.guid}`)

          if(angular.isUndefinedOrNull(reference)) {
            return null;
          }

          return reference.replace('export_reference_', '')
        }
        $rootScope.provide['csvGeneratorProvider'] = {}

        $rootScope.provide.csvGeneratorProvider.generate = async (config) => {
          const response = await documentRest.generateDocument({
            ...config,
          });

          localStorage.setItem(`export_reference_${$scope.inputPlayer.guid}`, response.reference);

          return response
        }

        $rootScope.provide.csvGeneratorProvider.progress = async (config) => {
          return documentRest.documentStatus(controller.reference());
        }

        $rootScope.provide.csvGeneratorProvider.cancelGeneration = async (reference) => {
          return documentRest.documentCancel(reference);
        }

        $rootScope.provide.csvGeneratorProvider.download = (reference) => {
          const a = document.createElement("a")
          const url = `services/service-document-generation/document/${reference}/download`;
          a.href = url;
          a.setAttribute('download', reference)
          document.body.appendChild(a);
          a.click();

          setTimeout(() => document.body.removeChild(a), 1500)
        }

        $rootScope.provide.csvGeneratorProvider.getConfig = () => {
          const params = {};
            params.userGuid = controller.selectedUser.guid;
            if (controller.playerTable) {
              params.domain = controller.selectedUser.domain.name
            } else {
              params.domain = controller.domain;
            }
            if (controller.model.dateRangeStart) {
              const date = controller.model.dateRangeStart
              params.dateRangeStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
            }
            if (controller.model.dateRangeEnd) {
              const date = controller.model.dateRangeEnd
              params.dateRangeEnd = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate());
            }
            if (angular.isUndefinedOrNull(controller.model.providers)) { params.providers = controller.model.providers; }
            if (angular.isUndefinedOrNull(controller.model.statuses)) { params.statuses = controller.model.statuses; }
            if (angular.isUndefinedOrNull(controller.model.games)) { params.games = controller.model.games; }
            if (angular.isUndefinedOrNull(controller.model.betRoundGuid)) { params.betRoundGuid = controller.model.betRoundGuid; }
          return {
            domain: params.domain,
            provider: 'service-csv-provider-casino',
            page: 0,
            size: 10,
            userGuid: $scope.inputPlayer.guid,
            role: 'PLAYER_CASINO_HISTORY_VIEW',
            parameters: params,
            reference: controller.reference()
          };
        }

        window.VuePluginRegistry.loadByPage('dashboard/csv-export')


        window.VuePluginRegistry.loadByPage('dashboard/csv-export')
      }
    ]
  };
});
