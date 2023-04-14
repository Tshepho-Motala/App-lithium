import UserServiceInterface from '@/core/interface/service/UserServiceInterface'
import { DomainItemInterface } from '@/plugin/cms/models/DomainItem'
import { domains } from '../provider/DomainProviderMock'

export default class UserServiceMock implements UserServiceInterface {
  mock_isAdmin = true
  mock_allowedRoles = [
    'PLAYER_VIEW',
    'PLAYER_EDIT',
    'RANDOM',
    'DOCUMENT_REGULAR_EDIT',
    'DOCUMENT_SENSITIVE_EDIT',
    'DOCUMENT_TYPES_EDIT',
    'TEMPLATES_ADD',
    'GAME_TILE_MANAGE',
    'GAME_TILE_ADD',
    'GAME_TILE_DELETE',
    'BANNER_IMAGE_MANAGE',
    'BANNER_IMAGE_ADD',
    'BANNER_IMAGE_DELETE',
    'WEB_ASSET_MANAGE',
    'WEB_ASSET_ADD',
    'WEB_ASSET_DELETE',
    'PLAYER_EXPORT_LOGIN_HISTORY',
    'REWARD_EDIT',
    'ADMIN',
    'CASINO_LOBBIES_VIEW',
    'BANNERS_VIEW',
    'BANNERS_EDIT',
    'CASINO_BANNERS_ADD',
    'PROMOTIONS_VIEW',
    'PROMOTIONS_EDIT',
    'USER_PROMOTIONS_VIEW'
  ]

  authenticated = true

  roles() {
    return this.mock_allowedRoles
  }

  rolesForDomain(domain: string) {
    return this.mock_allowedRoles
  }

  domainsWithAnyRole(roles: string[]) {
    const domainItems: DomainItemInterface[] = []

    for (let role of roles) {
      domainItems.push(...this.domainsWithRole(role))
    }

    return domainItems
  }

  domainsWithRole(role: string): DomainItemInterface[] {
    return this.mockDomainForRole(role)
  }

  mockDomainForRole(role: string) {
    switch (role) {
      case 'DEMO':
        return {
          ...domains
          // Anything else
        }
      case 'ADMIN':
      default:
        return domains
    }
  }

  hasAdminRole() {
    return this.mock_isAdmin
  }

  hasAdminRoleForDomain(domain: string) {
    return this.mock_isAdmin
  }

  hasRole(role: string) {
    return this.mock_allowedRoles.includes(role)
  }

  hasRoleForDomain(domain: string, role: string) {
    return this.mock_allowedRoles.includes(role)
  }

  playerDomainsWithAnyRole(roles: string[]) {
    return this.mock_allowedRoles
  }

  logout() {
    this.authenticated = false
  }
  _refreshToken() {}

  authenticate(credentials?: any): Promise<void> {
    return new Promise((res) => {
      this.authenticated = true
      res()
    })
  }
}
