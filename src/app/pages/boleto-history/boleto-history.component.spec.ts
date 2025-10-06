import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoletoHistoryComponent } from './boleto-history.component';

describe('BoletoHistoryComponent', () => {
  let component: BoletoHistoryComponent;
  let fixture: ComponentFixture<BoletoHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BoletoHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BoletoHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
