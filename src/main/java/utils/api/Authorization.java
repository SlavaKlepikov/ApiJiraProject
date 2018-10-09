package utils.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import pojo.login.Login;
import utils.data.JSONFixture;

public class Authorization {

    public static String JSESSIONID;
    public static String baseURI = "http://jira.hillel.it:8080";
    public static String username = "webinar5";
    public static String password = "webinar5";

    public static void loginToJIRA() {
        RestAssured.baseURI = baseURI;

        Login loginPojo = JSONFixture.generateLogin(username, password);
        ValidatableResponse responselogin = JiraApiActions.login(loginPojo);

        Assert.assertEquals(responselogin.extract().statusCode(), 200);
        Assert.assertTrue(responselogin.extract().contentType().contains(ContentType.JSON.toString()));

        JSESSIONID = responselogin.extract().path("session.value");

        Assert.assertTrue(JSESSIONID.matches("[A-Z0-9]{32}"));
    }
}
