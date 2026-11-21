// @ts-check
/**
 * SPECS/PENDIENTES.SPEC.JS — MÓDULOS PENDIENTES DE IMPLEMENTAR
 * Plan de pruebas GA9-220501096-AA1-EV02 – Sistema MEDELED
 *
 * Estos casos corresponden a las HU-007 a HU-020 que aún no tienen
 * endpoints implementados.  Se marcan como test.skip para que el
 * reporte los muestre como "pendientes" sin fallar la suite.
 *
 * Cuando se implemente cada módulo:
 *   1. Reemplazar test.skip por test.
 *   2. Completar los pasos y aserciones según el plan de pruebas.
 */
import { test } from '@playwright/test';

// ── Seguimiento y servicio técnico ──────────────────────────────────────────

test.skip('CP-013 - Agendar visita técnica (HU-007)', () => {});
test.skip('CP-014 - Horario ocupado → 409 (HU-007)', () => {});
test.skip('CP-015 - Ticket automático al agendar visita (HU-008)', () => {});
test.skip('CP-016 - Cambiar estado del ticket (HU-008)', () => {});
test.skip('CP-017 - Crear orden de servicio prioridad Urgente (HU-009)', () => {});
test.skip('CP-018 - Color rojo 3 visitas previas (HU-009)', () => {});
test.skip('CP-019 - Registrar informe técnico completo (HU-010)', () => {});
test.skip('CP-020 - Guardar informe sin campos obligatorios → 400 (HU-010)', () => {});
test.skip('CP-021 - Consultar historial y descargar PDF (HU-011)', () => {});
test.skip('CP-022 - Notificación 24 h antes de la visita (HU-012)', () => {});
test.skip('CP-023 - Cliente solicita reagendar (HU-012)', () => {});

// ── Módulo de PQRS ───────────────────────────────────────────────────────────

test.skip('CP-024 - PQRS sin sesión → radicado único (HU-013)', () => {});
test.skip('CP-025 - Adjunto > 5 MB → 400 (HU-013)', () => {});
test.skip('CP-026 - Clasificar y asignar PQRS (HU-014)', () => {});
test.skip('CP-027 - Generar orden desde PQRS técnica (HU-015)', () => {});

// ── Inventario de productos ──────────────────────────────────────────────────

test.skip('CP-028 - Agregar producto nuevo → marca morada (HU-016)', () => {});
test.skip('CP-029 - Código duplicado → 409 (HU-016)', () => {});
test.skip('CP-030 - Vigencia marca morada 2 meses (HU-016)', () => {});

// ── Sistema de notificaciones ────────────────────────────────────────────────

test.skip('CP-031 - Envío promocional por segmento (HU-017)', () => {});
test.skip('CP-032 - Programar envío diferido (HU-017)', () => {});
test.skip('CP-033 - Confirmación automática de compra (HU-018)', () => {});
test.skip('CP-034 - Encuesta satisfacción 24 h después (HU-019)', () => {});
test.skip('CP-035 - Alerta calificación < 3 estrellas (HU-019)', () => {});

// ── Cronograma de actividades ────────────────────────────────────────────────

test.skip('CP-036 - Registrar evento y asignar personal (HU-020)', () => {});
test.skip('CP-037 - Alertas 7 días y 1 día previos al evento (HU-020)', () => {});
test.skip('CP-038 - Registrar leads del evento (HU-020)', () => {});
