package es.codeurjc.ais.tictactoe;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

@RunWith(Parameterized.class)
public class TicTacToeSystemTest {
    
    @Parameters(name = "{index}: Given 2 users playing TicTacToe game ({0} vs {1}), When {2} wins, Then alert message indicates that {2} wins")
    public static Collection<Object[]> data() {
        Object[][] values = {
        { "user1", "user2", "user1",
           new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.TOP_RIGHT},
           new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER}
        },
        { "user1", "user2", "user2", 
           new CellId[]{CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.BOTTOM_RIGHT}, 
           new CellId[]{CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER, CellId.MIDDLE_RIGHT}
        },
        { "luis", "javi", "no one", 
           new CellId[] {CellId.TOP_LEFT,   CellId.TOP_RIGHT ,    CellId.MIDDLE_LEFT, CellId.MIDDLE_RIGHT, CellId.BOTTOM_CENTER}, 
           new CellId[] {CellId.TOP_CENTER, CellId.MIDDLE_CENTER, CellId.BOTTOM_LEFT, CellId.BOTTOM_RIGHT}  
        },
        { "antonio", "pepe", "antonio", 
          new CellId[]{CellId.TOP_LEFT,    CellId.TOP_RIGHT,    CellId.MIDDLE_LEFT, CellId.BOTTOM_CENTER, CellId.BOTTOM_LEFT}, 
          new CellId[]{CellId.TOP_CENTER,  CellId.MIDDLE_CENTER,CellId.MIDDLE_RIGHT,CellId.BOTTOM_RIGHT}, 
        },
    };
        return Arrays.asList(values);
    }
    
    @Parameter(0) public String user1Name;
    @Parameter(1) public String user2Name;
    @Parameter(2) public String winnerName;
    @Parameter(3) public CellId[] user1Clicks;
    @Parameter(4) public CellId[] user2Clicks;
    
    WebDriver browser1;
    WebDriver browser2;
    
    @BeforeClass
    public static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
        WebDriverManager.chromedriver().setup();
        WebApp.start();
    }
    
    @AfterClass
    public static void teardownClass() {
        WebApp.stop();
    }
    
    @Before
    public void setup() {
        createBrowsers();
    }
    
    @After
    public void teardown() {
        quitBrowsers();
    }
    
    @Test
    public void Given2Users_WhenGameFinished_ThenCheckMessage() throws InterruptedException {
        // Given to users
        startGame(user1Name, user2Name);
        
        // When game played
         play(user1Clicks, user2Clicks);
        
        // Then        
        String result = "";
        if (winnerName == user1Name) {
            result = user1Name + " wins! " + user2Name + " looses.";
        } else if (winnerName == user2Name) {
            result = user2Name + " wins! " + user1Name + " looses.";
        } else {
            result = "Draw!";
        }
        
        assertEquals(browser1.switchTo().alert().getText(), result);
        assertEquals(browser2.switchTo().alert().getText(), result);
    }
        
    private void createBrowsers() {
        browser1 = new ChromeDriver();
        browser2 = new FirefoxDriver();
    }

    private void quitBrowsers() {
        if(browser1 != null) {
            browser1.quit();
        }
        if(browser2 != null) {
            browser2.quit();
        }
    }
    
    private void startGame(String userName1, String userName2) throws InterruptedException {
        loginBrowser(browser1, "http://localhost:8080", userName1);
        loginBrowser(browser2, "http://localhost:8080", userName2);
    }

    private void loginBrowser(WebDriver browser, String url, String userName) throws InterruptedException {
        browser.get(url);
        // This has been added for stability, otherwise login may not be performed correctly
        TimeUnit.MILLISECONDS.sleep(300);
        
        browser.findElement(By.id("nickname")).sendKeys(userName);

        browser.findElement(By.className("input-group-btn")).click();
    }
    
    private void clickOnCell(WebDriver browser, int cell) throws InterruptedException {
        browser.findElement(By.id("cell-" + cell)).click();            
    }
    
    private void play(CellId[] user1Clicks, CellId[] user2Clicks) throws InterruptedException {
        int max = Integer.max(user1Clicks.length, user2Clicks.length);
        for(int i = 0; i < max; i++) {
            if (i < user1Clicks.length) {
                clickOnCell(browser1, user1Clicks[i].ordinal());                
            }
            if (i < user2Clicks.length) {
                clickOnCell(browser2, user2Clicks[i].ordinal());                
            }
        }
        // This has been added for stability, otherwise some alert windows are not detected
        TimeUnit.MILLISECONDS.sleep(300);
    }
}
