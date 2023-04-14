<template>
    <v-row data-id='ExclusivePlayerList'>
        <template v-if="validApiPlayers !== null">

            <v-col cols="12">
                <v-toolbar flat>
                    <v-toolbar-title>
                        <span>Exclusive Player List</span>
                    </v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-btn color="primary"
                           @click="getValidTable"
                           text
                           small>
                        <v-icon left>mdi-refresh</v-icon>
                        Refresh
                    </v-btn>

                </v-toolbar>

                <v-data-table v-if="validApiPlayers"
                              :loading="loading"
                              :headers="validHeaders"
                              :items="validApiPlayers.content"
                              :page.sync="validPage"
                              :items-per-page="itemsPerPage"
                              hide-default-footer
                              disable-sort>

                    <template v-slot:[`item.id`]="{ item }">
                        <span v-text="item.id"></span>
                    </template>

                    <template v-slot:[`item.guid`]="{ item }">
                        <span v-text="item.guid"></span>
                    </template>

                </v-data-table>

                <div class="px-4 pb-2">
                    <v-pagination v-model="validPage"
                                  :length="validPageCount"
                                  @input="getValidTable"
                                  :total-visible="7"></v-pagination>
                </div>

            </v-col>
        </template>

        <template v-if="invalidApiPlayers !== null">
            <v-col cols="12">
                <v-card>
                    <v-toolbar flat>
                        <v-toolbar-title>
                            <span>Invalid Player List</span>
                        </v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-btn color="primary"
                               @click="getInvalidTable"
                               text
                               small>
                            <v-icon left>mdi-refresh</v-icon>
                            Refresh
                        </v-btn>
                    </v-toolbar>

                    <v-data-table v-if="invalidApiPlayers"
                                  :loading="loading"
                                  :headers="invalidHeaders"
                                  :items="invalidApiPlayers.content"
                                  :page.sync="invalidPage"
                                  :items-per-page="itemsPerPage"
                                  hide-default-footer
                                  disable-sort>

                        <template v-slot:[`item.id`]="{ item }">
                            <span v-text="item.id"></span>
                        </template>

                        <template v-slot:[`item.guid`]="{ item }">
                            <span v-text="item.guid"></span>
                        </template>

                        <template v-slot:[`item.status`]="{ item }">
                            <v-tooltip left>
                                <template v-slot:activator="{ on, attrs }">
                                    <div>
                                        <v-icon v-if="item.status === 'validation_failed'"
                                                color="red"
                                                v-bind="attrs"
                                                v-on="on">mdi-close</v-icon>
                                        <v-icon v-if="item.status === 'validation_success'"
                                                color="green">mdi-check</v-icon>
                                        <v-icon v-if="item.status === 'validation_in_progress'"
                                                color="warning">mdi-timer-sand</v-icon>
                                    </div>
                                </template>
                                <div v-if="item.reasonForFailure"
                                     class="text-caption">
                                    <span v-text="item.reasonForFailure"></span>
                                </div>
                            </v-tooltip>
                        </template>
                    </v-data-table>

                    <div class="px-4 pb-2">
                        <v-pagination v-model="invalidPage"
                                      :length="invalidPageCount"
                                      @input="getInvalidTable"
                                      :total-visible="7"></v-pagination>
                    </div>
                </v-card>
            </v-col>
        </template>



        <v-col cols="12"
               v-if="stats != null">
            <div>
                <p>Total players: {{ stats.totalPlayers }}</p>
                <p>Processed players: {{ stats.processedPlayers }}</p>
                <p>Invalid players: {{ stats.invalidPlayers }}</p>
                <p>Valid players: {{ stats.validPlayers }}</p>
            </div>
        </v-col>
    </v-row>
</template>

<script lang='ts'>
import { Vue, Component, VModel, Inject } from 'vue-property-decorator'
import { PromotionExclusivePlayer, PromoExclusiveStats, PromotionHistoricExclusivePlayer } from '@/core/interface/contract-interfaces/service-promo/PromotionContract'
import { PageContract } from '@/core/axios/axios-api/generic/TableContract'
import { Promotion } from '@/plugin/promotions/Promotion'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'

@Component
export default class ExclusivePlayerList extends Vue {
    @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface
    @VModel({ required: true, type: Promotion }) readonly promotion!: Promotion

    invalidApiPlayers: PageContract<PromotionExclusivePlayer> | null = null
    validApiPlayers: PageContract<PromotionHistoricExclusivePlayer> | null = null

    loading = false
    invalidHeaders = [
        { text: 'ID', value: 'id' },
        { text: 'Player GUID', value: 'guid' },
        { text: 'Status', value: 'status' }
    ]
    validHeaders = [
        { text: 'ID', value: 'id' },
        { text: 'Player GUID', value: 'guid' }
    ]

    get invalidPageCount() {
        if (!this.invalidApiPlayers) {
            return 0
        }
        return this.invalidApiPlayers.totalPages
    }

    get validPageCount() {
        if (!this.validApiPlayers) {
            return 0
        }
        return this.validApiPlayers.totalPages
    }

    pageSizes = [5, 10, 15, 20, 50, 100]
    invalidPage = 1
    validPage = 1
    itemsPerPage = 10
    stats: PromoExclusiveStats | null = null

    mounted() {
        this.refreshTables()
    }

    async getInvalidTable() {
        this.loading = true

        const promoId = this.promotion.id != null ? this.promotion.id.toString() : ''
        this.invalidApiPlayers = await this.apiClients.servicePromo.getPromotionExclusiveListTable(promoId, this.invalidPage.toString(), this.itemsPerPage.toString(), "VALIDATION_FAILED")

        this.loading = false
    }

    async getValidTable() {
        this.loading = true

        const promoId = this.promotion.id != null ? this.promotion.id.toString() : ''
        this.validApiPlayers = await this.apiClients.servicePromo.getPromotionValidExclusiveListTable(promoId, this.validPageCount.toString(), this.itemsPerPage.toString())

        this.loading = false
    }

    async getStats() {
        return // The following will be a TD task: PLAT-12828
        const promoExclusiveUploadId = '4' //Testing
        this.stats = await this.apiClients.servicePromo.getPromotionExclusiveListProgress(promoExclusiveUploadId)
    }

    // @Watch('promotion')
    refreshTables() {
        this.getInvalidTable()
        this.getValidTable()
        this.getStats()
    }
}

</script>

<style scoped></style>