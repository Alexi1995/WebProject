package com.webproject;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;


/**
 * Objektová reprezentace přihlašovací stránky (Page Object Model).
 * Poskytuje metody pro navigaci, vyplňování formulářů a interakci s prvky během testování aplikace po přihlášení.
 */
public class ApplicationTesting {
    private final Page page;

    /**
     * Konstruktor pro inicializaci stránky.
     * @param page Instance Playwright stránky, na které se budou provádět akce
     */
    public ApplicationTesting(Page page) {
        this.page = page;
    }

    /**
     * Počká, dokud ze stránky nezmizí zadaný textový prvek.
     * @param text Text, na jehož zmizení chceme počkat (např. "Spouštění")
     */
    public void waitForTextToDisappear(String text) {
        System.out.println("Čekám, až ze stránky zmizí text: " + text);
        
        // Použijeme textový lokátor a počkáme na stav HIDDEN
        page.locator("text=" + text).waitFor(
            new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
        );
        page.waitForTimeout(5000); // Počkáme 5 sekund, aby se stránka stabilizovala
    }

    /**
     * Prozkoumá kód stránky. Pokud najde prvek .react-grid-layout (domovskou stránku),
     * ale zároveň zjistí, že přes ni překáží nějaké okno, klikne na první tlačítko v levém menu.
     * 
     * @param menuToClick Přesný text prvního tlačítka v menu, na které se má kliknout
     */
    public void verifyHomePage(String menuToClick) {
        System.out.println("Kontroluji přítomnost domovské stránky (.react-grid-layout)...");
        
        // Ověříme, zda je na stránce přítomen hlavní layout
        boolean isHomePageVisible = page.locator(".react-grid-layout").isVisible();
        
        if (!isHomePageVisible) {       
            System.out.println("Layout nenalezen. Klikám v menu na: " + menuToClick);
            // Najde v menu tlačítko a klikne na něj
            page.locator(menuToClick).first().click();
        } else {
            System.out.println(".react-grid-layout je nalezen, stránka je v pořádku. Pokračuji.");
        }
    }

    /**
     * Klikne na prvek pravým tlačítkem myši (Right-click) podle CSS selektoru.
     * @param selector CSS selektor prvku (např. ".react-grid-layout")
     */
    public void rightClickOnSelector(String selector) {
        System.out.println("Klikám PRAVÝM tlačítkem na CSS selektor: " + selector);
        
        // Použijeme plnou cestu ke třídě MouseButton, aby Java nehlásila chybu
        page.locator(selector).first().click(
            new com.microsoft.playwright.Locator.ClickOptions()
                .setButton(com.microsoft.playwright.options.MouseButton.RIGHT)
        );
    }

    /**
     * Najede na položku text1 uvnitř specifického kontextového menu
     * identifikovaného textovým obsahem a klikne na menu text2.
     * @param text1 Text, na který se má najet myší (např. "Nový")
     * @param text2 Text, na který se má kliknout (např. "Dokument")
     */
    public void hoverInsideSpecificMenu(String text1, String text2) {
        System.out.println("Cíleně najíždíme myší na '" + text1 + "' uvnitř kontextového menu.");
        
        // Zaměříme kontejner, který v sobě obsahuje texty vašeho menu
        Locator specificMenu = page.locator("div:has-text('NovýSložkaDokumentTabulka')").last();
        
        // Vykreslení a stabilizace menu
        specificMenu.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        
        // Najedeme myší na text pouze uvnitř tohoto zaměřeného menu
        specificMenu.getByText(text1).first().hover();

        TakeScreenshot takeScreenshot = new TakeScreenshot(page);
        takeScreenshot.takeScreenshot("target/site/homepage_hovered_inside_menu.png");

        // Klikneme myší na text konkrétní menu pouze uvnitř tohoto zaměřeného menu
        System.out.println("Klikneme myší na '" + text2 + "' uvnitř kontextového menu.");
        specificMenu.getByText(text2).first().click();
    }

