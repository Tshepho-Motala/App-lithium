import PromotionContract, {
  PromotionExclusivePlayer,
  PromotionRewardReference,
  PromotionUserCategory
} from '@/core/interface/contract-interfaces/service-promo/PromotionContract'
import { PromotionChallengeGroupContract } from '@/core/interface/contract-interfaces/service-promo/PromotionChallengeGroupContract'
import { nanoid } from 'nanoid'
import { RRule } from 'rrule'
import { DomainItemInterface } from '../cms/models/DomainItem'
import Category from '../components/Category'
import { Challenge } from './challenge/Challenge'
import { RRuleContract } from '../components/RRule'
import { formatInTimeZone } from 'date-fns-tz'
import { parse } from 'date-fns'

export class Schedule implements RRuleContract {
  id: string = nanoid()

  rruleString = ''
  lengthInDays: string = '1'

  preselectedStartUtc: Date | null = null

  dateTimeStartUtc: Date | null = null
  dateStartString: string | null = null
  dateTimeUntilUtc: Date | null = null

  singleDay: boolean = true
  timeStart = ''
  timezone: string | undefined

  get dateStartFormatted() {
    if (this.dateTimeStartUtc) {
      return formatInTimeZone(this.dateTimeStartUtc, 'UTC', 'yyyy-MM-dd HH:mm:ss')
    }
    return ''
  }

  get dateUntilFormatted() {
    if (this.dateTimeUntilUtc) {
      return formatInTimeZone(this.dateTimeUntilUtc, 'UTC', 'yyyy-MM-dd HH:mm:ss')
    }
    return null
  }
}

export class PromotionTheme {
  colorName: string = 'primary'
  colorHex: string | null = '#1976d2'

  get color(): string {
    return this.colorHex || this.color
  }
}

export class Promotion {
  id: number | null = null
  latestRevisionId: number | null = null

  title: string = ''
  description: string = ''

  schedule: Schedule = new Schedule()
  reward: PromotionRewardReference | null = null

  category: Category | null = null

  challenges: Challenge[] = [] // TODO: All these need to be interfaces

  domain: DomainItemInterface | null = null

  theme: PromotionTheme = new PromotionTheme()

  redeemOverPromotion: null | string = null // The total amount a player can redeem this promotion over the entire promotion period
  redeemOverEvents: null | string = null // The total amount a player can redeem this promotion over each of the promotion's events
  redeemOverEntry: null | string = null // The total amount a player can redeem this promotion over entry

  exclusive = false
  pendingExclusivePlayers: string[] = []

  tagList: PromotionUserCategory[] = []

  edit: PromotionContract | null = null // This will hold the edit object from when we fetch the promotions.

  requiresAllChallenges: boolean = true
  requiresAllChallengeGroups: boolean = false

  get challengeAmount() {
    return this.challenges.length
  }

  get hasChallenges(): boolean {
    return this.challenges.length > 0
  }

  get hasReward(): boolean {
    return this.reward !== null && this.reward !== null
  }

  get hasDraft() {
    return !!this.edit
  }

  fromContract(contract: PromotionContract, promotionId: number | null): void {
    this.id = promotionId
    this.latestRevisionId = contract.id || null
    this.domain = contract.domain
    this.title = contract.name
    this.description = contract.description

    this.schedule.timezone = this.domain.timezone
    this.schedule.dateTimeStartUtc = new Date(contract.startDate)
    // this.schedule.dateTimeStartUtc = parse
    this.schedule.dateStartString = contract.startDate

    if (contract.endDate) {
      this.schedule.dateTimeUntilUtc = new Date(contract.endDate)
    }
    this.schedule.lengthInDays = contract.eventDuration.toString()
    this.schedule.singleDay = contract.eventDuration === 1

    this.schedule.rruleString = contract.recurrencePattern
    this.reward = contract.reward
    this.redeemOverEvents = contract.redeemableInEvent.toString()
    this.redeemOverPromotion = contract.redeemableInTotal.toString()
    this.redeemOverEntry = contract.redeemablePerEntry ? contract.redeemablePerEntry.toString() : null

    this.exclusive = contract.exclusive
    this.pendingExclusivePlayers = []
    this.tagList = contract.userCategories

    for (const group of contract.challengeGroups) {
      for (const challenge of group.challenges) {
        const c = Challenge.fromContract(challenge, nanoid())
        this.challenges.push(c)
      }
    }
  }

  toContract(): PromotionContract {
    if (!this.domain) {
      throw Error('Domain needed')
    }

    if (!this.schedule.dateStartFormatted) {
      throw Error('Date Start needed')
    }

    if (!this.reward) {
      throw Error('Reward needed')
    }

    // Convert challenges into groups
    const items = this.challenges.filter((x) => x.groupId !== null).map((x) => x.groupId || '')
    const groupIds = Array.from(new Set(items))
    const challengeGroups: PromotionChallengeGroupContract[] = new Array(groupIds.length)

    let index = 0
    for (const groupId of groupIds) {
      const sequenced = true // TODO PROM get sequenced option from group
      challengeGroups[index] = { sequenced, challenges: [], requiresAllChallenges: this.requiresAllChallenges }

      const challenges = this.challenges.filter((x) => x.groupId === groupId)
      for (const challenge of challenges) {
        const contract = challenge.toContract()
        if (contract !== null) {
          challengeGroups[index].challenges.push(contract)
        }
      }

      index++
    }

    const redeemableInTotal = parseInt(this.redeemOverPromotion || '1')
    const redeemableInEvent = parseInt(this.redeemOverEvents || '1')
    const redeemablePerEntry = parseInt(this.redeemOverEntry || this.redeemOverPromotion || '1')
    const eventDuration = parseInt(this.schedule.lengthInDays || '1')

    return {
      domain: this.domain,
      name: this.title,
      description: this.description,
      xpLevel: 0,
      startDate: this.schedule.dateStartFormatted,
      endDate: this.schedule.dateUntilFormatted,
      recurrencePattern: this.schedule.rruleString,
      redeemablePerEntry,
      redeemableInTotal,
      redeemableInEvent,
      eventDuration,
      eventDurationGranularity: 3, // Hardcode 3 because we're always in days
      challengeGroups,
      reward: this.reward,
      exclusive: this.exclusive,
      userCategories: this.tagList,
      requiresAllChallengeGroups: this.requiresAllChallengeGroups
    }
  }
}
