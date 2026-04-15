/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmpleadoFormComponent } from './empleado-form.component';

declare const expect: any;

describe('EmpleadoFormComponent', () => {
  let fixture: ComponentFixture<EmpleadoFormComponent>;
  let component: EmpleadoFormComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmpleadoFormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(EmpleadoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('creates component', () => {
    expect(component).toBeTruthy();
  });

  it('emits save when form is valid', () => {
    const saveSpy = jasmine.createSpy('saveSpy');
    component.save.subscribe(saveSpy);

    component.form.setValue({
      nombre: 'Ana',
      direccion: 'Calle 1',
      telefono: '555',
      email: 'ana@example.com',
      password: 'ana123',
      estadoAcceso: 'ACTIVO',
      departamentoId: 1
    });

    component.submit();

    expect(saveSpy).toHaveBeenCalled();
  });
});
