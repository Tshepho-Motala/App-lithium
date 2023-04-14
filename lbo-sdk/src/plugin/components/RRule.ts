export interface RRuleContract {
  rruleString: string
  lengthInDays: string

  dateTimeStartUtc: Date | null
  dateTimeUntilUtc: Date | null

  preselectedStartUtc?: Date | null
}
