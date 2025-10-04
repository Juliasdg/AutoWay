import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BtnWhiteComponent } from './btn-white.component';

describe('BtnWhiteComponent', () => {
  let component: BtnWhiteComponent;
  let fixture: ComponentFixture<BtnWhiteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BtnWhiteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BtnWhiteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
