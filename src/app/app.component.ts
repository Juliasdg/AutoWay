import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BtnPurpleComponent } from './componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from './componets/btn-white/btn-white.component';
import { HeaderComponent } from './componets/header/header.component';
import { HeaderPurpleComponent } from './componets/header-purple/header-purple.component';
import { ActiveCarComponent } from './pages/active-car/active-car.component';
import { CarHistoryComponent } from './pages/car-history/car-history.component';
import { CodePasswordComponent } from './pages/code-password/code-password.component';
import { EditCarComponent } from './pages/edit-car/edit-car.component';
import { EditPasswordComponent } from './pages/edit-password/edit-password.component';
import { EditRegisterComponent } from './pages/edit-register/edit-register.component';
import { HomeComponent } from './pages/home/home.component';
import { HomeAdminComponent } from './pages/home-admin/home-admin.component';
import { InativateAccountComponent } from './pages/inativate-account/inativate-account.component';
import { InativateCarComponent } from './pages/inativate-car/inativate-car.component';
import { ListUsersComponent } from './pages/list-users/list-users.component';
import { NewCarComponent } from './pages/new-car/new-car.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { RegisterComponent } from './pages/register/register.component';
import { RegisteredCarComponent } from './pages/registered-car/registered-car.component';
import { ValidationPasswordComponent } from './pages/validation-password/validation-password.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ValidationPasswordComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'autoway-front-angular';
}
