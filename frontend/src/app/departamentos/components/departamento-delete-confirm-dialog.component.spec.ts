/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DepartamentoDeleteConfirmDialogComponent } from './departamento-delete-confirm-dialog.component';

declare const expect: any;

describe('DepartamentoDeleteConfirmDialogComponent', () => {
  let fixture: ComponentFixture<DepartamentoDeleteConfirmDialogComponent>;
  let component: DepartamentoDeleteConfirmDialogComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepartamentoDeleteConfirmDialogComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(DepartamentoDeleteConfirmDialogComponent);
    component = fixture.componentInstance;
    component.open = true;
    component.departamentoId = 1;
    fixture.detectChanges();
  });

  it('renders confirm dialog when open', () => {
    expect(fixture.nativeElement.textContent).toContain('Eliminar departamento');
    expect(fixture.nativeElement.textContent).toContain('1');
  });
});
