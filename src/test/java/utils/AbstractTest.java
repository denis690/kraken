package utils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

import java.sql.Timestamp;
import java.util.Date;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;

abstract public class AbstractTest {

    private long scenarioStartTimeMills;

    @Rule
    public TestName name = new TestName();

    @BeforeClass
    public static void superBeforeClass() {
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Before
    public void before() {
        scenarioStartTimeMills = System.currentTimeMillis();
        System.out.println("\n*** " + new Timestamp(new Date().getTime()) + " Started test " + name.getMethodName() + " ***\n");
    }

    @After
    public void superAfter() {
        System.out.println("\n*** " + new Timestamp(new Date().getTime()) + " Completed test " + name.getMethodName() + " in " + (System.currentTimeMillis() - scenarioStartTimeMills) + " msec ***\n");
    }
}

