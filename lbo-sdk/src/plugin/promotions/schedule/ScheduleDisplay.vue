<template>
  <v-row>
    <!-- <v-col cols="12">
      {{ events }}
    </v-col> -->
    <v-col cols="12">
      <ScheduleCalendar ref="calendar"
                        :events="events"
                        @change="onCalendarChange"
                        @dayClick="onCreatePromotionClick"
                        @dateClick="onCreatePromotionClick"
                        @createClick="onCreatePromotionClick"
                        @edit="onEdit" />
    </v-col>
    <v-col cols="12">
      <v-card>
        <v-sheet color="grey lighten-4 rounded px-4 py-5 d-flex">
          <div>
            <span class="text-h5"
                  style="color: #222">Scheduled Promotions for {{ calendarTitle }}</span>
          </div>
          <v-spacer></v-spacer>
          <div>
            <v-btn outlined
                   @click="() => onCreatePromotionClick()">Create New Promotion</v-btn>
          </div>
        </v-sheet>
        <ScheduleList :promotions="promotions"
                      :events="events"
                      @edit="onEdit"
                      @disable="onDisable" />
      </v-card>
    </v-col>
  </v-row>
</template>

<script lang='ts'>
import { RRule } from 'rrule'
import { Vue, Component, Prop, Watch } from 'vue-property-decorator'
import { Promotion } from '../Promotion'
import { ScheduleEventInterface } from './ScheduleEventInterface'

import ScheduleCalendar from './ScheduleCalendar.vue'
import ScheduleList from './ScheduleList.vue'
import { utcToZonedTime, zonedTimeToUtc } from 'date-fns-tz'
import { addDays, isAfter, isBefore, subDays, subSeconds, lastDayOfMonth, format, parse, getMonth } from 'date-fns'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'

@Component({
  components: {
    ScheduleCalendar,
    ScheduleList
  }
})
export default class ScheduleDisplay extends Vue {
  @Prop({ required: true }) readonly promotions!: Promotion[]
  @Prop({ required: true }) readonly domain!: DomainItemInterface

  calendarStart: null | Date = null
  calendarEnd: null | Date = null

  events: ScheduleEventInterface[] = []

  calendarTitle = ''

  get localTimezone() {
    return Intl.DateTimeFormat().resolvedOptions().timeZone // This includes DST
  }

  @Watch('promotions')
  getEventsForMonth() {
    this.events = []

    const sortThisListOfEvents: ScheduleEventInterface[] = []

    if (this.calendarStart && this.calendarEnd) {
      for (const promotion of this.promotions) {
        if (!promotion.domain?.timezone) {
          console.error("Promotion does not have a domain with a timezone. Promo ID: ", promotion.id)
          continue
        }
        const { origOptions: { freq, interval, count } } = RRule.fromString(promotion.schedule.rruleString)
        const rrx = new RRule({
          dtstart: promotion.schedule.dateTimeStartUtc,
          freq,
          tzid: 'UTC',
          interval,
          count
        })

        // const eventStartDates = rrule.all()
        const eventStartDates = rrx.all()

        for (const start of eventStartDates) {

          const modifier = parseInt(promotion.schedule.lengthInDays) || 1
          const end = addDays(start, modifier)

          const startLocal = utcToZonedTime(start, promotion.domain.timezone) // 'yyyy-MM-dd'
          const endLocal = utcToZonedTime(end, promotion.domain.timezone)


          // Check to see if the dates are within the month
          // if(getMonth(startLocal) !== getMonth(this.calendarStart) || getMonth(endLocal) !== getMonth(this.calendarEnd)) {
          //   continue
          // }
          // if (isBefore(startLocal, this.calendarStart) || isAfter(endLocal, this.calendarEnd)) {
          //   continue
          // }
          // End check

          sortThisListOfEvents.push({
            start: startLocal,
            startUtc: start,
            end: subSeconds(endLocal, 1),// -1 second for visualisation
            endUtc: subSeconds(end, 1),// -1 second for visualisation
            name: promotion.title,
            color: promotion.theme.color,
            // startDateFormatted: `${dateStartString} ${timeStartString}`,
            promotion,
            timezone: promotion.domain.timezone
          })
        }
      }
    }

    sortThisListOfEvents.sort((a, b) => a.start.getTime() - b.start.getTime())
    this.events = sortThisListOfEvents
  }

  async onCalendarChange({ start, end, title }) {
    if (!this.domain.timezone) {
      return
    }

    this.calendarTitle = title


    const dStart = new Date(Date.UTC(start.year, start.month - 1, start.day))
    const dEnd = new Date(Date.UTC(end.year, end.month - 1, end.day + 1))

    const bufferStart = subDays(dStart, 1)
    const bufferEnd = addDays(dEnd, 1)

    const bStartFormat = format(bufferStart, 'yyyy-MM-dd')
    const bEndFormat = format(bufferEnd, 'yyyy-MM-dd')

    this.calendarStart = dStart
    this.calendarEnd = dEnd

    this.$emit('calendarChange', { start: bStartFormat, end: bEndFormat })

    // this.getEventsForMonth()
  }

  onCreatePromotionClick(date?: any) {
    this.$emit('create', date)
  }

  onEdit(event: ScheduleEventInterface) {
    this.$emit('edit', event.promotion)
  }

  onDisable(event: ScheduleEventInterface) {
    this.$emit('disable', event.promotion)
  }
}
</script>

<style>
.v-event {
  margin-left: 5px;
}
</style>