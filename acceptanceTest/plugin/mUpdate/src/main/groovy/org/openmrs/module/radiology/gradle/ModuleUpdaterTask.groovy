package org.openmrs.module.radiology.gradle
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ModuleUpdaterTask extends DefaultTask {
    String uName = "admin";
    String uPW = "Admin123";
    String updaterBaseUrl = "http://localhost:8080/"

    @TaskAction
    def updateModule() {
        println 'Updating module...'
        File newModuleFile;
        if(System.getProperty("installFile")==null)
        {
            File mainProjectDirectory = new File("").getAbsoluteFile().parentFile
            newModuleFile = new File(mainProjectDirectory.absolutePath+"\\omod\\target\\radiology-0.1.0.0-dev-SNAPSHOT.omod")
        }
        else
        {
            newModuleFile = new File(System.getProperty("installFile"))
        }

        if(newModuleFile.exists())
        {
            println 'Starting...'
            //Setup
            HtmlUnitDriver driver = new HtmlUnitDriver(true);
            //Login
            doMessage("Start to log in...")
            driver.get(updaterBaseUrl + "/openmrs/index.htm");
            driver.findElement(By.id("username")).clear();
            driver.findElement(By.id("username")).sendKeys(uName);
            driver.findElement(By.id("password")).clear();
            driver.findElement(By.id("password")).sendKeys(uPW);
            driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
            //Action
            doMessage("Go to config site...")
            driver.findElement(By.linkText("Administration")).click();
            driver.findElement(By.linkText("Manage Modules")).click();
            driver.findElement(By.id("addUpgradeButton")).click();
            driver.findElement(By.cssSelector("#uploadUpdateForm > input[name=\"moduleFile\"]")).clear();
            driver.findElement(By.cssSelector("#uploadUpdateForm > input[name=\"moduleFile\"]")).sendKeys(newModuleFile.getAbsolutePath());
            doMessage("Start to upload file...")
            driver.findElement(By.cssSelector("#uploadUpdateForm > input[type=\"submit\"]")).click();
            //Check
            doMessage("File uploaded! Start checking...")
            String wantedAnswerMessage = "Module loaded and started successfully";
            String realAnswerMessage = driver.findElement(By.id("openmrs_msg")).getText();

            if(realAnswerMessage.equals(wantedAnswerMessage)==false)
            {
                throw new NoSuchElementException("The expected OpenMRS answer-message should be \""
                        +wantedAnswerMessage+"\", but got \""+realAnswerMessage+"\"");
            }
            else
            {
                doMessage("Check passed! The OpenMRS-Message contains \""+realAnswerMessage+"\"")
            }
            //Logout
            driver.findElement(By.linkText("Log out")).click();
            doMessage("Update-process passed.")
        }
        else
        {
            println "Cannot update module with given file \""+newModuleFile.absolutePath+"\": The file does not exist.";
        }

    }

    def doMessage(String message)
    {
        println "";
        println message;
        println "";
    }
}