# Selewright

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.java.com)
[![Selenium](https://img.shields.io/badge/Selenium-43B02A?style=for-the-badge&logo=selenium&logoColor=white)](https://selenium.dev)
[![Playwright](https://img.shields.io/badge/Playwright-2EAD33?style=for-the-badge&logo=playwright&logoColor=white)](https://playwright.dev)

**A Unified Browser Automation Tool for Test Automation**

Selewright brings together the best of Selenium and Playwright, allowing you to write browser automation code once and run it on either framework without any code changes.

## 🚀 Why Choose Selewright?

- **✅ Write Once, Run Anywhere**: Code once and execute with either Selenium or Playwright
- **🔄 Future-Proof**: Easily adopt new browser automation frameworks under the Selewright umbrella
- **🎯 Focus on Business Logic**: Selewright handles the complexities while you focus on your test scenarios
- **🛠️ Built-in Conveniences**: Automatic waits, POJO setup to mock APIs triggered by browser frontend, boilerplate code handling
- **🔧 No Tool Lock-in**: Switch between automation tools without rewriting your tests

## 📋 Table of Contents

- [Project Structure](#-project-structure)
- [Quick Start](#-quick-start)
- [Installation](#-installation)
- [Usage](#-usage)
- [Core Methods](#-core-methods)
- [Migration Guide](#-migration-guide)
- [Contributing](#-contributing)
- [Acknowledgements](#-acknowledgements)
- [Roadmap](#-roadmap)
- [Issues and Support](#-issues-and-support)


## 🏗️ Project Structure

```
selewright/
├── src/
│   ├── main/java/com/redbus/selewright/
│   │   ├── Selewright.java                    # Core interface for browser automation tools
│   │   ├── PlaywrightImplementation.java      # Implements Selewright methods using Playwright
│   │   ├── SeleniumImplementation.java        # Implements Selewright methods using Selenium
│   │   ├── RequestConditionsToMock.java       # POJO to define request conditions for APIs invoked by browser frontend
│   │   ├── MockResponseToSend.java            # POJO to define mock responses for APIs invoked by browser frontend
│   │   └── OtherHelpers.java                  # Utility functions for common test automation tasks
│   └── test/java/
│       └── demo.java                          # Demonstrates sample setup and usage of Selewright
```


> **Note**:
We recommend Playwright for tests which involve fetching or mocking APIs invoked by the browser frontend.
The corresponding methods in `SeleniumImplementation` are left unimplemented as Selenium currently supports network interception only in Chrome via the DevTools Protocol.


## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+
- Git

### Try It Out

1. **Clone the repository**
   ```bash
   git clone https://github.com/Krishna-D-Hegde/selewright.git
   cd selewright
   ```

2. **Run the demo**
   ```bash
   cd src/test/java
   javac demo.java
   java demo
   ```

## 📦 Installation

### Maven Setup

1. **Download the JAR** (Coming soon on Maven Central Repository)

   Download [`selewright-1.0-SNAPSHOT.jar`](https://github.com/Krishna-D-Hegde/selewright/blob/main/selewright-1.0-SNAPSHOT.jar) and place it in your project root.

2. **Add to your `pom.xml`**
   ```xml
   <dependency>
       <groupId>com.redbus</groupId>
       <artifactId>selewright</artifactId>
       <version>1.0-SNAPSHOT</version>
       <scope>system</scope>
       <systemPath>${basedir}/selewright-1.0-SNAPSHOT.jar</systemPath>
   </dependency>
   
   <!-- Selenium dependency -->
   <dependency>
       <groupId>org.seleniumhq.selenium</groupId>
       <artifactId>selenium-java</artifactId>
       <version>4.35.0</version>
   </dependency>
   
   <!-- Playwright dependency -->
   <dependency>
       <groupId>com.microsoft.playwright</groupId>
       <artifactId>playwright</artifactId>
       <version>1.55.0</version>
   </dependency>
   
   <!-- Appium (for mobile web testing) -->
   <dependency>
       <groupId>io.appium</groupId>
       <artifactId>java-client</artifactId>
       <version>10.0.0</version>
   </dependency>
   ```

> **Note:** You are free to use any version of Selenium or Playwright. However, make sure the Appium version you choose is compatible with your Selenium version.  
> For more details, refer [here](https://claude.ai/share/a64308e2-8268-4a6c-9f17-b3147114560e).
 

## 💻 Usage

### Basic Setup with a Sample Test

```java
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
            selewright = setupSelewright("playwright");
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
        selewright.click("//a[@href='/redbus-labs/selewright']");
        return selewright.isDisplayed("(//a[@href='/redbus-labs/selewright/blob/main/README.md'])[last()]");
    }
}
```

## 📚 Core Methods

Refer [Selewright-Interface](https://github.com/Krishna-D-Hegde/selewright/blob/main/src/main/java/com/redbus/selewright/Selewright.java) for complete method list with Javadocs.


## 🔄 Migration Guide

### From Selenium

If you have an existing Selenium helper class:

**Before:**
```java
public void click(String locator) {
    driver.findElement(By.xpath(locator)).click();
}
```

**After:**
```java
public void click(String locator) {
    selewright.click(locator);
}
```

### From Playwright

Similar migration process - replace your Playwright-specific code with Selewright method calls.

## 🤝 Contributing

We welcome contributions from the community! This project can only evolve with open source contributions.

### How to Contribute

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please read our [Contributing Guidelines](https://github.com/Krishna-D-Hegde/selewright/blob/main/CONTRIBUTING.md) before submitting contributions.

### Development Setup

1. Clone the repository
2. Import into your IDE
3. Run tests to ensure everything works
4. Start coding!


## 🙌 Acknowledgements

### Author:
- [Krishna Hegde](https://www.linkedin.com/in/krishna-d-hegde/), Senior SDET, redBus

### Guided by:
- [Chandrashekar Patil](https://www.linkedin.com/in/patilchandrashekhar/), Senior Director - QA, redBus
- [Eesha Karanwal](https://www.linkedin.com/in/eesha-karanwal-1263461ab/), SDET Engineering Manager, redBus

### Contributors [Browser Test Automation Team, redBus]:
- [Smruti Sourav Sahoo](https://www.linkedin.com/in/smruti-sourav-2000/), SDET
- [Shon Noronha](https://www.linkedin.com/in/shon-noronha-07278820b/), SDET
- [Anuj Gaur](https://www.linkedin.com/in/anujgaur06/), SDET
- [Ayan Ray](https://www.linkedin.com/in/ayan-ray-69ba77141/), Senior SDET
- [Varun Singhai](https://www.linkedin.com/in/varun-singhai-64791814a/), Senior SDET
- [Shravya Acharya](https://www.linkedin.com/in/shravya-a-acharya-98b228183/), SDET
- [Pooja Benni](https://www.linkedin.com/in/pooja-benni-b1886571/), Senior QA
- [Sowmya Acharya](https://in.linkedin.com/in/sowmya-acharya-105a4025b), SDET


## 🎯 Roadmap

- [ ] Support for additional programming languages (Python, JavaScript, C#)
- [ ] Selewright MCP
- [ ] Support for additional browser automation frameworks


## 🐛 Issues and Support

Found a bug or need help? Please [open an issue](https://github.com/Krishna-D-Hegde/selewright/issues) on GitHub.

---
