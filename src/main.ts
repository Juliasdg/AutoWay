import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideNgxMask } from 'ngx-mask';


bootstrapApplication(AppComponent, {
  providers: [provideRouter(routes), provideHttpClient(withFetch()), provideNgxMask() // <-- aqui ativa o ngx-mask
]
}).catch(err => console.error(err));
