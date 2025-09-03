import { ApplicationConfig, NgModule, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { CarHistoryComponent } from './pages/car-history/car-history.component';
import { HomeComponent } from './pages/home/home.component';
import { ActiveCarComponent } from './pages/active-car/active-car.component';
import { CodePasswordComponent } from './pages/code-password/code-password.component';
import { EditCarComponent } from './pages/edit-car/edit-car.component';
import { EditPasswordComponent } from './pages/edit-password/edit-password.component';
import { EditRegisterComponent } from './pages/edit-register/edit-register.component';
import { HomeAdminComponent } from './pages/home-admin/home-admin.component';
import { InativateCarComponent } from './pages/inativate-car/inativate-car.component';
import { ListUsersComponent } from './pages/list-users/list-users.component';
import { NewCarComponent } from './pages/new-car/new-car.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { RegisterComponent } from './pages/register/register.component';
import { RegisteredCarComponent } from './pages/registered-car/registered-car.component';
import { ValidationPasswordComponent } from './pages/validation-password/validation-password.component';

import { BrowserModule } from '@angular/platform-browser';

NgModule({
  declarations: [
    HomeComponent,
    ActiveCarComponent,
    CarHistoryComponent,
    CodePasswordComponent,
    EditCarComponent,
    EditPasswordComponent,
    EditRegisterComponent,
    HomeComponent,
    HomeAdminComponent,
    InativateCarComponent,
    ListUsersComponent,
    NewCarComponent,
    ProfileComponent,
    RegisterComponent,
    RegisteredCarComponent,
    ValidationPasswordComponent
  ],
  imports: [    
    BrowserModule
  ]
})


export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes)]
};
