package com.webproject;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

/**
 * Hlavní třída pro automatizované testování webové aplikace Onice.
 * Obsahuje workflow pro přihlášení uživatele pomocí Playwright.
 */
public class AppTest {
    /**
     * Výchozí konstruktor pro testovací třídu AppTest.
     */
    public AppTest() {
        // Inicializace testu
    }
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
    
        browser = playwright.chromium().launch(
        new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setChannel("chrome")
            // TYTO ARGUMENTY ZRYCHLÍ VYBARVOVÁNÍ STRÁNEK NA MACU:
            .setArgs(java.util.Arrays.asList(
                "--disable-gpu", 
                "--disable-dev-shm-usage",
                "--no-sandbox"
            ))
        );
        context = browser.newContext();
        page = context.newPage();
        page.setDefaultNavigationTimeout(90000); // 90 sekund na načtení úvodní stránky webu
        page.setDefaultTimeout(60000);           // 60 sekund na objevení jakéhokoliv tlačítka/pole
    }


    /**
     * Komplexní testovací scénář pro ověření funkčnosti přihlašovacího formuláře.
     * Krok za krokem prochází zadáním e-mailu, hesla a kontroluje stavy pomocí screenshotů.
     */
    @Test
    @DisplayName("Ověření úspěšného přihlášení uživatele")
    void testLoginWorkflow() {
        String webName = "https://iwqa01.onice.io";
        String userName = "alexandr@iwqa01.onice.io";
        String password = "tl0kvvgM";
        String continueButtonLabel = "Pokračovat";
        String loginButtonLabel = "Přihlásit";
        String userNameLabel = "Uživatelské jméno";
        String passwordLabel = "Heslo";
        String loading = "Spouštění";
        String starting = "Spouštění";
        String homePageMenuToClick = "[id=\"gui\\.frm_main\\.filter\\#D\"]";
        String rightClickSelector = ".react-grid-layout";
        String rightClickMenuToHover = "Nový";
        String rightClickMenuToClick = "Dokument"; // "Prezentace" nebo "Tabulka" lze také použít, pokud je potřeba
        String fillLocatorOfName = "Zadejte jméno a vytvořte";
        String fileNameToFill = "test_document";

        LoginPage loginPage = new LoginPage(page);
        ApplicationTesting applicationTesting = new ApplicationTesting(page);
        TakeScreenshot takeScreenshot = new TakeScreenshot(page);
        
        /** Krok 1: Otevření přihlašovací stránky a uložení výchozího stavu */
        loginPage.navigateTo(webName);
        takeScreenshot.takeScreenshot("target/site/chrome_login.png");

        /** Krok 2: Vyplnění uživatelského jména (e-mailu) */
        loginPage.fillInfo(userName, userNameLabel);
        takeScreenshot.takeScreenshot("target/site/chrome_before_login.png");

        /** Krok 3: Kliknutí na tlačítko Pokračovat */
        loginPage.clickButton(continueButtonLabel);
        takeScreenshot.takeScreenshot("target/site/chrome_after_login.png");

        /** Krok 4: Vyplnění přístupového hesla */
        loginPage.fillInfo(password, passwordLabel);
        takeScreenshot.takeScreenshot("target/site/chrome_password_filled.png");

        /** Krok 5: Potvrzení formuláře tlačítkem Přihlásit */
        loginPage.clickButton(loginButtonLabel);
        takeScreenshot.takeScreenshot("target/site/chrome_after_password.png");

        /** Krok 6: Čekání na dokončení načítání */
        applicationTesting.waitForTextToDisappear(loading);
        takeScreenshot.takeScreenshot("target/site/chrome_loading_finished.png");

        /** Krok 7: Ověření domovské stránky */
        applicationTesting.verifyHomePage(homePageMenuToClick);
        takeScreenshot.takeScreenshot("target/site/chrome_homepage.png");

        /** Krok 8: Kliknutí pravým tlačítkem na specifický prvek */
        applicationTesting.rightClickOnSelector(rightClickSelector);
        takeScreenshot.takeScreenshot("target/site/homepage_right_clicked.png");

        /** Krok 9: Kliknutí na prvek v menu podle textu */
        applicationTesting.hoverInsideSpecificMenu(rightClickMenuToHover, rightClickMenuToClick);
        takeScreenshot.takeScreenshot("target/site/homepage_menu_clicked.png");

        /** Krok 10: Kliknutí na prvek v menu podle textu a kliknutí na tlačítko pro vytvoření dokumentu/tabulky/prezentace */
        applicationTesting.fillNameAndPressButton(fillLocatorOfName, fileNameToFill);
        takeScreenshot.takeScreenshot("target/site/file_doc_created.png");

        /** Krok 11: Počkáme až zmizí Načítání */
        applicationTesting.waitForTextToDisappear(starting);
        takeScreenshot.takeScreenshot("target/site/file_doc_ready.png");

        /** Krok 12: Zavření dokumentu */
        applicationTesting.closing();
        takeScreenshot.takeScreenshot("target/site/file_doc_closed.png");

        /** Krok 13: Kontrola a smazání dokumentu */
        applicationTesting.deleteDocument(fileNameToFill);
        takeScreenshot.takeScreenshot("target/site/file_doc_deleted.png");

        // Poznámka: page.pause() je pro automatický report vynechán, 
        // aby se test v CI/CD terminálu nezasekl. Pokud ho chcete, odkomentujte:
        //page.pause();
    }

    @AfterEach
    void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}
