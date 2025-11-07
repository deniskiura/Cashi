package ke.kiura.cashi.bdd

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["src/androidUnitTest/resources/features"],
    glue = ["ke.kiura.cashi.bdd.steps"],
    plugin = [
        "pretty",
        "html:build/reports/cucumber/cucumber-report.html",
        "json:build/reports/cucumber/cucumber.json"
    ],
    monochrome = true
)
class CucumberTestRunner
