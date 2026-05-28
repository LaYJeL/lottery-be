import { test, expect } from '@playwright/test';

test.describe('Login Page', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/protocol/openid-connect/auth?client_id=lottery-frontend&response_type=code');
    });

    test('should show error for invalid credentials', async ({ page }) => {
        await page.locator('#username').fill('nonexistentuser');
        await page.locator('#password').fill('wrongpassword');
        await page.locator('#kc-login').click();

        await expect(page.locator('#input-error-username')).toBeVisible();
        // or checks generic error area depending on theme
        const errorText = await page.textContent('body');
        expect(errorText).toContain('Invalid username or password');
    });

    test('should navigate to forgot password', async ({ page }) => {
        await page.locator('text=Forgot Password?').click();
        await expect(page).toHaveURL(/login-actions\/reset-credentials/);
        await expect(page.locator('h1')).toContainText('Forgot Password?');
    });
});
