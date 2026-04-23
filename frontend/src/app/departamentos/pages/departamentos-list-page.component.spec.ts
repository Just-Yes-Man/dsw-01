/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { DepartamentosErrorMapperService } from '../services/departamentos-error-mapper.service';
import { DepartamentosStoreService } from '../services/departamentos-store.service';
import { DepartamentosListPageComponent } from './departamentos-list-page.component';

declare const expect: any;

describe('DepartamentosListPageComponent', () => {
  let fixture: ComponentFixture<DepartamentosListPageComponent>;
  let storeStub: {
    loadPage: jasmine.Spy;
    delete: jasmine.Spy;
    refreshCurrentPage: jasmine.Spy;
    setError: jasmine.Spy;
    listState: () => { page: number; size: number; totalElements: number; loading: boolean; error: string | null };
    items: () => any[];
  };
  let authSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    storeStub = {
      loadPage: jasmine.createSpy('loadPage').and.returnValue(of({ page: 0, size: 10, totalElements: 0, items: [] })),
      delete: jasmine.createSpy('delete').and.returnValue(of(void 0)),
      refreshCurrentPage: jasmine.createSpy('refreshCurrentPage'),
      setError: jasmine.createSpy('setError'),
      listState: () => ({ page: 0, size: 10, totalElements: 0, loading: false, error: null }),
      items: () => []
    };

    authSpy = jasmine.createSpyObj<AuthService>('AuthService', ['logout']);
    authSpy.logout.and.returnValue(of(void 0));
    routerSpy = jasmine.createSpyObj<Router>('Router', ['navigateByUrl']);

    await TestBed.configureTestingModule({
      imports: [DepartamentosListPageComponent, RouterTestingModule],
      providers: [
        { provide: DepartamentosStoreService, useValue: storeStub },
        { provide: DepartamentosErrorMapperService, useValue: { toUserMessage: () => 'error' } },
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DepartamentosListPageComponent);
    fixture.detectChanges();
  });

  it('loads first page on init', () => {
    expect(storeStub.loadPage).toHaveBeenCalledWith(0);
  });
});
