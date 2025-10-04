import { Component } from '@angular/core';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';

@Component({
  selector: 'app-home',
  imports: [HeaderPurpleComponent, BtnPurpleComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
