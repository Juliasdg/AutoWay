import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';

@Component({
  selector: 'app-code-password',
  imports: [HeaderComponent, BtnPurpleComponent],
  templateUrl: './code-password.component.html',
  styleUrl: './code-password.component.scss'
})
export class CodePasswordComponent {

}
