/// <reference types="cypress" />

describe('Departamentos eliminación rechazada', () => {
  it('muestra mensaje cuando hay empleados asociados', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('GET', '**/api/v1/departamentos?page=0', {
      statusCode: 200,
      body: {
        page: 0,
        size: 10,
        totalElements: 1,
        items: [
          { id: 1, nombre: 'Sistemas', estado: 'ACTIVO', creadoEn: '2026-03-19T00:00:00', actualizadoEn: '2026-03-19T00:00:00' }
        ]
      }
    });

    cy.intercept('DELETE', '**/api/v1/departamentos/1', {
      statusCode: 409,
      body: {
        code: 'CONFLICT',
        message: 'No se puede eliminar el departamento porque tiene empleados asociados'
      }
    }).as('deleteConflict');

    cy.visit('/departamentos');
    cy.contains('button', 'Eliminar').click();
    cy.contains('button', 'Confirmar').click();
    cy.wait('@deleteConflict');
    cy.get('p.error').should('exist');
    cy.contains(/eliminar|empleados|asociados|conflicto|solicitud/i).should('exist');
  });
});
