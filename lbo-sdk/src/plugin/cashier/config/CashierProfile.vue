<template>
  <div data-test-id="cashier-profile" class="px-10 py-4 mb-4 d-flex flex-column">
    <div class="d-flex justify-space-between mb-7">
      <v-btn
          data-test-id="btn-show-add-profile-dialog"
          v-show="hasRole('CASHIER_CONFIG_ADD')"
          @click.stop="addProfileDialog = true"
          color="primary"
          class="mr-2">
        <v-icon
            dark
        >
          mdi-plus
        </v-icon>
        {{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.ADD.TITLE')}}
      </v-btn>
      <v-dialog
          transition="dialog-top-transition"
          max-width="600"
          v-model="addProfileDialog"
      >
        <v-card>
          <v-toolbar
              color="primary"
              dark
          >{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.ADD.TITLE')}}</v-toolbar>
          <v-card-text  class="pt-4">
            <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.NAME')}}({{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.PROFILES.FIELDS.CODE.LETTERS_ALLOWED')}}) * </p>
            <v-text-field
                data-test-id="txt-new-method-name"
                v-model="newProfileCode"
                outlined
                dense
                :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.PLACEHOLDER')"
                @blur="checkCode"
                v-on:keypress="isLetter($event)"
            />
            <p class="red--text" v-if="isInAnotherProfile">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.PROFILES_TAB.BASE_HAS_SUCH_NAME')}}</p>
            <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.DESCRIPTION')}}</p>
            <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.NAME')}} *</p>
            <v-text-field
                data-test-id="txt-new-method-name"
                v-model="newProfileName"
                outlined
                dense
                :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.PLACEHOLDER')" />
            <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.DESCRIPTION')}}</p>
            <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.NAME')}}</p>
            <v-text-field
                data-test-id="txt-new-method-name"
                v-model="newProfileDescription"
                outlined
                dense
                :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.PLACEHOLDER')" />
            <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.DESCRIPTION')}}</p>
          </v-card-text>
          <v-card-actions class="d-flex justify-space-between" v-if="!checkingUser">
            <v-btn
                data-test-id="btn-add-profile"
                @click="addProfile"
                class="mr-2 green lighten-2"
                :disabled="disableAddButton"
            >
              {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SUBMIT')}}
            </v-btn>
            <v-btn
                data-test-id="btn-hide-add-profile-dialog"
                @click="addProfileDialog = false"
                class="mr-2 deep-orange lighten-2" >
              {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CLOSE')}}
            </v-btn>
          </v-card-actions>
          <v-card-text v-else class="d-flex justify-space-between">
            <v-progress-circular
                indeterminate
                color="primary"
                :size="70"
                :width="7"
            ></v-progress-circular>
          </v-card-text>
        </v-card>
      </v-dialog>
      <v-text-field
          data-test-id="txt-filtered-profile-name"
          v-model="filteredProfileName"
          placeholder="Enter a search string"
          outlined
          dense
          clearable
          hide-details
          style="max-width: 400px;"
      ></v-text-field>
    </div>
    <div class="w-100 d-flex flex-column align-center" v-if="filteredProfiles.length">
      <cashier-profile-item v-for="profile in filteredProfiles" :key="profile.id" :profile="profile" :domain="domain" @change="profileWasChanged" ></cashier-profile-item>
    </div>
    <div class="w-100 d-flex flex-column align-center" v-else>
      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.SEARCHEMPTY')}}</p>
    </div>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Prop, Mixins, Watch} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import CashierProfileItem from "@/plugin/cashier/config/components/CashierProfileItem.vue";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import TranslationMixin from "@/core/mixins/translationMixin";
import {CashierConfigProfile} from "@/core/interface/cashierConfig/CashierConfigInterface";
import {AxiosApiClientsInterface} from "@/core/axios/AxiosApiClients";

@Component({
  components: {
    CashierProfileItem
  }
})
export default class CashierProfile extends Mixins(TranslationMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @Prop() domain!: string

  profiles: CashierConfigProfile[] = []
  newProfileDescription: string = ""
  newProfileCode: string = ""
  newProfileName: string = ""
  addProfileDialog: boolean = false
  enableButton: boolean = true
  isInAnotherProfile: boolean = false
  filteredProfileName: string = ""
  checkingUser: boolean = false

  mounted() {
    this.getProfiles(this.domain)
  }

  async getProfiles(domain: string) {
     const profiles = await this.apiClients.serviceCashierConfig.getProfiles(domain)

     if (profiles !== null) {
       this.profiles = profiles
     }
  }

  async addProfile() {
    const newProfile: CashierConfigProfile = {
      code: this.newProfileCode,
      name: this.newProfileName,
      description: this.newProfileDescription,
      domain: {
        name: this.domain
      }
    }

    await this.apiClients.serviceCashierConfig.profileSave(this.domain, newProfile)

    this.enableButton = false
    this.addProfileDialog = false
    this.newProfileDescription = ""
    this.newProfileCode = ""
    this.newProfileName = ""
    await this.getProfiles(this.domain)
  }

  hasRole(role: string) {
    let arr: string[] = role.split(',')

    return arr.some( r => this.userService.hasRole(r) )
  }

  profileWasChanged() {
    this.getProfiles(this.domain)
  }

  get filteredProfiles(): CashierConfigProfile[] {
    if(this.filteredProfileName) {
      if(this.profiles.length) {
        let filtered = this.profiles.filter( m => m.name.toLowerCase().includes(this.filteredProfileName.toLowerCase()))

        return filtered
      }
    }
    return this.profiles
  }

  async checkCode() {
    this.checkingUser = true
    this.isInAnotherProfile = false

    let result =  await this.apiClients.serviceCashierConfig.checkProfileCode(this.domain, this.newProfileCode)

    if( result !== null && result?.code) {
      this.enableButton = false
      this.isInAnotherProfile = true

    } else {
      this.enableButton = true
    }

    this.checkingUser = false
  }

  isLetter(e) {
    let char = String.fromCharCode(e.keyCode); // Get the character
    if(/^[A-Za-z]+$/.test(char)) return true; // Match with regex
    else e.preventDefault(); // If not match, don't add to input text
  }

  get disableAddButton (): boolean {
    if(this.enableButton) {
      return !(this.newProfileCode  && this.newProfileName)
    } else {
      return true
    }
  }
}
</script>

<style scoped>
</style>