    /**
     * Vyhledá textové pole podle zadaného lokátoru a vyplní do něj požadovaný název.
     * 
     * @param CSSselector Smluvní lokátor prvku
     * @param text Text, který se má do pole vyplnit
     */
    public void fillNameAndPressButton(String CSSselector, String text) {
        System.out.println("Hledám pole s placeholderem '" + CSSselector + "' a simuluji psaní: " + text);
        
        // 1. Zaměříme pole a klikneme do něj, aby získalo fokus
        com.microsoft.playwright.Locator pole = page.getByPlaceholder(CSSselector);
        pole.click();
        
        // 2. Vymažeme případný předvyplněný text
        pole.clear();
        
        // 3. KLÍČOVÝ KROK: Vyťkáme text písmeno po písmenu, což odblokuje tlačítko "Vytvořit"
        pole.pressSequentially(text, new com.microsoft.playwright.Locator.PressSequentiallyOptions().setDelay(50));
        
        // 4. Uložení screenshotu pro vizuální kontrolu v reportu
        TakeScreenshot takeScreenshot = new TakeScreenshot(page);
        takeScreenshot.takeScreenshot("target/site/doc_name_filled.png");

        System.out.println("Klikám na tlačítko: Vytvořit dokument");
        // Klikáme na tlačítko s vypnutou striktní shodou
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Vytvořit dokument").setExact(false)).click(); 
    }

    /**
     * Zavře otevřený dokument kliknutím na jeho vlastní zavírací tlačítko (X).
     * Metoda postupně vyzkouší vyhledání podle role, textu i běžných CSS tříd pro ikonu křížku.
     */
    public void closing() {
        System.out.println("Zavírám dokument kliknutím na tlačítko X.");
        page.locator("[id=\"gui\\.doc\\#header_end\"] div").nth(3).click();
    }

    /**
     * Vyhledá dokument schovaný ve značce span, ověří jeho existenci,
     * klikne na něj pravým tlačítkem a vybere možnost Smazat.
     * 
     * @param docName Přesný název souboru (např. "test_document.docx" nebo "test_document.pptx")
     */
    public void deleteDocument(String docName) {
        System.out.println("KROK: Hledám span s textem: " + docName);
        
        // 1. Přesné zaměření prvku podle vašeho funkčního vzoru
        Locator dokumentSpan = page.locator("span")
            .filter(new com.microsoft.playwright.Locator.FilterOptions().setHasText(docName))
            .first();
            
        // Stabilizace: počkáme 5 sekund na překreslení React plochy
        page.waitForTimeout(5000);

        // 2. KONTROLNÍ BOD (Asertace): Ověříme, zda prvek na obrazovce opravdu existuje
        org.junit.jupiter.api.Assertions.assertTrue(dokumentSpan.isVisible(), 
            "Test selhal: Prvek span s názvem '" + docName + "' na stránce neexistuje!");
            
        System.out.println("POTVRZENO: Dokument nalezen. Vyvolávám menu pravým tlačítkem.");

        // 3. KLÍČOVÁ ZMĚNA: Klikneme na tento konkrétní span PRAVÝM tlačítkem myši
        dokumentSpan.click(new com.microsoft.playwright.Locator.ClickOptions()
            .setButton(com.microsoft.playwright.options.MouseButton.RIGHT));
            
        // Krátká pauza na zobrazení menu
        page.waitForTimeout(500);
        
        // 4. Klikneme na položku "Smazat" v kontextovém menu
        page.getByRole(com.microsoft.playwright.options.AriaRole.MENU)
            .getByText("Smazat", new com.microsoft.playwright.Locator.GetByTextOptions().setExact(false))
            .first()
            .click();


        // 5. Zaměříme se pouze na DIALOG a v něm klikneme na tlačítko Smazat
        page.getByRole(com.microsoft.playwright.options.AriaRole.DIALOG)
        .getByRole(com.microsoft.playwright.options.AriaRole.BUTTON, 
        new com.microsoft.playwright.Locator.GetByRoleOptions().setName("Smazat"))
        .click();

        org.junit.jupiter.api.Assertions.assertFalse(dokumentSpan.isVisible(), 
            "Test selhal: Prvek span s názvem '" + docName + "' na stránce stále existuje!");
            
        System.out.println("Dokument byl v dialogu úspěšně potvrzen a smazán.");
    }





}