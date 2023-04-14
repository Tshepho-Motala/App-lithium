import AxiosApiClient from '../../AxiosApiClient'
import {CashierConfigUser} from "@/core/interface/cashierConfig/CashierConfigInterface";

export default class ServiceUserSearchClient extends AxiosApiClient {
    localPrefix: string = 'service-user/'
    livePrefix: string = 'services/' + this.localPrefix


    searchUsers(domainName: string, userName: string): Promise<CashierConfigUser[] | null> {
        return this.getWithParameter({search: userName}, domainName,'users','list')
    }
}
