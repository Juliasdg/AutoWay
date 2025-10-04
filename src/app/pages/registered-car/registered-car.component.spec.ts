import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisteredCarComponent } from './registered-car.component';

describe('RegisteredCarComponent', () => {
  let component: RegisteredCarComponent;
  let fixture: ComponentFixture<RegisteredCarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisteredCarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisteredCarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
