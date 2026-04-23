/// <reference types="cypress" />

describe('Departamentos listado vacío/error', () => {
  it('muestra estado vacío', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/departamentos?page=0', {
      statusCode: 200,
      body: {
        page: 0,
        size: 10,
        totalElements: 0,
        items: []
      }
    });

    cy.visit('/departamentos');
    cy.contains('No hay departamentos para mostrar.').should('exist');
  });

  it('muestra estado de error', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/departamentos?page=0', {
      statusCode: 500,
      body: { code: 'INTERNAL_ERROR', message: 'Error interno' }
    });

    cy.visit('/departamentos');
    cy.contains('No fue posible cargar departamentos.').should('exist');
  });
});
