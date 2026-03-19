/// <reference types="jasmine" />

import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { DepartamentosApiService } from './departamentos-api.service';
import { DepartamentosStoreService } from './departamentos-store.service';

declare const expect: any;

describe('DepartamentosStoreService', () => {
  let service: DepartamentosStoreService;
  let apiSpy: jasmine.SpyObj<DepartamentosApiService>;

  beforeEach(() => {
    apiSpy = jasmine.createSpyObj<DepartamentosApiService>('DepartamentosApiService', ['findAll', 'findById', 'create', 'update', 'delete']);
    apiSpy.findAll.and.returnValue(
      of({
        page: 0,
        size: 10,
        totalElements: 1,
        items: [
          {
            id: 1,
            nombre: 'Sistemas',
            estado: 'ACTIVO',
            creadoEn: '2026-03-19T00:00:00',
            actualizadoEn: '2026-03-19T00:00:00'
          }
        ]
      })
    );

    TestBed.configureTestingModule({
      providers: [
        DepartamentosStoreService,
        { provide: DepartamentosApiService, useValue: apiSpy }
      ]
    });

    service = TestBed.inject(DepartamentosStoreService);
  });

  it('loads first page and sets list state', () => {
    service.loadPage(0).subscribe();

    expect(apiSpy.findAll).toHaveBeenCalledWith(0);
    expect(service.items().length).toBe(1);
    expect(service.listState().loading).toBeFalse();
  });
});
