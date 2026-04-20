/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DepartamentoFormComponent } from './departamento-form.component';

declare const expect: any;

describe('DepartamentoFormComponent', () => {
  let fixture: ComponentFixture<DepartamentoFormComponent>;
  let component: DepartamentoFormComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepartamentoFormComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(DepartamentoFormComponent);
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
      nombre: 'Finanzas'
    });

    component.submit();

    expect(saveSpy).toHaveBeenCalled();
  });
});
