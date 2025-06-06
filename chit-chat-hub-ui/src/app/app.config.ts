import { ApplicationConfig, provideZoneChangeDetection, APP_INITIALIZER } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideToastr } from 'ngx-toastr';
import { provideAnimations } from '@angular/platform-browser/animations'; // ✅ Required for Toastr animations

import { routes } from './app.routes';
import { KeycloakService } from './utils/keycloak/keycloak.service';
import { keycloakHttpInterceptorInterceptor } from './utils/Http/keycloak-http-interceptor.interceptor';

// ✅ Keycloak initialization function
export function kcService(kc: KeycloakService) {
  console.log('[APP_INITIALIZER] Factory function called');
  return () => kc.init(); // Returns a function that returns a Promise
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
    },
    provideAnimations(), // ✅ Enables Angular animation support (required by ngx-toastr)
    provideToastr({
      progressBar: true,
      closeButton: true,
      newestOnTop: true,
      tapToDismiss: true,
      positionClass: 'toast-bottom-right',
      timeOut: 8000
    }),
  ]
};
