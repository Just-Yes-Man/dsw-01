/// <reference types="cypress" />

describe('Empleados editar', () => {
  it('edita un empleado existente', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/empleados/EMP-1*', {
      statusCode: 200,
      body: {
        clave: 'EMP-1',
        nombre: 'Ana',
        direccion: 'Calle 1',
        telefono: '555',
        email: 'ana@example.com',
        estadoAcceso: 'ACTIVO',
        departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
      }
    }).as('getEmpleado');

    cy.intercept('PUT', '**/api/v1/empleados/EMP-1*', {
      statusCode: 200,
      body: {
        clave: 'EMP-1',
        nombre: 'Ana Editada',
        direccion: 'Calle 1',
        telefono: '555',
        email: 'ana@example.com',
        estadoAcceso: 'ACTIVO',
        departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
      }
    }).as('update');

    cy.intercept('GET', '**/api/v1/empleados*', {
      statusCode: 200,
      body: {
        items: [
          {
            clave: 'EMP-1',
            nombre: 'Ana Editada',
            direccion: 'Calle 1',
            telefono: '555',
            email: 'ana@example.com',
            estadoAcceso: 'ACTIVO',
            departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
          }
        ],
        page: 0,
        size: 10,
        totalElements: 1
      }
    }).as('listAfterUpdate');

    cy.visit('/empleados/EMP-1/editar');
    cy.wait('@getEmpleado');
    cy.get('#nombre').clear().type('Ana Editada');
    cy.get('#password').clear().type('ana123');
    cy.contains('button', 'Guardar').click();
    cy.wait('@update');
    cy.wait('@listAfterUpdate');
  });
});
