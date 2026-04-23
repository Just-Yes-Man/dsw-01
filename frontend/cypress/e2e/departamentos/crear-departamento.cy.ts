/// <reference types="cypress" />

describe('Departamentos crear', () => {
  it('crea departamento y regresa al listado', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('POST', '**/api/v1/departamentos', {
      statusCode: 201,
      body: {
        id: 11,
        nombre: 'Compras',
        estado: 'ACTIVO',
        creadoEn: '2026-03-19T00:00:00',
        actualizadoEn: '2026-03-19T00:00:00'
      }
    }).as('create');

    cy.intercept('GET', '**/api/v1/departamentos?page=0', {
      statusCode: 200,
      body: {
        page: 0,
        size: 10,
        totalElements: 1,
        items: [
          {
            id: 11,
            nombre: 'Compras',
            estado: 'ACTIVO',
            creadoEn: '2026-03-19T00:00:00',
            actualizadoEn: '2026-03-19T00:00:00'
          }
        ]
      }
    }).as('listAfterCreate');

    cy.visit('/departamentos/nuevo');
    cy.get('#nombre').type('Compras');
    cy.contains('button', 'Guardar').click();
    cy.wait('@create');
    cy.wait('@listAfterCreate');
    cy.url().should('include', '/departamentos');
  });
});
