'use strict';

angular.module('lithium').controller('CasinoHistoryController', ['user', 'domainInfo','playerTableBoolean', function (user, domainInfo, playerTableBoolean) {
  var controller = this;
  controller.selectedUser = user;
  controller.domain = domainInfo;
  playerTableBoolean = true
  controller.playerTableFlag = playerTableBoolean
}]);
