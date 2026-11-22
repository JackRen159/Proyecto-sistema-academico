import { Injectable, signal } from '@angular/core';

export type ToastType = 'success' | 'error' | 'warning' | 'info';

export interface ToastMessage {
  id: string;
  type: ToastType;
  titulo?: string;
  mensaje: string;
  duracionMs?: number;
}

/**
 * Servicio Centralizado de Notificaciones y Toasts.
 * Permite emitir mensajes flotantes de éxito, error, advertencia e información
 * con auto-cierre configurable y cierre manual.
 */
@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly toastsSignal = signal<ToastMessage[]>([]);
  readonly toasts = this.toastsSignal.asReadonly();

  show(type: ToastType, mensaje: string, titulo?: string, duracionMs = 5000): void {
    const id = `${Date.now()}-${Math.random().toString(36).substring(2, 7)}`;
    const nuevoToast: ToastMessage = { id, type, mensaje, titulo, duracionMs };

    this.toastsSignal.update(toasts => [...toasts, nuevoToast]);

    if (duracionMs > 0) {
      setTimeout(() => {
        this.dismiss(id);
      }, duracionMs);
    }
  }

  showSuccess(mensaje: string, titulo = 'Operación Exitosa'): void {
    this.show('success', mensaje, titulo, 4000);
  }

  showError(mensaje: string, titulo = 'Error'): void {
    this.show('error', mensaje, titulo, 6000);
  }

  showWarning(mensaje: string, titulo = 'Atención'): void {
    this.show('warning', mensaje, titulo, 5000);
  }

  showInfo(mensaje: string, titulo = 'Información'): void {
    this.show('info', mensaje, titulo, 4000);
  }

  dismiss(id: string): void {
    this.toastsSignal.update(toasts => toasts.filter(t => t.id !== id));
  }

  clear(): void {
    this.toastsSignal.set([]);
  }
}
