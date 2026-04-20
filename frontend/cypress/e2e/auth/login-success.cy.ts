describe('Auth login success', () => {
  it('logs in and redirects to /index', () => {
    cy.intercept('POST', '**/api/v1/auth/login', {
      statusCode: 200,
      body: {
        authenticated: true,
        empleadoClave: 'EMP-1',
        email: 'ana@example.com',
        expiresAt: '2026-03-18T10:00:00Z'
      }
    }).as('login');

    cy.visit('/login');
    cy.get('#email').type('ana@example.com');
    cy.get('#password').type('ana123');
    cy.contains('button', 'Entrar').click();

    cy.wait('@login');
    cy.url().should('include', '/index');
    cy.contains('h1', 'Panel principal').should('exist');
    cy.contains('a', 'Ir a empleados').should('exist');
    cy.contains('a', 'Ir a departamentos').should('exist');
  });
});
