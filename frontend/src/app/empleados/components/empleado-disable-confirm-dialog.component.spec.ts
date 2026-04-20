/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmpleadoDisableConfirmDialogComponent } from './empleado-disable-confirm-dialog.component';

declare const expect: any;

describe('EmpleadoDisableConfirmDialogComponent', () => {
  let fixture: ComponentFixture<EmpleadoDisableConfirmDialogComponent>;
  let component: EmpleadoDisableConfirmDialogComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpleadoDisableConfirmDialogComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(EmpleadoDisableConfirmDialogComponent);
    component = fixture.componentInstance;
    component.open = true;
    component.empleadoClave = 'EMP-1';
    fixture.detectChanges();
  });

  it('renders confirm dialog when open', () => {
    expect(fixture.nativeElement.textContent).toContain('Desactivar empleado');
    expect(fixture.nativeElement.textContent).toContain('EMP-1');
  });
});
