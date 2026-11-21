// @ts-check
import { defineConfig } from '@playwright/test';
import dotenv from 'dotenv';

dotenv.config();

const BASE_URL = process.env.BASE_URL || 'http://localhost:8080';

export default defineConfig({
  testDir: './tests/specs',
  timeout: 15_000,
  retries: 0,
  reporter: [
    ['html', { outputFolder: 'playwright-report', open: 'never' }],
    ['list'],
  ],
  use: {
    baseURL: BASE_URL,
    extraHTTPHeaders: { 'Content-Type': 'application/json' },
  },
  projects: [
    { name: 'API – MEDELED GAP-EV02', testMatch: '**/*.spec.js' },
  ],
});
