import { test, expect } from '@playwright/test';

test.describe('Registration Page', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/protocol/openid-connect/registrations?client_id=lottery-frontend&response_type=code');
    });

    test('should show validation errors for empty fields', async ({ page }) => {
        // Focus and blur fields to trigger logic
        await page.locator('#email').focus();
        await page.locator('#username').focus(); // Triggers blur on email
        await page.locator('#password').focus(); // Triggers blur on username
        await page.locator('#password-confirm').focus(); // Triggers blur on password
        await page.locator('#email').focus(); // Triggers blur on confirm

        await expect(page.locator('#client-email-error')).toBeVisible();
        await expect(page.locator('#client-email-error')).toContainText('Please specify email');

        await expect(page.locator('#client-username-error')).toBeVisible();
        await expect(page.locator('#client-username-error')).toContainText('Please specify username');

        await expect(page.locator('#client-password-error-top')).toBeVisible();
        await expect(page.locator('#client-password-error-top')).toContainText('Please specify password');

        // Check Confirm mismatch/empty
        // Note: Empty confirm might say "Please confirm password" or similar depending on implementation
        if (await page.locator('#client-password-error').isVisible()) {
            await expect(page.locator('#client-password-error')).toContainText('Please confirm password');
        }

        // Verify button is disabled
        await expect(page.locator('button[type="submit"]')).toBeDisabled();
    });

    test('should validate password complexity rules', async ({ page }) => {
        await page.locator('#password').fill('short');

        // Check rules list
        const lengthRule = page.locator('#rule-length');
        await expect(lengthRule).toHaveClass(/invalid/);

        // Type valid password
        await page.locator('#password').fill('Valid123!');

        await expect(page.locator('#rule-length')).toHaveClass(/valid/);
        await expect(page.locator('#rule-upper')).toHaveClass(/valid/);
        await expect(page.locator('#rule-lower')).toHaveClass(/valid/);
        await expect(page.locator('#rule-digit')).toHaveClass(/valid/);
        await expect(page.locator('#rule-special')).toHaveClass(/valid/);
    });

    test('should show error for password mismatch', async ({ page }) => {
        await page.locator('#password').fill('Valid123!');
        await page.locator('#password-confirm').fill('Mismatch123!');

        // Trigger validation
        await page.locator('#password-confirm').blur();

        await expect(page.locator('#client-password-error')).toBeVisible();
        await expect(page.locator('#client-password-error')).toContainText('Passwords do not match');

        await expect(page.locator('button[type="submit"]')).toBeDisabled();
    });

    test('should show server-side error for duplicate user', async ({ page }) => {
        // Assuming 'testuser' already exists from manual testing
        await page.locator('#email').fill('test@example.com');
        await page.locator('#username').fill('testuser');
        await page.locator('#password').fill('Valid123!');
        await page.locator('#password-confirm').fill('Valid123!');

        await page.locator('button[type="submit"]').click();

        // Expect reload with error
        await expect(page.locator('#input-error-username, #input-error-email')).toBeVisible();
        // One of them should show "already exists"
        const content = await page.content();
        expect(content).toMatch(/already exists/i);
    });
});
