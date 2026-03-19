/// <reference types="cypress" />

describe('Empleados cancelar desactivación', () => {
  it('cancela desactivación sin enviar actualización', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

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
    });

    cy.intercept('PUT', '**/api/v1/empleados/EMP-1').as('deactivate');

    cy.visit('/empleados');
    cy.contains('button', 'Desactivar').click();
    cy.contains('button', 'Cancelar').click();
    cy.get('@deactivate.all').should('have.length', 0);
  });
});
