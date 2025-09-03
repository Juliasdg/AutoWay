import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveCarComponent } from './active-car.component';

describe('ActiveCarComponent', () => {
  let component: ActiveCarComponent;
  let fixture: ComponentFixture<ActiveCarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActiveCarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActiveCarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
