import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuditoraGlobalService } from '../../core/api/generated/api/auditora-global.service';
import { NotificationService } from '../../core/services/notification.service';
import { VerificacionIntegridadResponse } from '../../core/api/generated/model/verificacion-integridad-response';
import { AlertaSeguridadResponse } from '../../core/api/generated/model/alerta-seguridad-response';

@Component({
  selector: 'app-auditoria',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="auditoria-container">
      <header class="header">
        <div>
          <h2>Auditoría Forense y Seguridad del Sistema</h2>
          <p class="subtitle">Cadena criptográfica append-only SHA-256, alertas de seguridad en tiempo real y trazabilidad (UC-PROV-29, RN-10)</p>
        </div>
      </header>

      <!-- TABS -->
      <div class="tabs-bar">
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'integridad'"
          (click)="tabActiva.set('integridad')"
        >
          🔐 Cadena Criptográfica SHA-256
        </button>
        <button
          type="button"
          class="tab-btn"
          [class.active]="tabActiva() === 'alertas'"
          (click)="tabActiva.set('alertas')"
        >
          🚨 Alertas de Seguridad en Tiempo Real
        </button>
      </div>

      <!-- TAB 1: INTEGRIDAD CRIPTOGRÁFICA -->
      @if (tabActiva() === 'integridad') {
        <div class="tab-content">
          <div class="audit-action-card">
            <div class="card-info">
              <h3>Verificación de la Cadena de Integridad (SHA-256 Encadenado)</h3>
              <p class="card-desc">
                Recorre matemáticamente todos los eventos registrados en la bitácora append-only, calculando el hash
                SHA-256 encadenado desde el bloque génesis para detectar cualquier manipulación o borrado directo en la base de datos PostgreSQL.
              </p>
            </div>
            <button
              type="button"
              [disabled]="auditando()"
              class="btn-primary btn-audit"
              (click)="auditarCadena()"
            >
              @if (auditando()) {
                <span>Verificando Integridad Criptográfica...</span>
              } @else {
                <span>🛡️ Ejecutar Auditoría de Integridad</span>
              }
            </button>
          </div>

          @if (resultadoIntegridad()) {
            <div
              class="verif-card"
              [class.card-integra]="resultadoIntegridad()?.estado === 'CADENA_INTEGRA_VERIFICADA' || resultadoIntegridad()?.estado === 'INTEGRA'"
              [class.card-corrupta]="resultadoIntegridad()?.estado !== 'CADENA_INTEGRA_VERIFICADA' && resultadoIntegridad()?.estado !== 'INTEGRA'"
            >
              <div class="res-header">
                <h3>Dictamen Forense de Integridad</h3>
                <span class="badge" [class.badge-success]="resultadoIntegridad()?.estado === 'CADENA_INTEGRA_VERIFICADA' || resultadoIntegridad()?.estado === 'INTEGRA'" [class.badge-danger]="resultadoIntegridad()?.estado !== 'CADENA_INTEGRA_VERIFICADA' && resultadoIntegridad()?.estado !== 'INTEGRA'">
                  {{ resultadoIntegridad()?.estado }}
                </span>
              </div>

              <div class="metrics-grid">
                <div class="metric-box">
                  <span class="metric-label">Eventos Validados</span>
                  <span class="metric-value">{{ resultadoIntegridad()?.totalEventosVerificados }}</span>
                </div>
                <div class="metric-box">
                  <span class="metric-label">Fecha de Verificación</span>
                  <span class="metric-value date-val">{{ resultadoIntegridad()?.fechaVerificacion }}</span>
                </div>
              </div>

              <p class="res-detail">{{ resultadoIntegridad()?.detalle }}</p>
            </div>
          }
        </div>
      }

      <!-- TAB 2: ALERTAS DE SEGURIDAD -->
      @if (tabActiva() === 'alertas') {
        <div class="tab-content">
          <div class="content-toolbar">
            <h3>Bandeja de Incidentes y Alertas de Seguridad</h3>
            <button type="button" class="btn-secondary" (click)="cargarAlertas()">
              🔄 Refrescar Alertas
            </button>
          </div>

          <div class="alertas-list">
            @if (alertas().length === 0) {
              <div class="empty-state">
                <p>✓ No se registran incidentes ni alertas de seguridad pendientes de atención.</p>
              </div>
            }
            @for (al of alertas(); track al.id) {
              <div class="alerta-item" [ngClass]="'alerta-' + al.severidad?.toLowerCase()">
                <div class="alerta-header">
                  <div class="alerta-title">
                    <span class="badge" [ngClass]="'badge-' + al.severidad?.toLowerCase()">
                      {{ al.severidad }}
                    </span>
                    <strong>{{ al.tipoAlerta }}</strong>
                  </div>
                  <span class="alerta-fecha">{{ al.fechaAlerta }}</span>
                </div>

                <p class="alerta-desc">{{ al.descripcionEvento }}</p>

                <div class="alerta-footer">
                  <span class="alerta-meta">
                    Usuario: <strong>{{ al.usernameInvolucrado || 'Anónimo' }}</strong> |
                    IP Origen: <strong>{{ al.ipOrigen || '127.0.0.1' }}</strong> |
                    ID Correlación: <code>{{ al.correlationId || '-' }}</code>
                  </span>

                  <button type="button" class="btn-sm btn-action" (click)="marcarLeida(al.id!)">
                    Marcar Atendida
                  </button>
                </div>
              </div>
            }
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .auditoria-container {
      max-width: 1000px;
      margin: 0 auto;
      font-family: system-ui, -apple-system, sans-serif;
    }
    .header { margin-bottom: 1.5rem; }
    h2 { margin: 0; color: #1e293b; font-size: 1.4rem; }
    .subtitle { margin: 0.25rem 0 0; color: #64748b; font-size: 0.88rem; }
    .tabs-bar {
      display: flex;
      gap: 0.5rem;
      border-bottom: 2px solid #e2e8f0;
      margin-bottom: 1.5rem;
    }
    .tab-btn {
      padding: 0.65rem 1.25rem;
      border: none;
      background: none;
      font-size: 0.95rem;
      font-weight: 600;
      color: #64748b;
      cursor: pointer;
      border-bottom: 2px solid transparent;
      margin-bottom: -2px;
      transition: all 0.15s;
    }
    .tab-btn:hover { color: #1e293b; }
    .tab-btn.active { color: #2563eb; border-bottom-color: #2563eb; }
    .audit-action-card {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 1.5rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1.5rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
      margin-bottom: 1.5rem;
    }
    .card-info h3 { margin: 0 0 0.5rem; color: #1e293b; font-size: 1.1rem; }
    .card-desc { margin: 0; font-size: 0.88rem; color: #64748b; line-height: 1.45; }
    .btn-audit { flex-shrink: 0; padding: 0.75rem 1.25rem; font-size: 0.95rem; }
    .btn-primary { background: #2563eb; color: white; border: none; border-radius: 4px; font-weight: 600; cursor: pointer; }
    .btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
    .btn-secondary { background: #e2e8f0; color: #334155; border: none; padding: 0.45rem 0.85rem; border-radius: 4px; font-size: 0.85rem; cursor: pointer; font-weight: 500; }
    .verif-card {
      border-radius: 8px;
      padding: 1.5rem;
      border: 1px solid transparent;
    }
    .card-integra { background: #f0fdf4; border-color: #bbf7d0; }
    .card-corrupta { background: #fef2f2; border-color: #fecaca; }
    .res-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
    .res-header h3 { margin: 0; font-size: 1.15rem; color: #166534; }
    .metrics-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem; }
    .metric-box { background: white; padding: 1rem; border-radius: 6px; border: 1px solid #e2e8f0; text-align: center; }
    .metric-label { display: block; font-size: 0.8rem; color: #64748b; font-weight: 600; text-transform: uppercase; margin-bottom: 0.25rem; }
    .metric-value { font-size: 1.4rem; font-weight: 700; color: #1e293b; }
    .date-val { font-size: 1rem; font-weight: 600; }
    .res-detail { margin: 0; font-size: 0.9rem; color: #334155; line-height: 1.45; }

    /* ALERTAS */
    .content-toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; }
    .content-toolbar h3 { margin: 0; font-size: 1.1rem; color: #1e293b; }
    .alertas-list { display: flex; flex-direction: column; gap: 0.85rem; }
    .alerta-item {
      background: white;
      border: 1px solid #e2e8f0;
      border-left: 5px solid #cbd5e1;
      border-radius: 8px;
      padding: 1rem 1.25rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    }
    .alerta-critica { border-left-color: #ef4444; }
    .alerta-alta { border-left-color: #f97316; }
    .alerta-media { border-left-color: #f59e0b; }
    .alerta-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem; }
    .alerta-title { display: flex; align-items: center; gap: 0.5rem; }
    .alerta-fecha { font-size: 0.8rem; color: #64748b; }
    .alerta-desc { margin: 0 0 0.75rem; font-size: 0.88rem; line-height: 1.45; color: #1e293b; }
    .alerta-footer { display: flex; justify-content: space-between; align-items: center; padding-top: 0.5rem; border-top: 1px solid #f1f5f9; font-size: 0.8rem; color: #64748b; }
    .btn-sm { padding: 0.25rem 0.55rem; font-size: 0.78rem; border-radius: 4px; border: none; cursor: pointer; }
    .btn-action { background: #eff6ff; color: #2563eb; border: 1px solid #bfdbfe; font-weight: 500; }
    .badge { padding: 0.2rem 0.5rem; border-radius: 4px; font-size: 0.75rem; font-weight: 600; }
    .badge-success { background: #dcfce7; color: #166534; }
    .badge-danger, .badge-critica { background: #fee2e2; color: #991b1b; }
    .badge-alta { background: #ffedd5; color: #c2410c; }
    .badge-media { background: #fef9c3; color: #854d0e; }
    .empty-state { background: white; padding: 2rem; border-radius: 8px; text-align: center; color: #166534; border: 1px solid #bbf7d0; background: #f0fdf4; font-weight: 500; }
  `]
})
export class AuditoriaComponent implements OnInit {
  private readonly auditoriaService = inject(AuditoraGlobalService);
  private readonly notificationService = inject(NotificationService);

  readonly tabActiva = signal<'integridad' | 'alertas'>('integridad');
  readonly auditando = signal(false);

  readonly resultadoIntegridad = signal<VerificacionIntegridadResponse | null>(null);
  readonly alertas = signal<AlertaSeguridadResponse[]>([]);

  ngOnInit(): void {
    this.cargarAlertas();
  }

  auditarCadena(): void {
    this.auditando.set(true);
    this.auditoriaService.apiAuditoriaVerificarIntegridadGet().subscribe({
      next: (res) => {
        this.auditando.set(false);
        this.resultadoIntegridad.set(res);
        if (res.estado === 'CADENA_INTEGRA_VERIFICADA' || res.estado === 'INTEGRA') {
          this.notificationService.showSuccess('Cadena criptográfica SHA-256 íntegra. Sin alteraciones.');
        } else {
          this.notificationService.showError('¡ALERTA FORENSE! Discrepancia detectada en la cadena criptográfica.');
        }
      },
      error: () => this.auditando.set(false)
    });
  }

  cargarAlertas(): void {
    this.auditoriaService.listarAlertasSeguridadPendientes().subscribe({
      next: (data) => {
        this.alertas.set(data);
      }
    });
  }

  marcarLeida(id: number): void {
    this.auditoriaService.marcarAlertaSeguridadLeida(id).subscribe({
      next: () => {
        this.notificationService.showSuccess('Alerta de seguridad atendida.');
        this.cargarAlertas();
      }
    });
  }
}
