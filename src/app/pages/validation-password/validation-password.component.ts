import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';

@Component({
  selector: 'app-validation-password',
  imports: [HeaderComponent, BtnPurpleComponent],
  templateUrl: './validation-password.component.html',
  styleUrl: './validation-password.component.scss'
})
export class ValidationPasswordComponent {

}
