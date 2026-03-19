/// <reference types="cypress" />

describe('Departamentos listado', () => {
  it('muestra departamentos paginados', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    }).as('session');

    cy.intercept('GET', '**/api/v1/departamentos?page=0', {
      statusCode: 200,
      body: {
        page: 0,
        size: 10,
        totalElements: 1,
        items: [
          {
            id: 1,
            nombre: 'Sistemas',
            estado: 'ACTIVO',
            creadoEn: '2026-03-19T00:00:00',
            actualizadoEn: '2026-03-19T00:00:00'
          }
        ]
      }
    }).as('list');

    cy.visit('/departamentos');
    cy.wait('@session');
    cy.wait('@list');
    cy.contains('h1', 'Departamentos').should('exist');
    cy.contains('td', 'Sistemas').should('exist');
  });
});
