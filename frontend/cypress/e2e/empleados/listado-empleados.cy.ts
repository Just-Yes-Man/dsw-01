/// <reference types="cypress" />

describe('Empleados listado', () => {
  it('muestra empleados paginados', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    }).as('session');

    cy.intercept('GET', '**/api/v1/empleados?page=0', {
      statusCode: 200,
      body: {
        page: 0,
        size: 10,
        totalElements: 1,
        items: [
          {
            clave: 'EMP-1',
            nombre: 'Ana',
            direccion: 'Calle 1',
            telefono: '555',
            email: 'ana@example.com',
            estadoAcceso: 'ACTIVO',
            departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
          }
        ]
      }
    }).as('list');

    cy.visit('/empleados');
    cy.wait('@session');
    cy.wait('@list');
    cy.contains('h1', 'Empleados').should('exist');
    cy.contains('td', 'EMP-1').should('exist');
  });
});
