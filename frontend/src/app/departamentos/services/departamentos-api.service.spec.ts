/// <reference types="jasmine" />

import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../environments/environment';
import { DepartamentosApiService } from './departamentos-api.service';

declare const expect: any;

describe('DepartamentosApiService', () => {
  let service: DepartamentosApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DepartamentosApiService]
    });

    service = TestBed.inject(DepartamentosApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('calls paginated list endpoint', () => {
    service.findAll(0).subscribe();

    const request = httpMock.expectOne(`${environment.apiBaseUrl}${environment.departamentosPath}?page=0`);
    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBeTrue();
    request.flush({ page: 0, size: 10, totalElements: 0, items: [] });
  });
});
