package it.hillel.jira.test;

import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;


public class TestJiraApi {


    @Test
    public void validateURL() {
        given().get("http://jira.hillel.it:8080/rest/auth/1/session").then().statusCode(401).log().all();
    }
}
