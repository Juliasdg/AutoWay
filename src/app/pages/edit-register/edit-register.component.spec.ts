import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditRegisterComponent } from './edit-register.component';

describe('EditRegisterComponent', () => {
  let component: EditRegisterComponent;
  let fixture: ComponentFixture<EditRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
