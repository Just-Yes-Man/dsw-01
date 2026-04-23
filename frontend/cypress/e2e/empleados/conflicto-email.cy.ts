/// <reference types="cypress" />

describe('Empleados conflicto email', () => {
  it('muestra mensaje específico al recibir conflicto', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('POST', '**/api/v1/empleados*', {
      statusCode: 400,
      body: { code: 'VALIDATION_ERROR', message: 'email ya está registrado' }
    }).as('createConflict');

    cy.visit('/empleados/nuevo');
    cy.get('#nombre').type('Nuevo');
    cy.get('#direccion').type('Calle 3');
    cy.get('#telefono').type('333');
    cy.get('#email').type('ana@example.com');
    cy.get('#password').type('pass123');
    cy.get('#departamentoId').clear().type('1');
    cy.contains('button', 'Guardar').click();
    cy.wait('@createConflict').then((interception) => {
      expect(interception.response?.statusCode).to.eq(400);
      expect(interception.response?.body?.message).to.contain('registrado');
    });
    cy.url().should('include', '/empleados/nuevo');
  });
});
