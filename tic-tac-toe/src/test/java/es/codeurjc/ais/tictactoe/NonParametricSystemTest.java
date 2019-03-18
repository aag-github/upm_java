package es.codeurjc.ais.tictactoe;

import static org.junit.Assert.assertEquals;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class NonParametricSystemTest {

    WebDriver browser1;
    WebDriver browser2;
    
    @BeforeClass
    public static void setupClass() throws InterruptedException {
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
    public void Given2Users_WhenUser1Wins_ThenWinMessageForUser1() throws InterruptedException {
        // Given to users
        String user1 = "pepe";
        String user2 = "juan";
        startGame(user1, user2);
        
        // When user1 wins
        CellId[] clicks1 = {CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.TOP_RIGHT};
        CellId[] clicks2 = {CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER};
        play(clicks1, clicks2);
        
        // Then
        String result = user1 + " wins! " + user2 + " looses.";
        assertEquals(browser1.switchTo().alert().getText(), result);
        assertEquals(browser2.switchTo().alert().getText(), result);
    }
    
    @Test
    public void Given2Users_WhenUser2Wins_ThenWinMessageForUser2() throws InterruptedException {
        // Given to users
        String user1 = "pepe";
        String user2 = "juan";
        startGame(user1, user2);
        
        // When user2 wins
        CellId[] clicks1 = {CellId.TOP_LEFT,    CellId.TOP_CENTER,    CellId.BOTTOM_RIGHT};
        CellId[] clicks2 = {CellId.MIDDLE_LEFT, CellId.MIDDLE_CENTER, CellId.MIDDLE_RIGHT};
        play(clicks1, clicks2);

        String result = user2 + " wins! " + user1 + " looses.";
        assertEquals(browser1.switchTo().alert().getText(), result);
        assertEquals(browser2.switchTo().alert().getText(), result);
    }
    
    @Test
    public void Given2Users_WhenDraw_ThenDrawMessage() throws InterruptedException {
        // Given to users
        String user1 = "pepe";
        String user2 = "juan";
        startGame(user1, user2);
        
        // When Draw
        CellId[] u1 = { CellId.TOP_LEFT,   CellId.TOP_RIGHT ,    CellId.MIDDLE_LEFT, CellId.MIDDLE_RIGHT, CellId.BOTTOM_CENTER};
        CellId[] u2 = { CellId.TOP_CENTER, CellId.MIDDLE_CENTER, CellId.BOTTOM_LEFT, CellId.BOTTOM_RIGHT  };
        play(u1, u2);

        // Then
        String result = "Draw!";
        assertEquals(browser1.switchTo().alert().getText(), result);
        assertEquals(browser2.switchTo().alert().getText(), result);
    }
    
    private void createBrowsers() {
        browser1 = new ChromeDriver();
        browser2 = new ChromeDriver();
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
        TimeUnit.MILLISECONDS.sleep(200);
    }
}
