/// <reference types="cypress" />

describe('Empleados desactivar', () => {
  it('desactiva con confirmación', () => {
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
    }).as('list');

    cy.intercept('PUT', '**/api/v1/empleados/EMP-1', {
      statusCode: 200,
      body: {
        clave: 'EMP-1',
        nombre: 'Ana',
        direccion: 'Calle 1',
        telefono: '555',
        email: 'ana@example.com',
        estadoAcceso: 'INACTIVO',
        departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
      }
    }).as('deactivate');

    cy.visit('/empleados');
    cy.wait('@list');
    cy.contains('button', 'Desactivar').click();
    cy.contains('button', 'Confirmar').click();
    cy.wait('@deactivate');
  });
});
