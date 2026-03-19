/// <reference types="cypress" />

describe('Auth logout', () => {
  it('logs out and blocks protected route access', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: {
        authenticated: true,
        empleadoClave: 'EMP-1',
        email: 'ana@example.com',
        expiresAt: '2026-03-18T10:00:00Z'
      }
    }).as('sessionValid');

    cy.intercept('POST', '**/api/v1/auth/logout', {
      statusCode: 204,
      body: {}
    }).as('logout');

    cy.intercept('GET', '**/api/v1/empleados*', {
      statusCode: 200,
      body: {
        items: [],
        page: 0,
        size: 10,
        totalElements: 0
      }
    }).as('empleadosList');

    cy.visit('/empleados');
    cy.wait('@sessionValid');
    cy.wait('@empleadosList');
    cy.url().should('include', '/empleados');

    cy.contains('button', 'Cerrar sesión').click();
    cy.wait('@logout');
    cy.url().should('include', '/login');

    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 401,
      body: { code: 'UNAUTHORIZED', message: 'Sesión no válida' }
    }).as('sessionCheck');

    cy.visit('/empleados');
    cy.wait('@sessionCheck');
    cy.url().should('include', '/login');
  });
});
