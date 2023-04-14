'use strict';

angular.module('lithium').controller('CasinoHistoryController', ['user', 'domain','playerTableBoolean', '$state', function (user, domain, playerTableBoolean, $state) {
  var controller = this;
  controller.selectedUser = user;
  controller.domain = domain;
  controller.playerTableFlag = playerTableBoolean
}]);
