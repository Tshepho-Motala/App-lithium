import {
    CashierConfigDmpp,
    CashierConfigDmpu,
    CashierConfigFees,
    CashierConfigLimits,
    CashierConfigProcessor, DomainAccountingInfoInterface
} from "@/core/interface/cashierConfig/CashierConfigInterface";

export default class CashierConfigProcessorMock implements CashierConfigProcessor {

    accessRule: string | null  = ""
    accessRuleOnTranInit: string | null = ""
    accountingDay: DomainAccountingInfoInterface | null = null
    accountingLastMonth: DomainAccountingInfoInterface | null = null
    accountingMonth: DomainAccountingInfoInterface | null = null
    accountingWeek: DomainAccountingInfoInterface | null = null
    active: boolean | null = null
    changelog?:any = {}
    deleted: boolean = false
    description: string | null = null
    domainLimits: null | CashierConfigLimits = null
    dmpp?: null | CashierConfigDmpp = null
    dmpu?: null | CashierConfigDmpu = null
    domainMethod: null | any = null
    enabled: boolean = false
    fees: null | CashierConfigFees = null
    id: number = 0
    limits: null | CashierConfigLimits = null
    processor: null | any = null
    reserveFundsOnWithdrawal: null | boolean = null
    version: number = 0
    weight: number = 0
}