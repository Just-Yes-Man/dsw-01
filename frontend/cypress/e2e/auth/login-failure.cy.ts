describe('Auth login failure', () => {
  it('shows generic invalid credentials message and remains on /login', () => {
    cy.intercept('POST', '**/api/v1/auth/login', {
      statusCode: 401,
      body: {
        code: 'UNAUTHORIZED',
        message: 'Credenciales inválidas'
      }
    }).as('loginFailure');

    cy.visit('/login');
    cy.get('#email').type('ana@example.com');
    cy.get('#password').type('wrong');
    cy.contains('button', 'Entrar').click();

    cy.wait('@loginFailure');
    cy.url().should('include', '/login');
    cy.get('[data-testid="login-error"]').should('contain.text', 'Credenciales inválidas');
  });
});
