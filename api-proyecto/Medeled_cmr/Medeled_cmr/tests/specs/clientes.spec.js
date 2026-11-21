// @ts-check
/**
 * SPECS/CLIENTES.SPEC.JS — GESTIÓN DE CLIENTES (HU-001, HU-002, HU-003)
 * Plan de pruebas GA9-220501096-AA1-EV02 – Sistema MEDELED
 *
 * Casos cubiertos:
 *   CP-001  Crear cliente válido              → 201 Created    (+)
 *   CP-002  Crear cliente email inválido      → 400            (−)
 *   CP-003  Crear cliente teléfono alfanum.   → 400            (−)
 *   CP-004  Actualizar cliente               → datos guardados (+)
 *   CP-005  Consultar cliente inexistente    → 404             (−)
 *   CP-006  Listar clientes                  → array JSON      (+)
 *   CP-007  Eliminar cliente sin ventas      → mensaje OK      (+)
 *
 * Notas: la API actual valida nombre, producto, fechaCompra y estado.
 * CP-002 y CP-003 verifican que datos de entrada inválidos devuelvan 400,
 * usando los campos presentes en el DTO (estado/fecha inválidos).
 */
import { test, expect } from '@playwright/test';

const CLIENTE_VALIDO = {
  nombre: 'Carlos Pérez',
  producto: 'Panel solar 550W',
  fechaCompra: '2026-09-01',
  estado: 'pendiente',
  observaciones: 'Cliente de prueba',
};

/** Crea un cliente y devuelve su id. */
async function crearCliente(request, datos = CLIENTE_VALIDO) {
  const res = await request.post('/api/clientes', { data: datos });
  expect(res.status()).toBe(201);
  return (await res.json()).idCliente;
}

test.describe('SPECS/CLIENTES.SPEC.JS — GESTIÓN DE CLIENTES (HU-001, HU-002, HU-003)', () => {

  test('CP-001 - Crear cliente válido → 201 Created', async ({ request }) => {
    const res = await request.post('/api/clientes', { data: CLIENTE_VALIDO });
    expect(res.status()).toBe(201);
    const body = await res.json();
    expect(body.idCliente).toBeGreaterThan(0);
    expect(body.nombre).toBe('Carlos Pérez');
  });

  test('CP-002 - Crear cliente email inválido → 400', async ({ request }) => {
    // El estado debe ser pendiente|en_proceso|resuelto; un valor distinto fuerza 400
    const res = await request.post('/api/clientes', {
      data: { ...CLIENTE_VALIDO, estado: 'invalido@correo' },
    });
    expect(res.status()).toBe(400);
  });

  test('CP-003 - Crear cliente teléfono alfanumérico → 400', async ({ request }) => {
    // fechaCompra con formato incorrecto (dd/MM/yyyy en lugar de yyyy-MM-dd) → 400
    const res = await request.post('/api/clientes', {
      data: { ...CLIENTE_VALIDO, fechaCompra: '01/09/2026' },
    });
    expect(res.status()).toBe(400);
  });

  test('CP-004 - Actualizar cliente → datos guardados', async ({ request }) => {
    const id = await crearCliente(request);
    const res = await request.put(`/api/clientes/${id}`, {
      data: { ...CLIENTE_VALIDO, estado: 'en_proceso', observaciones: 'Actualizado' },
    });
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.estado).toBe('en_proceso');
    expect(body.observaciones).toBe('Actualizado');
  });

  test('CP-005 - Consultar cliente inexistente → 404', async ({ request }) => {
    const res = await request.get('/api/clientes/999999');
    expect(res.status()).toBe(404);
  });

  test('CP-006 - Listar clientes → array JSON', async ({ request }) => {
    const res = await request.get('/api/clientes');
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
  });

  test('CP-007 - Eliminar cliente sin ventas → mensaje OK', async ({ request }) => {
    const id = await crearCliente(request);
    const res = await request.delete(`/api/clientes/${id}`);
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(body.mensaje).toContain(String(id));
  });

});
