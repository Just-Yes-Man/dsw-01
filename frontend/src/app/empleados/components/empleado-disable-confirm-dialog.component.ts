import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-empleado-disable-confirm-dialog',
  standalone: true,
  imports: [NgIf],
  templateUrl: './empleado-disable-confirm-dialog.component.html'
})
export class EmpleadoDisableConfirmDialogComponent {
  @Input() open = false;
  @Input() processing = false;
  @Input() empleadoClave: string | null = null;

  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}
