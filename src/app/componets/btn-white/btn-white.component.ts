import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-btn-white',
  imports: [],
  templateUrl: './btn-white.component.html',
  styleUrl: './btn-white.component.scss'
})
export class BtnWhiteComponent {
   @Input() label: string = 'Clique';
}
