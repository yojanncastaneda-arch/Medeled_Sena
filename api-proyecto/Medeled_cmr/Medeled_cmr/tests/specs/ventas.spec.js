// @ts-check
/**
 * SPECS/VENTAS.SPEC.JS — VENTAS (HU-003, HU-004)
 * Plan de pruebas GA9-220501096-AA1-EV02 – Sistema MEDELED
 *
 * Casos cubiertos:
 *   CP-V-001  Registrar venta              → 201 Created    (+)
 *   CP-V-002  Venta cliente inexistente    → 400/404        (−)
 *   CP-V-003  Listar ventas                → array JSON     (+)
 *   CP-V-004  Consultar venta por id       → datos correctos(+)
 *   CP-V-005  Consultar venta inexistente  → 404            (−)
 *   CP-V-006  Ventas del cliente           → array JSON     (+)
 */
import { test, expect } from '@playwright/test';

const CLIENTE_BASE = {
  nombre: 'Laura Gómez',
  producto: 'Panel solar 550W',
  fechaCompra: '2026-09-01',
  estado: 'pendiente',
};

const ventaBase = (idCliente) => ({
  idCliente,
  producto: 'Panel solar 450W',
  cantidad: 4,
  precioUnitario: 850000.00,
  fechaVenta: '2026-09-16',
  estado: 'pendiente',
});

/** Crea un cliente auxiliar y devuelve su id. */
async function crearCliente(request) {
  const res = await request.post('/api/clientes', { data: CLIENTE_BASE });
  expect(res.status()).toBe(201);
  return (await res.json()).idCliente;
}

test.describe('SPECS/VENTAS.SPEC.JS — VENTAS (HU-003, HU-004)', () => {

  test('CP-V-001 - Registrar venta → 201 Created', async ({ request }) => {
    const idCliente = await crearCliente(request);
    const res = await request.post('/api/ventas', { data: ventaBase(idCliente) });
    expect(res.status()).toBe(201);
    const body = await res.json();
    expect(body.idVenta).toBeGreaterThan(0);
    expect(body.idCliente).toBe(idCliente);
  });

  test('CP-V-002 - Venta cliente inexistente → 400/404', async ({ request }) => {
    const res = await request.post('/api/ventas', { data: ventaBase(999999) });
    expect([400, 404]).toContain(res.status());
  });

  test('CP-V-003 - Listar ventas → array JSON', async ({ request }) => {
    const res = await request.get('/api/ventas');
    expect(res.status()).toBe(200);
    expect(Array.isArray(await res.json())).toBe(true);
  });

  test('CP-V-004 - Consultar venta por id → datos correctos', async ({ request }) => {
    const idCliente = await crearCliente(request);
    const created = await request.post('/api/ventas', { data: ventaBase(idCliente) });
    const { idVenta, producto } = await created.json();

    const res = await request.get(`/api/ventas/${idVenta}`);
    expect(res.status()).toBe(200);
    expect((await res.json()).producto).toBe(producto);
  });

  test('CP-V-005 - Consultar venta inexistente → 404', async ({ request }) => {
    const res = await request.get('/api/ventas/999999');
    expect(res.status()).toBe(404);
  });

  test('CP-V-006 - Ventas del cliente → array JSON', async ({ request }) => {
    const idCliente = await crearCliente(request);
    await request.post('/api/ventas', { data: ventaBase(idCliente) });

    const res = await request.get(`/api/clientes/${idCliente}/ventas`);
    expect(res.status()).toBe(200);
    const body = await res.json();
    expect(Array.isArray(body)).toBe(true);
    expect(body.length).toBeGreaterThan(0);
  });

});
