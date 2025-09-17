import { test, expect } from '@playwright/test';

const ADMIN = {
  email: 'admin@paycanvas.io',
  password: 'password'
};

test('店舗マスタで店舗名を更新できる', async ({ page }) => {
  await page.goto('/login');
  await page.fill('input[type="text"]', ADMIN.email);
  await page.fill('input[type="password"]', ADMIN.password);
  await Promise.all([
    page.waitForURL('**/dashboard'),
    page.click('button:has-text("ログイン")')
  ]);

  await page.click('text=マスタ管理');
  await page.click('button:has-text("店舗")');

  const firstRow = page.locator('table tbody tr').first();
  await expect(firstRow).toBeVisible();

  const originalName = (await firstRow.locator('td').nth(1).innerText()).trim();
  const newName = `${originalName}-テスト更新`;

  await firstRow.locator('button:has-text("編集")').click();
  await page.fill('label:has-text("店舗名") input', newName);
  await Promise.all([
    page.waitForResponse((resp) => resp.url().includes('/api/masters/stores') && resp.request().method() === 'PATCH'),
    page.click('button:has-text("更新する")')
  ]);

  await expect(page.locator('table tbody tr td')).toContainText(newName);

  await page.locator('table tbody tr', { hasText: newName }).locator('button:has-text("編集")').click();
  await page.fill('label:has-text("店舗名") input', originalName);
  await Promise.all([
    page.waitForResponse((resp) => resp.url().includes('/api/masters/stores') && resp.request().method() === 'PATCH'),
    page.click('button:has-text("更新する")')
  ]);

  await expect(page.locator('table tbody tr td')).toContainText(originalName);
});
