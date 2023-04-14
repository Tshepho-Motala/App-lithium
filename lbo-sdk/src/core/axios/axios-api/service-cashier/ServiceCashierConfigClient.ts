import AxiosApiClient from '../../AxiosApiClient'
import {
    CashierConfigProfile, CashierConfigUser,
    CashierProfileUser,
    CashierTableData
} from "@/core/interface/cashierConfig/CashierConfigInterface";
import {TableContract} from "@/core/axios/axios-api/generic/TableContract";

export default class ServiceCashierConfigClient extends AxiosApiClient {
    localPrefix: string = 'service-cashier/cashier/'
    livePrefix: string = 'services/' + this.localPrefix

    getProfiles(domainName: string): Promise<CashierConfigProfile[] | null> {
        return this.get(
            'profile',
            domainName
        )
    }

    profileSave(domainName: string, params: CashierConfigProfile): Promise<void | null> {
        return this.postJson(
            {
                ...params
            },
            'profile',
            domainName
        )
    }

    deleteProfileById(domainName: string, profileId: string): Promise<CashierConfigProfile[] | null> {
        return this.post({}, 'profile', domainName, profileId, 'delete')
    }


    userByGuid(userGuid: string): Promise<CashierProfileUser | null> {
        return this.getWithParameter( { userGuid: userGuid}, 'user')
    }

    profileUsers(profileId: string, params: CashierTableData): Promise<TableContract<CashierConfigUser> | null> {
        return this.getWithParameter(
            {
                ...params
            },
            'user',
            'profile',
            profileId,
            'table'
        )
    }

    userProfileUpdate(user: string, profileId: string): Promise<void | null>{
        return this.putWithParameters( {
                profileId: profileId,
                userGuid: user
            },
            'user',
            'profile'
        )
    }

    checkProfileCode(domainName: string, code: string): Promise<CashierConfigProfile | null> {
        return this.getWithParameter( { code: code}, 'profile', domainName , 'code')
    }
}
