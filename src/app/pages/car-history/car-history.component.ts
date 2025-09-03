import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { BtnWhiteComponent } from '../../componets/btn-white/btn-white.component';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';

@Component({
  selector: 'app-car-history',
  imports: [HeaderPurpleComponent, BtnPurpleComponent, BtnWhiteComponent],
  templateUrl: './car-history.component.html',
  styleUrl: './car-history.component.scss'
})
export class CarHistoryComponent {

}
