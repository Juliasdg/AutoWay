import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-btn-purple',
  template: `
    <button
      [type]="type"
      [disabled]="disabled"
      class="btn-purple"
      (click)="handleClick()"
    >
      {{ label }}
    </button>
  `,
  styleUrls: ['./btn-purple.component.scss']
})
export class BtnPurpleComponent {
  @Input() label: string = 'Clique';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() disabled: boolean = false;

  @Output() clickEvent = new EventEmitter<void>();

  handleClick() {
    if (!this.disabled) {
      this.clickEvent.emit();
    }
  }
}
