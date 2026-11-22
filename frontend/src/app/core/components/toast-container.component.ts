import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, ToastMessage } from '../services/notification.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toast-container" aria-live="polite" aria-atomic="true">
      @for (toast of notificationService.toasts(); track toast.id) {
        <div class="toast" [ngClass]="'toast-' + toast.type" role="alert">
          <div class="toast-content">
            @if (toast.titulo) {
              <strong class="toast-title">{{ toast.titulo }}</strong>
            }
            <span class="toast-message">{{ toast.mensaje }}</span>
          </div>
          <button
            type="button"
            class="toast-close"
            (click)="notificationService.dismiss(toast.id)"
            aria-label="Cerrar notificación"
          >
            &times;
          </button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 1.5rem;
      right: 1.5rem;
      z-index: 9999;
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      max-width: 420px;
      width: calc(100% - 3rem);
      pointer-events: none;
    }
    .toast {
      pointer-events: auto;
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: 0.75rem;
      padding: 0.85rem 1rem;
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      font-family: system-ui, -apple-system, sans-serif;
      font-size: 0.9rem;
      line-height: 1.4;
      animation: slideIn 0.25s ease-out;
      border-left: 5px solid transparent;
    }
    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
    .toast-content {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
      flex: 1;
    }
    .toast-title {
      font-weight: 600;
      font-size: 0.92rem;
    }
    .toast-message {
      word-break: break-word;
    }
    .toast-close {
      background: none;
      border: none;
      font-size: 1.3rem;
      line-height: 1;
      cursor: pointer;
      padding: 0;
      color: inherit;
      opacity: 0.7;
      transition: opacity 0.15s;
    }
    .toast-close:hover {
      opacity: 1;
    }
    .toast-success {
      background: #f0fdf4;
      color: #166534;
      border-color: #22c55e;
    }
    .toast-error {
      background: #fef2f2;
      color: #991b1b;
      border-color: #ef4444;
    }
    .toast-warning {
      background: #fffbeb;
      color: #92400e;
      border-color: #f59e0b;
    }
    .toast-info {
      background: #eff6ff;
      color: #1e40af;
      border-color: #3b82f6;
    }
  `]
})
export class ToastContainerComponent {
  readonly notificationService = inject(NotificationService);
}
