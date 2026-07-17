package com.webproject;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

/**
 * Objektová reprezentace přihlašovací stránky (Page Object Model).
 * Poskytuje metody pro navigaci, vyplňování formulářů a interakci s prvky během přihlašování.
 * @author Alexandr
 * @version 1.0
 */
public class LoginPage {
    private final Page page;
    
    // Lokátory a konstanty schované uvnitř třídy
    private final String userNameLabel = "Uživatelské jméno";

    /**
     * Konstruktor pro inicializaci stránky.
     * @param page Instance Playwright stránky, na které se budou provádět akce
     */
    public LoginPage(Page page) {
        this.page = page;
    }

    /**
     * Otevře zadanou webovou adresu a počká na vykreslení formuláře.
     * @param hostname Kompletní URL adresa (např. https://onice.io)
     */
    public void navigateTo(String hostname) {
        System.out.println("Otevírám Chrome a přicházím na: " + hostname);
        page.navigate(hostname);

        TakeScreenshot takeScreenshot = new TakeScreenshot(page);
        takeScreenshot.takeScreenshot("target/site/login_page1.png");
        
        // STABILIZACE PRO CLOUD: Dáme robotovi v Linuxu až 100 sekund, 
        // než se pole na pomalejším serveru poprvé objeví a aktivuje.
        page.getByLabel(userNameLabel, new Page.GetByLabelOptions().setExact(false))
            .waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
                .setTimeout(100000) // 100 sekund pro první start v pipeline
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
    }

    /**
     * Vyhledá textové pole podle jeho štítku (labelu) a vyplní do něj zadanou hodnotu.
     * @param info Text, který se má do pole zapsat (např. e-mail nebo heslo)
     * @param name Název štítku textového pole
     */
    public void fillInfo(String info, String name) {
        System.out.println("Vyplňuji " + name + ": " + info);
        // Hledáme s vypnutou striktní shodou (setExact false) pro větší stabilitu
        page.getByLabel(name, new Page.GetByLabelOptions().setExact(false)).fill(info);
    }

    /**
     * Najde na stránce tlačítko s odpovídajícím textem a klikne na něj.
     * @param ButtonToClick Text zobrazený na tlačítku
     */
    public void clickButton(String ButtonToClick) {
        System.out.println("Klikám na tlačítko: " + ButtonToClick);
        // Klikáme na tlačítko s vypnutou striktní shodou
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ButtonToClick).setExact(false)).click(); 
    }
}
