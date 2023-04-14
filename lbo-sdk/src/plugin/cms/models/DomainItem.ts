import { nanoid } from 'nanoid'

export interface DomainItemInterface {
  displayName: string
  name: string
  pd: boolean
  timezone?: string
}