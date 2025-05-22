import {Injectable} from '@angular/core';
import Keycloak from "keycloak-js";

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak?: Keycloak;

  get keycloak(): Keycloak {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: 'http://localhost:9090',
        realm: 'chit-chat-hub',   //realm
        clientId: 'chit-chat-hub-client'   //client
      });
    }
    return this._keycloak;
  }

  async init(): Promise<boolean> {
    console.log('[KeycloakService] Initializing Keycloak...');
    try {
      const authenticated = await this.keycloak.init({
        onLoad: 'login-required',          // auto-login on app load
        checkLoginIframe: false            // disables silent SSO iframe
      });
      console.log('[KeycloakService] Authenticated:', authenticated);
      return authenticated;
    } catch (e) {
      console.error('[KeycloakService] Init failed', e);
      return false;
    }
  }


  async login() {
    await this.keycloak?.login();
  }

  get userId() {
    return this.keycloak?.tokenParsed?.sub as string;
  }

  get isTokenValid() {
    return !this.keycloak?.isTokenExpired();
  }

  get fullName(): string {
    return this.keycloak?.tokenParsed?.['name'] as string;
  }

  logout() {
    return this.keycloak?.logout({redirectUri: 'http://localhost:4200'})
  }

  accountManagement() {
    return this.keycloak?.accountManagement();
  }

  constructor() {
  }


}
