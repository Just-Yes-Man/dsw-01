import { defineConfig } from 'cypress';

export default defineConfig({
  projectId: 'ghfqri',
  e2e: {
    baseUrl: process.env.CYPRESS_BASE_URL ?? 'http://localhost:8081',
    supportFile: 'cypress/support/e2e.ts',
    specPattern: 'cypress/e2e/**/*.cy.ts',
    video: false
  }
});
