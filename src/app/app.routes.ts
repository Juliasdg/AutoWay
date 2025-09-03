import { Routes } from '@angular/router';
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

export const routes: Routes = [
    { path: '', component: HomeComponent},
    { path: 'carhistory', component: CarHistoryComponent},
    {path: 'activatecar', component: ActiveCarComponent},
    {path: 'codepassword', component: CodePasswordComponent},
    {path: 'editcar', component: EditCarComponent},
    {path: 'editpassword', component: EditPasswordComponent},
    {path: 'editregister', component: EditRegisterComponent},
    {path: 'homeadmin', component: HomeAdminComponent},
    {path: 'inativecar', component: InativateCarComponent},
    {path: 'listusers', component: ListUsersComponent},
    {path: 'newcar', component: NewCarComponent},
    {path: 'profile', component: ProfileComponent},
    {path: 'register', component: RegisterComponent},
    {path: 'registered', component: RegisteredCarComponent},
    {path: 'validationpassword', component: ValidationPasswordComponent}
];
