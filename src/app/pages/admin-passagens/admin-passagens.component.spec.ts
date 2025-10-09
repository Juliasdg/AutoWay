import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPassagensComponent } from './admin-passagens.component';

describe('AdminPassagensComponent', () => {
  let component: AdminPassagensComponent;
  let fixture: ComponentFixture<AdminPassagensComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminPassagensComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AdminPassagensComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
