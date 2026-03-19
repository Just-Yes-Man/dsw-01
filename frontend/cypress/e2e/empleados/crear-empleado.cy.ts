/// <reference types="cypress" />

describe('Empleados crear', () => {
  it('crea empleado y regresa al listado', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('POST', '**/api/v1/empleados*', {
      statusCode: 201,
      body: {
        clave: 'EMP-10',
        nombre: 'Nuevo',
        direccion: 'Calle 3',
        telefono: '333',
        email: 'nuevo@example.com',
        estadoAcceso: 'ACTIVO',
        departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
      }
    }).as('create');

    cy.intercept('GET', '**/api/v1/empleados*', {
      statusCode: 200,
      body: {
        items: [
          {
            clave: 'EMP-10',
            nombre: 'Nuevo',
            direccion: 'Calle 3',
            telefono: '333',
            email: 'nuevo@example.com',
            estadoAcceso: 'ACTIVO',
            departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
          }
        ],
        page: 0,
        size: 10,
        totalElements: 1
      }
    }).as('listAfterCreate');

    cy.visit('/empleados/nuevo');
    cy.get('#nombre').type('Nuevo');
    cy.get('#direccion').type('Calle 3');
    cy.get('#telefono').type('333');
    cy.get('#email').type('nuevo@example.com');
    cy.get('#password').type('pass123');
    cy.get('#departamentoId').clear().type('1');
    cy.contains('button', 'Guardar').click();
    cy.wait('@create');
    cy.wait('@listAfterCreate');
    cy.url().should('include', '/empleados');
  });
});
