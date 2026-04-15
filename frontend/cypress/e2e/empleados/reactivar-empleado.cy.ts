/// <reference types="cypress" />

describe('Empleados reactivar', () => {
  it('reactiva empleado desde edición', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/empleados/EMP-2*', {
      statusCode: 200,
      body: {
        clave: 'EMP-2',
        nombre: 'Luis',
        direccion: 'Calle 2',
        telefono: '444',
        email: 'luis@example.com',
        estadoAcceso: 'INACTIVO',
        departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
      }
    }).as('getEmpleado');

    cy.intercept('PUT', '**/api/v1/empleados/EMP-2*', {
      statusCode: 200,
      body: {
        clave: 'EMP-2',
        nombre: 'Luis',
        direccion: 'Calle 2',
        telefono: '444',
        email: 'luis@example.com',
        estadoAcceso: 'ACTIVO',
        departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
      }
    }).as('update');

    cy.intercept('GET', '**/api/v1/empleados*', {
      statusCode: 200,
      body: {
        items: [
          {
            clave: 'EMP-2',
            nombre: 'Luis',
            direccion: 'Calle 2',
            telefono: '444',
            email: 'luis@example.com',
            estadoAcceso: 'ACTIVO',
            departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
          }
        ],
        page: 0,
        size: 10,
        totalElements: 1
      }
    }).as('listAfterUpdate');

    cy.visit('/empleados/EMP-2/editar');
    cy.wait('@getEmpleado');
    cy.get('#estadoAcceso').select('ACTIVO');
    cy.get('#password').clear().type('luis123');
    cy.contains('button', 'Guardar').click();
    cy.wait('@update');
    cy.wait('@listAfterUpdate');
  });
});
