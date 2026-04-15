/// <reference types="cypress" />

describe('Departamentos editar', () => {
  it('edita un departamento existente', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/departamentos/1', {
      statusCode: 200,
      body: {
        id: 1,
        nombre: 'Sistemas',
        estado: 'ACTIVO',
        creadoEn: '2026-03-19T00:00:00',
        actualizadoEn: '2026-03-19T00:00:00'
      }
    }).as('getDepartamento');

    cy.intercept('PATCH', '**/api/v1/departamentos/1', {
      statusCode: 200,
      body: {
        id: 1,
        nombre: 'Sistemas y Redes',
        estado: 'ACTIVO',
        creadoEn: '2026-03-19T00:00:00',
        actualizadoEn: '2026-03-19T00:00:00'
      }
    }).as('update');

    cy.intercept('GET', '**/api/v1/departamentos?page=0', {
      statusCode: 200,
      body: {
        page: 0,
        size: 10,
        totalElements: 1,
        items: [
          {
            id: 1,
            nombre: 'Sistemas y Redes',
            estado: 'ACTIVO',
            creadoEn: '2026-03-19T00:00:00',
            actualizadoEn: '2026-03-19T00:00:00'
          }
        ]
      }
    }).as('listAfterUpdate');

    cy.visit('/departamentos/1/editar');
    cy.wait('@getDepartamento');
    cy.get('#nombre').clear().type('Sistemas y Redes');
    cy.contains('button', 'Guardar').click();
    cy.wait('@update');
    cy.wait('@listAfterUpdate');
  });
});
