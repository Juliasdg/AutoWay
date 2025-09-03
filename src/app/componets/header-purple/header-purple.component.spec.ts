import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderPurpleComponent } from './header-purple.component';

describe('HeaderPurpleComponent', () => {
  let component: HeaderPurpleComponent;
  let fixture: ComponentFixture<HeaderPurpleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderPurpleComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderPurpleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
