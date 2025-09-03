import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CodePasswordComponent } from './code-password.component';

describe('CodePasswordComponent', () => {
  let component: CodePasswordComponent;
  let fixture: ComponentFixture<CodePasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CodePasswordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CodePasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
