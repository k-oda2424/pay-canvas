import { test, expect } from '@playwright/test';

const ADMIN_CREDENTIALS = {
  email: 'admin@paycanvas.io',
  password: 'password'
};

async function login(page) {
  await page.goto('/login');
  await page.fill('input[type="text"]', ADMIN_CREDENTIALS.email);
  await page.fill('input[type="password"]', ADMIN_CREDENTIALS.password);
  await Promise.all([
    page.waitForURL('**/dashboard'),
    page.click('button:has-text("ログイン")')
  ]);
}

test.describe('payCanvas UI smoke', () => {
  test('company admin sees expected menu', async ({ page }) => {
    await login(page);
    const navItems = page.locator('.nav-item');
    await expect(navItems).toHaveCount(5);
    await expect(navItems.nth(0)).toHaveText('ダッシュボード');
    await expect(navItems.nth(1)).toHaveText('日次実績');
    await expect(navItems.nth(2)).toHaveText('給与計算');
    await expect(navItems.nth(3)).toHaveText('給与明細');
    await expect(navItems.nth(4)).toHaveText('マスタ管理');
  });
});
