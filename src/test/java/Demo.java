import com.microsoft.playwright.*;
import com.redbus.selewright.PlaywrightImplementation;
import com.redbus.selewright.SeleniumImplementation;
import com.redbus.selewright.Selewright;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * A demo class to showcase the usage of Selewright with both Selenium and Playwright
 */
public class Demo {
    public static void main(String[] args) {
        Selewright selewright = null;

        try {
            selewright = setupSelewright("selenium");
            boolean result = runSampleTest(selewright);
            System.out.println("Test Verification: " + result);
        } finally {
            if (selewright != null) {
                selewright.closeBrowser();
            }
        }
    }

    /**
     * Setup Selewright with either Selenium or Playwright based on the input parameter
     * @param automationTool - "selenium" or "playwright"
     * @return Selewright instance
     */
    private static Selewright setupSelewright(String automationTool) {
        if ("selenium".equalsIgnoreCase(automationTool.trim())) {
            WebDriver driver = new ChromeDriver();
            return new SeleniumImplementation(driver);
        } else if ("playwright".equalsIgnoreCase(automationTool.trim())) {
            Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium()
                    .launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            return new PlaywrightImplementation(page);
        }
        throw new IllegalArgumentException("Invalid tool: " + automationTool);
    }

    /**
     * A sample test to demonstrate the usage of Selewright
     * Selects 'selewright' repository on redbus-labs GitHub and verifies readme file presence
     * @param selewright
     * @return
     */
    private static boolean runSampleTest(Selewright selewright) {
        selewright.openUrl("https://github.com/orgs/redbus-labs/repositories");
        long startTime = System.nanoTime();
        selewright.click("//a[@href='/redbus-labs/selewright']");
        long endTime = System.nanoTime();
        System.out.println("Time taken to click and load repository: " + (endTime - startTime) / 1_000_000 + " ms");
        return selewright.isDisplayed("(//a[@href='/redbus-labs/selewright/blob/main/README.md'])[last()]");
    }
}
