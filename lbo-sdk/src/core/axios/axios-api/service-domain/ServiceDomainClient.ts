import DomainProviderContract from '@/core/interface/contract-interfaces/service-domain/DomainProviderContract'
import AxiosApiClient from '../../AxiosApiClient'

export default class ServiceDomainClient extends AxiosApiClient {
  localPrefix: string = 'service-domain/'
  livePrefix: string = 'services/service-domain/'

  findAllDomains(): Promise<DomainProviderContract[] | null> {
    return this.get('domains', 'findAllDomains')
  }
}
