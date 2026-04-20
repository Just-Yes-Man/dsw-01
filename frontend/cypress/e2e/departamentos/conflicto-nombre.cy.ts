/// <reference types="cypress" />

describe('Departamentos conflicto de nombre', () => {
  it('muestra conflicto al crear con nombre duplicado', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 200,
      body: { authenticated: true, empleadoClave: 'EMP-1', email: 'ana@example.com' }
    });

    cy.intercept('POST', '**/api/v1/departamentos', {
      statusCode: 409,
      body: {
        code: 'CONFLICT',
        message: "Departamento con nombre 'Sistemas' ya existe"
      }
    }).as('conflict');

    cy.visit('/departamentos/nuevo');
    cy.get('#nombre').type('Sistemas');
    cy.contains('button', 'Guardar').click();
    cy.wait('@conflict').its('response.statusCode').should('eq', 409);
  });
});
