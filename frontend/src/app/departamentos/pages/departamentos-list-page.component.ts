import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { DepartamentoDeleteConfirmDialogComponent } from '../components/departamento-delete-confirm-dialog.component';
import { DepartamentosErrorMapperService } from '../services/departamentos-error-mapper.service';
import { DepartamentosStoreService } from '../services/departamentos-store.service';

@Component({
  selector: 'app-departamentos-list-page',
  standalone: true,
  imports: [NgIf, NgFor, DepartamentoDeleteConfirmDialogComponent],
  templateUrl: './departamentos-list-page.component.html',
  styleUrl: './departamentos-list-page.component.css'
})
export class DepartamentosListPageComponent implements OnInit {
  confirmId: number | null = null;
  confirmProcessing = false;

  constructor(
    public readonly store: DepartamentosStoreService,
    private readonly errorMapper: DepartamentosErrorMapperService,
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

  goToCreate(): void {
    this.router.navigateByUrl('/departamentos/nuevo');
  }

  goToEdit(id: number): void {
    this.router.navigateByUrl(`/departamentos/${id}/editar`);
  }

  openDelete(id: number): void {
    this.confirmId = id;
  }

  closeDelete(): void {
    this.confirmId = null;
    this.confirmProcessing = false;
  }

  confirmDelete(): void {
    if (!this.confirmId || this.confirmProcessing) {
      return;
    }

    this.confirmProcessing = true;
    this.store.delete(this.confirmId).subscribe({
      next: () => this.closeDelete(),
      error: (error) => {
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
