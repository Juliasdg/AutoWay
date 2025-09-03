import { Component } from '@angular/core';
import { HeaderComponent } from '../../componets/header/header.component';
import { BtnPurpleComponent } from '../../componets/btn-purple/btn-purple.component';

@Component({
  selector: 'app-edit-car',
  imports: [HeaderComponent, BtnPurpleComponent],
  templateUrl: './edit-car.component.html',
  styleUrl: './edit-car.component.scss'
})
export class EditCarComponent {

}
