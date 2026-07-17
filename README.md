# 🎭 Playwright Java Automation Framework (Onice Platform)

Automatizovaný testovací framework postavený na **Java 17**, **Playwright** a **JUnit 5** pro validaci přihlašovacího workflow a správy dokumentů na platformě Onice (`https://onice.io`).

Projekt plně využívá návrhový vzor **Page Object Model (POM)** pro maximální udržitelnost a čitelnost kódu.

---

## 🛠️ Architektura projektu

* `src/main/java/com/webproject/LoginPage.java` – Page Object třída obsahující robustní a stabilizované metody pro interakci s prvky pro přihlášení (vstup na webovou stránku, zadávání přihlašovacího jména a hesla, klikání na tlačítka)
* `src/main/java/com/webproject/TakeScreenshot.java` - Page Object třída obsahující metody pro tvorbu screenshotů, která se volá z více míst
* `src/main/java/com/webproject/ApplicationTesting.java` - Page Object třída obsahující metody pro práci s prostředím webového rozhraní (čekání na přihlášení, kontrola domovské stránky, tvorbu dokumentu ajeho mazání)
* `src/test/java/com/webproject/AppTest.java` – Samotný testovací scénář validující kompletní workflow od přihlášení, přes vytvoření nového dokumentu, až po jeho úspěšné smazání.
* `.github/workflows/playwright-tests.yml` – Automatická CI/CD pipeline, která spouští testy v cloudu na virtuálním monitoru (`Xvfb`) a vynucuje české prostředí.

---

## 🚀 Lokální spuštění testů

Pro spuštění testu u vás na Macu s otevřeným grafickým oknem prohlížeče zadejte do terminálu:

```bash
mvn clean test -Dtest=AppTest -Dheadless=false surefire-report:report
mvn - nastartuje Maven
clean - smaže složku target, aby v následující exekuci nezůstala data z předchozího běhu testu
test - spouštěč testu
-Dtest=AppTest - spustí se jen AppTest.java, -D - dynamický parametr
-Dheadless=false - prohlížeč se nespustí na pozadí, ale budeme ho vidět na obrazovce
surefire-report:report = vezme XML soubory a vytvoří surface-report.html ve složce target/site/
```

---

## 📊 Automatické reporty a screenshoty

Projekt je nakonfigurován tak, že **každý běh testu** automaticky vygeneruje detailní výstupy do složky `target/site/`:

1. **`surefire-report.html`** – Kompletní grafický HTML report s výsledky a časy testu.
2. **Screenshoty (`.png`)** – Vizuální ověření klíčových kroků testu zachycené přímo v průběhu přihlašování a mazání.

### Jak otevřít report na Macu:
```bash
open target/site/surefire-report.html
```
---

## 🤖 CI/CD Pipeline (GitHub Actions)

Testy běží automaticky při každém pushnutí kódu do větve `main`. Pipeline provádí:
* Instalaci čistého Linux prostředí s **JDK 17**.
* Stažení stabilních binárek prohlížeče **Chromium**.
* Vynucení češtiny (`cs_CZ.UTF-8`) pro správné chování lokalizovaných prvků na webu.
* Uložení výsledků testu do sekce **Artifacts** na GitHubu pod názvem `test-results`.
