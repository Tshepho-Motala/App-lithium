<template>
  <div data-test-id="cashier-profile-item" class="mb-4" style="min-width: 50%">
    <v-card elevation="10" class="d-flex justify-space-between px-5 py-3" height="100%" min-height="100">
      <div class="d-flex flex-column align-start ">
        <p class="blue--text font-weight-bold">{{profile.name}} ({{profile.code}})</p>
        <p>{{profile.description}}</p>
      </div>
      <div >
        <div>
          <v-btn
              data-test-id="btn-open-edit-profile-tab"
              v-show="hasRole('CASHIER_CONFIG_EDIT')"
              @click="openTab(0)"
              class="mx-2"
          >
            <v-icon
                dark
            >
              mdi-text-box-edit
            </v-icon>
          </v-btn>
          <v-btn
              data-test-id="btn-open-users-profile-tab"
              @click="openTab(1)"
              v-show="hasRole('CASHIER_CONFIG_VIEW,CASHIER_CONFIG_ADD,CASHIER_CONFIG_EDIT,CASHIER_CONFIG_DELETE')"
              class="mx-2"
          >
            <v-icon
                dark
            >
              mdi-account-group
            </v-icon>
          </v-btn>
            <v-dialog
                transition="dialog-top-transition"
                max-width="600"
                v-model="editProfileDialog"
            >
              <v-tabs
                  v-model="editTab"
                  background-color="deep-purple accent-4"
                  centered
                  dark
                  icons-and-text
              >
                <v-tab v-for="tab in tabs"
                       :key="tab.id">
                    {{tab.name}}
                </v-tab>
              </v-tabs>
              <v-tabs-items v-model="editTab">
                <v-tab-item
                    v-for="tab in tabs"
                    :key="tab.id"
                    v-show="hasRoleForDomain(tab.roles, domain)"
                >
                  <v-card v-if="tab.id === 0">
                    <v-card-text  class="pt-4">
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.NAME')}} *</p>
                      <v-text-field data-test-id="txt-new-method-name"
                                    v-model="profileCode"
                                    disabled
                                    outlined
                                    dense />
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.CODE.DESCRIPTION')}}</p>
                      <p class="red--text" v-if="isInAnotherProfile">{{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.PROFILES_TAB.BASE_HAS_SUCH_NAME')}}</p>
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.NAME')}} *</p>
                      <v-text-field data-test-id="txt-new-method-name"
                                    v-model="profileName"
                                    disabled
                                    outlined
                                    dense />
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.NAME.DESCRIPTION')}}</p>

                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.NAME')}}</p>
                      <v-text-field data-test-id="txt-new-method-name"
                                    v-model="profileDescription"
                                    outlined
                                    dense
                                    :placeholder="$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.PLACEHOLDER')" />
                      <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.FIELDS.DESC.DESCRIPTION')}}</p>
                    </v-card-text>
                    <v-card-actions class="d-flex justify-space-between">
                      <v-btn
                          data-test-id="btn-edit-profile"
                          @click="editProfile"
                          class="mr-2 green lighten-2"
                      >
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SUBMIT')}}
                      </v-btn>
                      <v-btn
                          data-test-id="btn-hide-edit-profile-dialog"
                          @click="editProfileDialog = false"
                          class="mr-2 deep-orange lighten-2" >
                        {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.CLOSE')}}
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                  <v-card v-else-if="tab.id === 1" >
                    <div class="d-flex justify-space-between pa-3">
                      <v-btn
                          data-test-id="btn-open-users-tab"
                          v-show="hasRole('CASHIER_CONFIG_ADD')"
                          @click="addUsersProfileDialog = true"
                      >
                        <v-icon
                            dark
                            right
                        >
                          mdi-account-multiple-plus
                        </v-icon>
                      </v-btn>
                      <v-dialog
                          transition="dialog-top-transition"
                          max-width="600"
                          v-model="addUsersProfileDialog"
                      >
                        <v-toolbar
                            color="primary"
                            dark
                        >{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.ADDUSER.TITLE')}}</v-toolbar>
                        <div class="d-flex flex-column white px-4">
                          <h4 class="blue--text">{{profile.name}}</h4>
                          <h4>{{profile.description}}</h4>
                          <p>{{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.ADDUSER.SEARCHTITLE')}}</p>
                          <v-autocomplete
                              data-test-id="slt-search-user"
                              v-model="searchedUser"
                              :loading="loadingUsers"
                              :items="users"
                              item-text="username"
                              item-value="username"
                              cache-items
                              class="mx-2 mb-4"
                              flat
                              hide-no-data
                              hide-details
                              label="search player"
                              solo-inverted
                              return-object
                              :search-input.sync="searchUsers"
                              @change="userWasSelected"
                          ></v-autocomplete>
                          <div v-if="isInAnotherProfile && otherProfile" class="method-disabled-shadow px-5 py-2 text-center font-weight-bold red--text mb-4">
                            {{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.ADDUSER.SHOWANOTHERPROFILEWARNING')}}
                            <div class="py-3 ">
                              <p class="blue--text text-center font-weight-bold mb-2">{{otherProfile.code}} - {{otherProfile.name}}</p>
                              <p class="text-center mb-0">{{otherProfile.description}}</p>
                            </div>
                          </div>
                          <div v-if="isInThisProfile" class="method-disabled-shadow pa-5 text-center font-weight-bold red--text mb-4">
                            {{$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.ADDUSER.INTHISPROFILE')}}
                          </div>
                          <div class="d-flex justify-space-between mb-6" v-if="!checkingUser">
                            <v-btn
                                data-test-id="btn-add-users-to-profile"
                                @click="addUsersToProfile"
                                color="primary"
                                :disabled="!enableAddUserButton && searchedUser"
                            >
                              {{$translate('UI_NETWORK_ADMIN.CASHIER_CONFIG.SUBMIT')}}
                            </v-btn>
                            <v-btn
                                data-test-id="btn-hide-add-users-dialog"
                                @click="hideAddUserDialog"
                            >
                              {{$translate('UI_NETWORK_ADMIN.CASHIER.BANK_ACCOUNT_LOOKUP.MANUAL_WITHDRAWAL.CANCEL')}}
                            </v-btn>
                          </div>
                          <div v-else class="d-flex justify-space-between">
                            <v-progress-circular
                                indeterminate
                                color="primary"
                                :size="70"
                                :width="7"
                            ></v-progress-circular>
                          </div>
                        </div>
                      </v-dialog>
                      <v-text-field
                          data-test-id="txt-filtered-user-name"
                          v-model="filteredUserName"
                          placeholder="Enter a user name"
                          outlined
                          dense
                          clearable
                          hide-details
                          style="max-width: 400px;"
                      ></v-text-field>
                    </div>
                      <v-data-table
                          data-test-id="tbl-profile-users"
                          :headers="headers"
                          :items="profileUsers"
                          item-key="username"
                          class="elevation-1 pa-3"
                          :search="filteredUserName"
                      >
                        <template v-slot:[`item`]="{ item }">
                          <div class="d-flex justify-space-between py-3 px-5">
                            <p>{{item.guid}}</p>
                            <v-btn
                                data-test-id="btn-delete-users-from-profile"
                                v-show="hasRole('CASHIER_PROFILE_EDIT')"
                                small
                                color="error"
                                class="ml-2"
                                @click.native="deleteUserFromProfile(item)"
                            >
                              {{$translate("UI_NETWORK_ADMIN.CASHIER.AUTOWITHDRAWALS.RULESETS.IMPORT.BUTTONS.DELETE")}}
                            </v-btn>
                          </div>
                        </template>
                    </v-data-table>
                  </v-card>
                </v-tab-item>
              </v-tabs-items>
          </v-dialog>
          <v-btn
              data-test-id="btn-delete-profile"
              v-show="hasRole('CASHIER_CONFIG_DELETE')"
              @click="deleteProfile(profile.id)"
              class="mx-2"
          >
            <v-icon
                dark
            >
              mdi-delete
            </v-icon>
          </v-btn>
        </div>
      </div>
    </v-card>
  </div>
</template>

<script lang="ts">
import {Component, Inject, Prop, Mixins, Watch} from "vue-property-decorator";
import {RootScopeInterface} from "@/core/interface/ScopeInterface";
import UserServiceInterface from "@/core/interface/service/UserServiceInterface";
import LogServiceInterface from "@/core/interface/service/LogServiceInterface";
import TranslationMixin from "@/core/mixins/translationMixin";
import {
  CashierConfigProfile,
  CashierConfigUser, CashierProfileTab,
  CashierProfileUser, CashierTableData, CashierTableHeader
} from "@/core/interface/cashierConfig/CashierConfigInterface";
import {AxiosApiClientsInterface} from "@/core/axios/AxiosApiClients";

@Component
export default class CashierProfileItem extends Mixins(TranslationMixin) {
  @Inject('rootScope') readonly rootScope!: RootScopeInterface
  @Inject('logService') readonly logService!: LogServiceInterface
  @Inject('userService') readonly userService!: UserServiceInterface
  @Inject('apiClients') readonly apiClients!: AxiosApiClientsInterface

  @Prop() profile!: CashierConfigProfile
  @Prop() domain!: string

  editProfileDialog: boolean = false
  editTab: number = 1
  addUsersProfileDialog: boolean = false
  filteredUserName: string = ""
  searchedUser: CashierConfigUser | null = null
  loadingUsers: boolean = false
  profileCode: string = ''
  profileName: string = ''
  profileDescription: string = ''
  searchUsers: string = ''
  profileUsers: CashierConfigUser[]  = []
  users: CashierConfigUser[] = []
  drawTable: number = 1
  isInThisProfile: boolean = false
  isInAnotherProfile: boolean = false
  enableAddUserButton: boolean = false
  enableEditButton: boolean = false
  otherProfile: CashierProfileUser | null = null
  checkingUser: boolean = false

  tabs: CashierProfileTab[] = [
    {
      id: 0,
      name: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.TAB.EDIT'),
      roles: "ADMIN"
    },
    {
      id: 1,
      name: this.$translate('UI_NETWORK_ADMIN.CASHIER.PROFILES.TAB.USERS'),
      roles: "ADMIN"
    }
  ]

  headers: CashierTableHeader[] = [
    { id: 1, text: 'username', value: 'guid' }
  ]

  mounted() {
    this.profileCode = this.profile.code ? this.profile.code : ""
    this.profileName = this.profile.name ? this.profile.name : ""
    this.profileDescription = this.profile.description ? this.profile.description : ""

    this.getUserForProfile(false)
  }

  async getUserForProfile(increaseDrawTable: boolean) {
    if(this.profile && this.profile.id) {
      if(increaseDrawTable) {
        this.drawTable++
      }

      const data: CashierTableData = {
        draw: this.drawTable,
        start: 0,
        length: 99999999,
      }

      const users =  await this.apiClients.serviceCashierConfig.profileUsers(this.profile.id.toString(), data)

      if (users !== null && users.data !== null) {
        this.profileUsers = users.data

      } else {
        this.profileUsers = []
      }

      if(!increaseDrawTable) {
        this.drawTable++
      }
    }
  }

  async editProfile() {
    const editedProfile: CashierConfigProfile = {
      ...this.profile,
      code: this.profileCode,
      name: this.profileName,
      description: this.profileDescription,
      domain: {
        name: this.domain
      }
    }

    await this.apiClients.serviceCashierConfig.profileSave(this.domain, editedProfile)
    this.$emit('change')

    this.editProfileDialog = false
  }

  async deleteUserFromProfile(item: CashierConfigProfile) {
    let profileId = -1

    await this.apiClients.serviceCashierConfig.userProfileUpdate(encodeURIComponent(item.guid), profileId.toString())

    await this.getUserForProfile(true)
  }

  openTab( tabState: number) {
    this.editTab = tabState
    this.editProfileDialog = true
  }

  async addUsersToProfile() {
    if(this.searchedUser && this.profile?.id){

      await this.apiClients.serviceCashierConfig.userProfileUpdate(encodeURIComponent(this.searchedUser.guid), this.profile.id.toString())

      this.editProfileDialog = true
      this.addUsersProfileDialog = false
      this.searchedUser = null
      this.searchUsers = ''
      this.drawTable++

      await this.getUserForProfile(true)
    }
  }

  async deleteProfile(itemId:number) {
    if (!itemId) {
      //throw some error
      return
    } else {
      await this.apiClients.serviceCashierConfig.deleteProfileById(this.domain, itemId.toString())
      this.drawTable++

      this.$emit('change')
    }
  }

  async getUsers(){
    this.loadingUsers = true

    let result = await this.apiClients.serviceUserSearch.searchUsers(this.domain, this.searchUsers)

    if (result !== null) {
      this.users = result
    }

    this.loadingUsers = false
  }

  @Watch('searchUsers')
  async onSearchUserChanged() {
    if(this.searchUsers && this.searchUsers.length > 1) {
      await this.getUsers()
    }
  }

  async userWasSelected() {
    this.isInThisProfile = false
    this.isInAnotherProfile = false
    this.enableAddUserButton = false
    this.otherProfile = null

    if(this.searchedUser && this.profile) {
      let res =  await this.apiClients.serviceCashierConfig.userByGuid(this.searchedUser.guid)

      if( res !== null ) {

        if( res.profile && res.profile.id === this.profile.id ) {
          this.isInThisProfile = true

        } else if (res.profile && res.profile.id !== this.profile.id) {
          this.isInAnotherProfile = true
          this.otherProfile = res.profile

        } else if (res.profile === null) {
          this.enableAddUserButton = true
        }
      }
    }
  }

  hideAddUserDialog() {
    this.searchedUser = null
    this.searchUsers = ''
    this.isInAnotherProfile = false
    this.isInThisProfile = false
    this.addUsersProfileDialog = false
  }

  hasRoleForDomain (role, domain) {
    if(Array.isArray(role)) {
      const results:any = []
      role.forEach((el:any) => {
        let isHas =  this.userService.hasRoleForDomain(domain, el)
        results.push(isHas)
      })
      const isNotHasRole:boolean = results.find((elem:any) => elem === false)
      return !isNotHasRole ? false : true
    }

    if(typeof role === 'string') {
      const roles = role.split(',')
      if (roles.length > 1) {
        return roles.some(r => this.userService.hasRole(r))
      }
    }

    return  this.userService.hasRoleForDomain(domain, role)
  }

  hasRole(role: string) {
    let arr: string[] = role.split(',')

    return arr.some( r => this.userService.hasRole(r) )
  }
}
</script>

<style scoped>
</style>