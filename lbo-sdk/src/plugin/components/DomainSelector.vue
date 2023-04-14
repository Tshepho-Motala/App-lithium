<template>
  <div id="DomainSelector">
    <template v-if="horizontal">
      <DomainSelectorHorizontal v-model="domain" @change="onChange" :roles="roles" :description="description" :title="title" :unlocked="unlocked" :solo="solo" :combineApiData="combineApiData" />
    </template>
    <template v-else>
      <DomainSelectorStacked v-model="domain" @change="onChange" :roles="roles" :description="description" :title="title" :unlocked="unlocked" :solo="solo" :combineApiData="combineApiData" />
    </template>
  </div>
</template>



<script lang="ts">
import { Component, Prop, VModel, Vue } from 'vue-property-decorator'
import { DomainItemInterface } from '../cms/models/DomainItem'

import DomainSelectorHorizontal from './domain-selector/DomainSelectorHorizontal.vue'
import DomainSelectorStacked from './domain-selector/DomainSelectorStacked.vue'

@Component({
  components: {
    DomainSelectorHorizontal,
    DomainSelectorStacked
  }
})
export default class DomainSelector extends Vue {
  @Prop({ type: Array, default: () => ['ADMIN'] }) readonly roles!: string[]
  @Prop({ default: '' }) readonly title!: string
  @Prop({ default: '' }) readonly description!: string

  @Prop({ default: false, type: Boolean }) readonly solo!: boolean
  @Prop({ default: false, type: Boolean }) readonly horizontal!: boolean
  @Prop({ default: false, type: Boolean }) readonly unlocked!: boolean // If true, this will be a simple drop down list with no lock
  @Prop({ type: Boolean, default: false }) readonly combineApiData!: boolean

  @VModel({ default: null }) domain!: DomainItemInterface | null

  get hasDomain(): boolean {
    return this.domain !== null
  }

  onChange(domain: DomainItemInterface | null) {
    this.$emit('change', domain)
  }

  reset() {
    this.domain = null
  }
}
</script>

<style scoped></style>
