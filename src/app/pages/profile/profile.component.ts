import { Component } from '@angular/core';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from '../../componets/btn-white/btn-white.component';

@Component({
  selector: 'app-profile',
  imports: [HeaderPurpleComponent, BtnPurpleComponent, BtnWhiteComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {

}

