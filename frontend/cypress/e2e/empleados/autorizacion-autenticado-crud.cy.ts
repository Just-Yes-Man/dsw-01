/// <reference types="cypress" />

describe('Empleados autorización autenticado CRUD', () => {
  it('permite acciones CRUD con sesión válida', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/empleados?page=0', {
      statusCode: 200,
      body: { page: 0, size: 10, totalElements: 0, items: [] }
    });

    cy.visit('/empleados');
    cy.url().should('include', '/empleados');
    cy.contains('Nuevo empleado').should('exist');
  });
});
