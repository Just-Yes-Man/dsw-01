/// <reference types="jasmine" />

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { EmpleadosStoreService } from '../services/empleados-store.service';
import { EmpleadosErrorMapperService } from '../services/empleados-error-mapper.service';
import { EmpleadosListPageComponent } from './empleados-list-page.component';

declare const expect: any;

describe('EmpleadosListPageComponent', () => {
  let fixture: ComponentFixture<EmpleadosListPageComponent>;
  let storeStub: {
    loadPage: jasmine.Spy;
    setShowInactive: jasmine.Spy;
    deactivate: jasmine.Spy;
    refreshCurrentPage: jasmine.Spy;
    setError: jasmine.Spy;
    listState: () => { page: number; size: number; totalElements: number; loading: boolean; showInactive: boolean; error: string | null };
    items: () => any[];
  };
  let authSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    storeStub = {
      loadPage: jasmine.createSpy('loadPage').and.returnValue(of({ page: 0, size: 10, totalElements: 0, items: [] })),
      setShowInactive: jasmine.createSpy('setShowInactive'),
      deactivate: jasmine.createSpy('deactivate').and.returnValue(of({} as any)),
      refreshCurrentPage: jasmine.createSpy('refreshCurrentPage'),
      setError: jasmine.createSpy('setError'),
      listState: () => ({ page: 0, size: 10, totalElements: 0, loading: false, showInactive: false, error: null }),
      items: () => []
    };

    authSpy = jasmine.createSpyObj<AuthService>('AuthService', ['logout']);
    authSpy.logout.and.returnValue(of(void 0));
    routerSpy = jasmine.createSpyObj<Router>('Router', ['navigateByUrl']);

    await TestBed.configureTestingModule({
      imports: [EmpleadosListPageComponent, RouterTestingModule],
      providers: [
        { provide: EmpleadosStoreService, useValue: storeStub },
        { provide: EmpleadosErrorMapperService, useValue: { toUserMessage: () => 'error' } },
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EmpleadosListPageComponent);
    fixture.detectChanges();
  });

  it('loads first page on init', () => {
    expect(storeStub.loadPage).toHaveBeenCalledWith(0);
  });
});
