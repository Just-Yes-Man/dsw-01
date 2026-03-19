describe('Auth login success', () => {
  it('logs in and redirects to /empleados', () => {
    cy.intercept('POST', '**/api/v1/auth/login', {
      statusCode: 200,
      body: {
        authenticated: true,
        empleadoClave: 'EMP-1',
        email: 'ana@example.com',
        expiresAt: '2026-03-18T10:00:00Z'
      }
    }).as('login');

    cy.intercept('GET', '**/api/v1/empleados*', {
      statusCode: 200,
      body: {
        items: [],
        page: 0,
        size: 10,
        totalElements: 0
      }
    }).as('empleados');

    cy.visit('/login');
    cy.get('#email').type('ana@example.com');
    cy.get('#password').type('ana123');
    cy.contains('button', 'Entrar').click();

    cy.wait('@login');
    cy.wait('@empleados');
    cy.url().should('include', '/empleados');
  });
});
