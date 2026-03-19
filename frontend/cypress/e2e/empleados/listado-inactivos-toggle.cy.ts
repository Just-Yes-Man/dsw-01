/// <reference types="cypress" />

describe('Empleados toggle inactivos', () => {
  it('permite mostrar inactivos', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/empleados?page=0', {
      statusCode: 200,
      body: {
        page: 0,
        size: 10,
        totalElements: 2,
        items: [
          {
            clave: 'EMP-1',
            nombre: 'Ana',
            direccion: 'Calle 1',
            telefono: '555',
            email: 'ana@example.com',
            estadoAcceso: 'ACTIVO',
            departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
          },
          {
            clave: 'EMP-2',
            nombre: 'Luis',
            direccion: 'Calle 2',
            telefono: '444',
            email: 'luis@example.com',
            estadoAcceso: 'INACTIVO',
            departamento: { id: 1, nombre: 'Sistemas', estado: 'ACTIVO' }
          }
        ]
      }
    });

    cy.visit('/empleados');
    cy.contains('td', 'EMP-2').should('not.exist');
    cy.contains('Mostrar inactivos').click();
    cy.contains('td', 'EMP-2').should('exist');
  });
});
