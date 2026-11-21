// @ts-check
/**
 * SPECS/AUTH.SPEC.JS — AUTENTICACIÓN (HU-LOGIN)
 * Plan de pruebas GA9-220501096-AA1-EV02 – Sistema MEDELED
 *
 * Casos cubiertos:
 *   CP-AUTH-001  Registro exitoso               → 201 Created
 *   CP-AUTH-002  Registro duplicado             → 409 Conflict
 *   CP-AUTH-003  Registro contraseña débil      → 400
 *   CP-AUTH-004  Login válido                   → 200 OK
 *   CP-AUTH-005  Login contraseña incorrecta    → 401
 */
import { test, expect } from '@playwright/test';

const USUARIO = `tester_${Date.now()}`;
const CONTRASENA = 'Prueba123';

test.describe('SPECS/AUTH.SPEC.JS — AUTENTICACIÓN (HU-LOGIN)', () => {

  test('CP-AUTH-001 - Registro exitoso → 201 Created', async ({ request }) => {
    const res = await request.post('/api/auth/registro', {
      data: { usuario: USUARIO, contrasena: CONTRASENA },
    });
    expect(res.status()).toBe(201);
    const body = await res.json();
    expect(body.mensaje).toBeTruthy();
  });

  test('CP-AUTH-002 - Registro duplicado → 409 Conflict', async ({ request }) => {
    const usuario = `dup_${Date.now()}`;
    await request.post('/api/auth/registro', {
      data: { usuario, contrasena: CONTRASENA },
    });
    const res = await request.post('/api/auth/registro', {
      data: { usuario, contrasena: CONTRASENA },
    });
    expect(res.status()).toBe(409);
  });

  test('CP-AUTH-003 - Registro contraseña débil → 400', async ({ request }) => {
    const res = await request.post('/api/auth/registro', {
      data: { usuario: `debil_${Date.now()}`, contrasena: 'abc' },
    });
    expect(res.status()).toBe(400);
  });

  test('CP-AUTH-004 - Login válido → 200 OK', async ({ request }) => {
    const usuario = `login_${Date.now()}`;
    await request.post('/api/auth/registro', {
      data: { usuario, contrasena: CONTRASENA },
    });
    const res = await request.post('/api/auth/login', {
      data: { usuario, contrasena: CONTRASENA },
    });
    expect(res.status()).toBe(200);
  });

  test('CP-AUTH-005 - Login contraseña incorrecta → 401', async ({ request }) => {
    const res = await request.post('/api/auth/login', {
      data: { usuario: USUARIO, contrasena: 'Incorrecta99' },
    });
    expect(res.status()).toBe(401);
  });

});
