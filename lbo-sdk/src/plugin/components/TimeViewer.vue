<template>
    <div>
        <span v-text="timeFormatted"></span>
        <!-- <span v-text="timezone"></span> -->
        <v-menu right
                offset-x
                offset-y
                :close-on-content-click="false">
            <template v-slot:activator="{ on, attrs }">
                <v-btn icon
                       v-bind="attrs"
                       v-on="on">
                    <v-icon small>mdi-clock</v-icon>
                </v-btn>
            </template>

            <div>
                <div class="d-flex">
                    <v-card class="pa-2">
                        <div class="text-center">
                            <span class="text-caption">Timezone References</span>
                        </div>
                        <div v-for="(zone, i) in internalAtz"
                             :key="`zone_${i}`">
                            <div class="d-flex flex-row justify-space-between">
                                <div class="text-caption pr-2">
                                    <span v-text="zone.name"></span> (<span v-text="zone.zone"></span>)
                                </div>
                                <div class="text-caption pl-2">
                                    {{ getZonedTime(zone.zone) }}
                                </div>
                            </div>
                        </div>
                    </v-card>
                </div>
            </div>
        </v-menu>
</div>
</template>

<script lang='ts'>
import { Vue, Component, VModel, Prop } from 'vue-property-decorator'
import { formatInTimeZone } from 'date-fns-tz'
import { format } from 'date-fns'

/**
 * This component should read UTC time, and output UTC, LOCAL, and INPUT (domain | default) time
 */
@Component
export default class TimeViewer extends Vue {
    @VModel({ default: () => [] }) availableTimezones!: { name: string, zone: string, time: string }[]
    @Prop({ default: null }) time!: string | Date | null // 2023-02-14T06:00:00.000Z
    @Prop({ default: null }) utcDate!: Date
    @Prop() timezone: string | undefined

    get localTimezone() {
        return Intl.DateTimeFormat().resolvedOptions().timeZone // This includes DST
    }

    get gmtTimezone() {
        return 'Etc/Greenwich'
    }

    get defaultTimezone() {
        return this.timezone // Get the domain timezone?
    }

    get timeFormatted() {
        return this.getZonedTime(this.defaultTimezone)
    }

    internalAtz: { name: string, zone: string, time: string }[] = []

    mounted() {
        this.getAvailableTimeZones()
    }

    getAvailableTimeZones() {
        const zones: { name: string, zone: string, time: string }[] = []
        if (this.timezone) {
            zones.push({
                name: 'Domain',
                zone: this.timezone,
                time: this.getZonedTime(this.timezone)
            })
        }

        this.internalAtz = [
            ...zones,
            {
                name: 'Local',
                zone: this.localTimezone,
                time: this.getZonedTime(this.localTimezone)
            },
            {
                name: 'GMT',
                zone: this.gmtTimezone,
                time: this.getZonedTime(this.gmtTimezone)
            }
        ]

        this.availableTimezones = this.internalAtz
    }

    getZonedTime(zone: string | null | undefined) {
        if (!zone) {
            return '- No Timezone -'
        }

        if (this.time ) {
            const d = new Date(this.time)
        return formatInTimeZone(d, zone, 'yyyy-MM-dd HH:mm zzz')
        
        }
        
        if(this.utcDate) {
            return formatInTimeZone(this.utcDate, zone, 'yyyy-MM-dd HH:mm zzz')
        }
        
        return 'Nothing'
    }
}
</script>

<style scoped></style>