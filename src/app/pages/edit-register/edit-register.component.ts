import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';

@Component({
  selector: 'app-edit-register',
  imports: [HeaderComponent, BtnPurpleComponent],
  templateUrl: './edit-register.component.html',
  styleUrl: './edit-register.component.scss'
})
export class EditRegisterComponent {

}
