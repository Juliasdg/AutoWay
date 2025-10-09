import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminEditRegistersComponent } from './admin-edit-registers.component';

describe('AdminEditRegistersComponent', () => {
  let component: AdminEditRegistersComponent;
  let fixture: ComponentFixture<AdminEditRegistersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminEditRegistersComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AdminEditRegistersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
