export default interface DomainProviderContract {
  id: number
  version: number
  name: string
  displayName: string
  description: string
  enabled: boolean
  deleted: boolean
  players: boolean
  playerDepositLimits: boolean
  playerTimeSlotLimits: boolean
  playtimeLimit: boolean
  playerBalanceLimit: boolean
  url: string
  supportUrl: null | string
  supportEmail: null | string
  parent: DomainProviderContract | null
  preSignupAccessRule: string | null
  signupAccessRule: string | null
  loginAccessRule: string | null
  preLoginAccessRule: string | null
  userDetailsUpdateAccessRule: string | null
  firstDepositAccessRule: string | null
  ipblockList: string | null
  superId: string | null
  superName: string | null
  currency: string
  currencySymbol: string
  physicalAddress: string | null
  postalAddress: string | null
  bankingDetails: string | null
  defaultLocale: null | string
  defaultTimezone: null | string
  timeout: string | null
  defaultCountry: null | string
  bettingEnabled: boolean
  current: Current | null
  failedLoginIpList: null
}

export interface Current {
  id: number
  version: number
  creationDate: number
  labelValueList: LabelValue[]
}

export interface LabelValue {
  id: number
  version: number
  label: Label
  labelValue?: LabelValue
  value?: string
}

export interface Label {
  id: number
  version: number
  name: string
}
