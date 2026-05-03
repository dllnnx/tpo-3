package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.LoginModalPage;

@SuppressWarnings("IllegalAllureIdUast")
public class HomePageTest extends BaseTest {

    /**
     * UC-10: Открытие модалки авторизации и ввод email.
     * Шаги: открыть главную → нажать «Войти» в шапке → дождаться модалки →
     * ввести email → нажать «Продолжить/Получить код» → дождаться шага ввода кода.
     */
    @Test(description = "UC-10: Открытие модалки авторизации и ввод email")
    public void uc10_openLoginModalAndSubmitEmail() {
        HomePage home = new HomePage(driver, wait).open();
        Assert.assertTrue(home.isHeaderNavigationVisible(),
                "Шапка с навигацией должна быть видна");

        LoginModalPage modal = home.openLoginModal().waitForVisible();
        Assert.assertTrue(modal.isEmailFieldVisible(),
                "Поле ввода email должно быть видно после клика «Войти»");

        modal.fillEmail("test_user_uc10@example.com");
        Assert.assertEquals(modal.getEmailValue(), "test_user_uc10@example.com",
                "Введённый email должен сохраниться в поле");

        modal.clickContinue();
        Assert.assertTrue(modal.codeStepReached(),
                "После отправки email должен появиться шаг ввода кода или сообщение");
    }
}
