<template>
  <v-card flat
          outlined
          id="ExclusiveConfig">
    <v-toolbar flat>
      <v-toolbar-title>
        <span>Mark Promotion as Exclusive</span>
      </v-toolbar-title>
      <v-spacer></v-spacer>

      <v-switch class="mt-6"
                v-model="promotion.exclusive"></v-switch>
    </v-toolbar>

    <template v-if="promotion.exclusive">
      <v-divider></v-divider>

      <v-row>
        <v-col cols="12"
               class="px-8 pt-8">
          <v-text-field outlined
                        :rules="rules.gtZero"
                        type="number"
                        label="Max Redemption per Entry *"
                        v-model="promotion.redeemOverEntry"
                        hint="When a player is added to the exclusive list they will be able to redeem this promotion up to a maximum amount of times specified here. When a player is removed this count will be reset back to 0 for them."
                        persistent-hint></v-text-field>
        </v-col>
        <v-col cols="12" class="pa-0 ma-0">
          <v-divider></v-divider>
        </v-col>

        <v-col cols="6">
          <div class="pa-4">
            <FileReaderText @data="onFileRead"
                            :disabled="!promotion.exclusive" />
            <v-text-field v-model="singlePendingPlayer"
                          outlined
                          label="Add User ID"
                          prepend-icon="mdi-account-plus-outline"
                          placeholder="User ID">
              <template #append-outer>
                <v-btn @click="onManualAdd"
                       :disabled="!singlePendingPlayer">Add</v-btn>
              </template>
            </v-text-field>
          </div>
          <!---pendinf-list-->
          <template v-if="!noResults">
            <v-divider></v-divider>
            <v-virtual-scroll :items="pendingPlayerList"
                              :item-height="itemHeight"
                              :height="virtualScrollHeight">
              <template #default="{ index, item }">
                <v-list-item :key="index">
                  <v-list-item-content>
                    <v-list-item-title>
                      <span v-text="item"> </span>
                    </v-list-item-title>
                  </v-list-item-content>
                  <v-list-item-action>
                    <!-- <v-checkbox v-model="filteredPlayerDataList[index].selected"></v-checkbox> -->
                    <v-btn icon
                           @click="removePendingByIndex(index)">
                      <v-icon>mdi-delete</v-icon>
                    </v-btn>
                  </v-list-item-action>
                </v-list-item>

                <v-divider v-if="index < total - 1"></v-divider>
              </template>
            </v-virtual-scroll>
            <v-divider></v-divider>
            <div class="pa-2 d-flex">
              <span class="text-caption font-weight-medium"
                    v-text="totalResults"></span>
            </div>
          </template>
        </v-col>
        <v-divider vertical></v-divider>
        <v-col cols="6">
          <ExclusivePlayerList v-model="promotion" />
        </v-col>
      </v-row>


    </template>
    <template>

    </template>
  </v-card>
</template>


<script lang='ts'>
import { Component, VModel, Inject, Prop, Watch, Mixins } from 'vue-property-decorator'
import { Promotion } from '../../Promotion'
import FileReaderText from '@/plugin/components/file-readers/FileReaderText.vue'
import { AxiosApiClientsInterface } from '@/core/axios/AxiosApiClients'
import ExclusivePlayerList from './exclusive-config/ExclusivePlayerList.vue'
import RulesMixin from '@/plugin/mixins/RulesMixin'

@Component({
  components: {
    FileReaderText,
    ExclusivePlayerList
  }
})
export default class ExclusiveConfig extends Mixins(RulesMixin) {
  @VModel({ required: true, type: Promotion }) readonly promotion!: Promotion
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  pendingPlayerList: string[] = []
  singlePendingPlayer = ''

  loading = false

  itemHeight = 60
  get virtualScrollHeight(): number {
    const maxHeight = 250
    const desired = this.pendingPlayerList.length * this.itemHeight
    if (desired > maxHeight) {
      return maxHeight
    }
    return desired
  }

  get noResults() {
    return this.total === 0
  }

  get total(): number {
    return this.pendingPlayerList.length
  }

  get totalResults(): string {
    return 'Total Results: ' + this.total
  }


  mounted() {
    this.pendingPlayerList = []
  }

  onManualAdd() {
    this.pendingPlayerList.push(this.singlePendingPlayer)

    this.singlePendingPlayer = ''
  }

  onFileRead(data: string) {
    const newlineRegex = new RegExp(/\r?\n/, 'g')
    this.pendingPlayerList.push(...data.split(newlineRegex))
  }

  @Watch("pendingPlayerList")
  updatePromotionsModel() {
    if (!this.promotion.domain) {
      return
    }

    this.promotion.pendingExclusivePlayers = []
    for (const item of this.pendingPlayerList) {
      // Ensure we append the domain name
      const guid = this.promotion.domain.name + '/' + item
      this.promotion.pendingExclusivePlayers.push(guid)
    }
  }

  removePendingByIndex(index: number) {
    this.pendingPlayerList.splice(index, 1)
  }
}

</script>

<style scoped></style>