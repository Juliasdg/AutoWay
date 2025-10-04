import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-btn-purple',
  imports: [],
  templateUrl: './btn-purple.component.html',
  styleUrl: './btn-purple.component.scss'
})
export class BtnPurpleComponent {
  @Input() label: string = 'Clique';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() disabled: boolean = false;
  @Output() onClick = new EventEmitter<void>();
}
