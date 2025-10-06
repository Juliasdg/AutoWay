import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditUserRegisterComponent } from './edit-register.component';

describe('EditRegisterComponent', () => {
  let component: EditUserRegisterComponent;
  let fixture: ComponentFixture<EditUserRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditUserRegisterComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditUserRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
