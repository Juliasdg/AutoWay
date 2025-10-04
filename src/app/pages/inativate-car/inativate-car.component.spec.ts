import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InativateCarComponent } from './inativate-car.component';

describe('InativateCarComponent', () => {
  let component: InativateCarComponent;
  let fixture: ComponentFixture<InativateCarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InativateCarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InativateCarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
