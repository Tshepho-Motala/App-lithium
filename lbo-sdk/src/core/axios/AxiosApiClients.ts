import UserServiceInterface from '../interface/service/UserServiceInterface'
import ServiceGamesClient from './axios-api/service-games/ServiceGamesClient'
import ServiceOauthClient from './axios-api/service-oauth/ServiceOauthClient'
import ServicePromoClient from './axios-api/service-promo/ServicePromoClient'
import ServiceRewardClient from './axios-api/service-reward/ServiceRewardClient'
import ServiceUserClient from './axios-api/service-user/ServiceUserClient'
import ServiceCashierClient from './axios-api/service-cashier/ServiceCashierClient'
import AxiosApiClient from './AxiosApiClient'
import ServiceDomainClient from './axios-api/service-domain/ServiceDomainClient'
import ServiceUserThresholdClient from '@/core/axios/axios-api/service-threshold/ServiceUserThresholdClient'
import ServiceCashierConfigClient from "@/core/axios/axios-api/service-cashier/ServiceCashierConfigClient";
import ServiceUserSearchClient from "@/core/axios/axios-api/service-user/ServiceUserSearchClient";

export default class AxiosApiClients implements AxiosApiClientsInterface {
  generic: AxiosApiClient
  serviceOauth: ServiceOauthClient
  serviceGames: ServiceGamesClient
  servicePromo: ServicePromoClient
  serviceReward: ServiceRewardClient
  serviceUser: ServiceUserClient
  serviceUserSearch: ServiceUserSearchClient
  serviceCashier: ServiceCashierClient
  serviceDomain: ServiceDomainClient
  serviceUserThreshold: ServiceUserThresholdClient
  serviceCashierConfig: ServiceCashierConfigClient

  constructor(userService: UserServiceInterface) {
    this.generic = new AxiosApiClient(userService)
    this.serviceOauth = new ServiceOauthClient(userService)
    this.serviceGames = new ServiceGamesClient(userService)
    this.servicePromo = new ServicePromoClient(userService)
    this.serviceReward = new ServiceRewardClient(userService)
    this.serviceUser = new ServiceUserClient(userService)
    this.serviceUserSearch = new ServiceUserSearchClient(userService)
    this.serviceCashier = new ServiceCashierClient(userService)
    this.serviceDomain = new ServiceDomainClient(userService)
    this.serviceUserThreshold = new ServiceUserThresholdClient(userService)
    this.serviceCashierConfig = new ServiceCashierConfigClient(userService)
  }
}

;(window as any).VueCreateAxiosClient = function (userService) {
  return new AxiosApiClients(userService)
}

export interface AxiosApiClientsInterface {
  generic: AxiosApiClient
  serviceOauth: ServiceOauthClient
  serviceGames: ServiceGamesClient
  servicePromo: ServicePromoClient
  serviceReward: ServiceRewardClient
  serviceUser: ServiceUserClient
  serviceUserSearch: ServiceUserSearchClient
  serviceCashier: ServiceCashierClient
  serviceDomain: ServiceDomainClient
  serviceUserThreshold: ServiceUserThresholdClient
  serviceCashierConfig: ServiceCashierConfigClient
}
