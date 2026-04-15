import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-departamento-delete-confirm-dialog',
  standalone: true,
  imports: [NgIf],
  templateUrl: './departamento-delete-confirm-dialog.component.html'
})
export class DepartamentoDeleteConfirmDialogComponent {
  @Input() open = false;
  @Input() processing = false;
  @Input() departamentoId: number | null = null;

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}
