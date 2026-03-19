import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { EmpleadosStoreService } from '../services/empleados-store.service';
import { EmpleadosErrorMapperService } from '../services/empleados-error-mapper.service';
import { EmpleadoDisableConfirmDialogComponent } from '../components/empleado-disable-confirm-dialog.component';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-empleados-list-page',
  standalone: true,
  imports: [NgIf, NgFor, EmpleadoDisableConfirmDialogComponent],
  templateUrl: './empleados-list-page.component.html',
  styleUrl: './empleados-list-page.component.css'
})
export class EmpleadosListPageComponent implements OnInit {
  confirmClave: string | null = null;
  confirmProcessing = false;

  constructor(
    public readonly store: EmpleadosStoreService,
    private readonly errorMapper: EmpleadosErrorMapperService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.load(0);
  }

  load(page: number): void {
    this.store.loadPage(page).subscribe({
      error: () => undefined
    });
  }

  toggleInactive(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.store.setShowInactive(input.checked);
  }

  goToCreate(): void {
    this.router.navigateByUrl('/empleados/nuevo');
  }

  goToEdit(clave: string): void {
    this.router.navigateByUrl(`/empleados/${clave}/editar`);
  }

  openDeactivate(clave: string): void {
    this.confirmClave = clave;
  }

  closeDeactivate(): void {
    this.confirmClave = null;
    this.confirmProcessing = false;
  }

  confirmDeactivate(): void {
    if (!this.confirmClave) {
      return;
    }

    this.confirmProcessing = true;
    this.store.deactivate(this.confirmClave).subscribe({
      next: () => this.closeDeactivate(),
      error: (error) => {
        this.store.refreshCurrentPage();
        this.confirmProcessing = false;
        this.store.setError(this.errorMapper.toUserMessage(error));
      }
    });
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: () => this.router.navigateByUrl('/login')
    });
  }
}
