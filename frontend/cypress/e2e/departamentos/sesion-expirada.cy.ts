/// <reference types="cypress" />

describe('Departamentos sesión expirada', () => {
  it('redirige a login cuando API responde 401', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/departamentos?page=0', {
      statusCode: 401,
      body: { code: 'UNAUTHORIZED', message: 'Credenciales inválidas' }
    }).as('unauthorizedList');

    cy.visit('/departamentos');
    cy.wait('@unauthorizedList');
    cy.url().should('include', '/login');
  });
});
