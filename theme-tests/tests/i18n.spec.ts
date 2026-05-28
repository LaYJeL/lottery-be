import { test, expect } from '@playwright/test';

test.describe('Internationalization', () => {
    test('should switch language to Ukrainian', async ({ page }) => {
        // Go to Login page
        await page.goto('/protocol/openid-connect/auth?client_id=lottery-frontend&response_type=code');

        // Open language menu and select UK
        await page.locator('.dropbtn').click();
        await page.locator('text=Українська').click();

        // Verify Title
        await expect(page.locator('h1')).toContainText('Увійти');

        // Trigger Error
        await page.locator('#kc-login').click();

        // Verify Error in UA
        await expect(page.locator('body')).toContainText("Невірне ім'я користувача або пароль");
    });
});
