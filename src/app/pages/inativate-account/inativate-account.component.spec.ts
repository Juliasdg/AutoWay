import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InativateAccountComponent } from './inativate-account.component';

describe('InativateAccountComponent', () => {
  let component: InativateAccountComponent;
  let fixture: ComponentFixture<InativateAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InativateAccountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InativateAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
