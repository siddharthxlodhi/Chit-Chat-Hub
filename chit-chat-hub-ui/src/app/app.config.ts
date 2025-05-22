import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import { KeycloakService } from './utils/keycloak/keycloak.service';
import { APP_INITIALIZER } from '@angular/core';
import {keycloakHttpInterceptorInterceptor} from "./utils/Http/keycloak-http-interceptor.interceptor"; // ✅ Important import

export function kcService(kc: KeycloakService) {
  console.log('[APP_INITIALIZER] Factory function called');
  return () => kc.init(); // Must return a function that returns a Promise
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([keycloakHttpInterceptorInterceptor])
    ),
    {
      provide: APP_INITIALIZER,
      useFactory: kcService,
      deps: [KeycloakService],
      multi: true
    }
  ]
};
