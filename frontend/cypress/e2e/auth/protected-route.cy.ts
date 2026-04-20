describe('Protected routes', () => {
  it('redirects unauthenticated users to /login', () => {
    cy.intercept('GET', '**/api/v1/auth/session', {
      statusCode: 401,
      body: { code: 'UNAUTHORIZED', message: 'Sesión no válida' }
    }).as('session');

    cy.visit('/empleados');
    cy.wait('@session');
    cy.url().should('include', '/login');
  });
});
