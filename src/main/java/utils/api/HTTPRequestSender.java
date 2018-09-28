package utils.api;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import pojo.Pojo;

import static io.restassured.RestAssured.given;

public class HTTPRequestSender {

    public static ValidatableResponse get(String urlPath) {
        ValidatableResponse response = given().
                header("Content-Type", ContentType.JSON).
                header("Cookie", "JSESSIONID=" + Authorization.JSESSIONID).
                when().
                get(urlPath).
                then();

        String responseBody = response.extract().asString();

        return response;
    }

    public static ValidatableResponse post(String urlPath, Pojo body) {
        System.out.println(Authorization.JSESSIONID);
        ValidatableResponse response = given().
                header("Content-Type", ContentType.JSON).
                header("Cookie", "JSESSIONID=" + Authorization.JSESSIONID).
                body(body).
                when().
                post(urlPath).
                then();

        String responseBody = response.extract().asString();

        return response;
    }

    public static ValidatableResponse put(String urlPath, Pojo body) {
        ValidatableResponse response = given().
                header("Content-Type", ContentType.JSON).
                header("Cookie", "JSESSIONID=" + Authorization.JSESSIONID).
                body(body).
                when().
                put(urlPath).
                then();

        String responseBody = response.extract().asString();

        return response;
    }

    public static ValidatableResponse delete(String urlPath) {
        ValidatableResponse response = given().
                header("Content-Type", ContentType.JSON).
                header("Cookie", "JSESSIONID=" + Authorization.JSESSIONID).
                when().
                delete(urlPath).
                then();

        String responseBody = response.extract().asString();

        return response;
    }
}
