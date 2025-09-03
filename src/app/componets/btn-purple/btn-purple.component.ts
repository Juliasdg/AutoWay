import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-btn-purple',
  imports: [],
  templateUrl: './btn-purple.component.html',
  styleUrl: './btn-purple.component.scss'
})
export class BtnPurpleComponent {
  @Input() label: string = 'Clique';
}
