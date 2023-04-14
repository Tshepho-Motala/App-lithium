'use strict';

angular.module('lithium')
.controller('BetHistoryDomainSelectController', ['$translate', '$scope', '$userService', '$stateParams', '$state',
    function($translate, $scope, $userService, $stateParams, $state) {
        var controller = this;
        controller.domains = $userService.playerDomainsWithAnyRole(['ADMIN']);
        controller.domainSelect = function(item) {
            controller.selectedDomain = item.name;
            $state.go('dashboard.casino.bet-history.list', {
                user: {},
                domain: controller.selectedDomain,
                playerTableBoolean: false,
            });
        }
        controller.clearSelectedDomain = function() {
            controller.selectedDomain = null;
            $scope.setDescription("");
            $state.go('dashboard.casino.bet-history');
        }
}
]);
