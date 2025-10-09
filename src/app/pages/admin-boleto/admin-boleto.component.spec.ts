import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBoletoComponent } from './admin-boleto.component';

describe('AdminBoletoComponent', () => {
  let component: AdminBoletoComponent;
  let fixture: ComponentFixture<AdminBoletoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminBoletoComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AdminBoletoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
