/// <reference types="jasmine" />

import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { EmpleadosApiService } from './empleados-api.service';
import { EmpleadosStoreService } from './empleados-store.service';

declare const expect: any;

describe('EmpleadosStoreService', () => {
  let service: EmpleadosStoreService;
  let apiSpy: jasmine.SpyObj<EmpleadosApiService>;

  beforeEach(() => {
    apiSpy = jasmine.createSpyObj<EmpleadosApiService>('EmpleadosApiService', ['findAll', 'create', 'update']);
    apiSpy.findAll.and.returnValue(
      of({
        page: 0,
        size: 10,
        totalElements: 1,
        items: [
          {
            clave: 'EMP-1',
            nombre: 'Ana',
            direccion: 'Calle 1',
            telefono: '555',
            email: 'ana@example.com',
            estadoAcceso: 'ACTIVO',
            departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
          }
        ]
      })
    );

    TestBed.configureTestingModule({
      providers: [
        EmpleadosStoreService,
        { provide: EmpleadosApiService, useValue: apiSpy }
      ]
    });

    service = TestBed.inject(EmpleadosStoreService);
  });

  it('loads page and exposes active items by default', () => {
    service.loadPage(0).subscribe();

    expect(apiSpy.findAll).toHaveBeenCalledWith(0);
    expect(service.items().length).toBe(1);
    expect(service.listState().loading).toBeFalse();
  });
});
