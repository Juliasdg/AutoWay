import { Component } from '@angular/core';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';
import { HeaderPurpleComponent } from '../../componets/header-purple/header-purple.component';

@Component({
  selector: 'app-car-history',
  imports: [HeaderPurpleComponent, BtnPurpleComponent],
  templateUrl: './car-history.component.html',
  styleUrl: './car-history.component.scss'
})
export class CarHistoryComponent {

}
