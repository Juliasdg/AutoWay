import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidationPasswordComponent } from './validation-password.component';

describe('ValidationPasswordComponent', () => {
  let component: ValidationPasswordComponent;
  let fixture: ComponentFixture<ValidationPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ValidationPasswordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValidationPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
