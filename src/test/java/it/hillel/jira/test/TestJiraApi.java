package it.hillel.jira.test;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import static io.restassured.RestAssured.given;

public class TestJiraApi {
    String username = "webinar5";
    String password = "webinar5";
    String sessionId;
    String issueId;
    String commentId;
    String projectKey="QAAUT6";

    @BeforeSuite
    public void setupMethod(){
        RestAssured.baseURI = "http://jira.hillel.it";
        RestAssured.port = 8080;
    }

    //Checking successfully authentication and received sessionId.
    @BeforeTest
    public void loginSessionIdTest() {
        JSONObject login = new JSONObject();
        login.put("username",username);
        login.put("password",password);

        sessionId = given().
            header("Content-Type", "application/json").
            body(login.toString()).
            when().
            post("/rest/auth/1/session").
            then().
            statusCode(200).log().all().
            extract().path("session.value");
        }

    //Check correct URL and status code 401
    @Test
    public void validateAuthUrlTest() {
        given().get("/rest/auth/1/session").then().statusCode(401).log().all(); //Code 401: Returned if the authentication credentials are incorrect or missing.
    }

    //Get Id's, Projects, Type and check correct response
    @Test
    public void getIdsProjectTypeTest() {
        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get("/rest/api/2/issue/createmeta").
                then().
                statusCode(200).log().all(); //Status code 200: Returned if the request is successful.
        String responseIdsProjectType = response.extract().asString();
    }

    //Check status code 401 on wrong username
    @Test
    public void loginWrongUsernameTest() {
        JSONObject login = new JSONObject();
        login.put("username","WrongUsername");
        login.put("password",password);

        ValidatableResponse responseLoginWrongUsername = given().
                header("Content-Type", "application/json").
                body(login.toString()).
                when().
                post("/rest/auth/1/session").
                then().
                statusCode(401).log().all(); //Code 401: Returned if the authentication credentials are incorrect or missing.
    }

    //Check status code 401 on wrong password.
    @Test
    public void loginWrongPasswordTest() {
        JSONObject login = new JSONObject();
        login.put("username",username);
        login.put("password","WrongPassword");

        ValidatableResponse responseLoginWrongPassword  = given().
                header("Content-Type", "application/json").
                body(login.toString()).
                when().
                post("/rest/auth/1/session").
                then().
                statusCode(401).log().all(); //Code 401: Returned if the authentication credentials are incorrect or missing.
    }

    //Create the issue and receive issueId.
    @Test
    public void createIssueTest() {

        String fieldsSummary = "API test somethings wrong";
        String issueTypeId = "10105";
        String assigneeName = "Klepikov_Vjacheslav";

        JSONObject issueCreate = new JSONObject();
        JSONObject fields = new JSONObject();
        JSONObject project = new JSONObject();
        JSONObject issueType = new JSONObject();
        JSONObject assignee = new JSONObject();

        project.put("key", projectKey);
        issueType.put("id", issueTypeId);
        assignee.put("name", assigneeName);
        fields.put("project", project);
        fields.put("issuetype", issueType);
        fields.put("assignee", assignee);
        fields.put("summary", fieldsSummary);
        issueCreate.put("fields", fields);

        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                body(issueCreate.toString()).
                when().
                post("/rest/api/2/issue").
                then().
                statusCode(201).log().all(); // Code 201: Returns a link to the created issue.
        //String responseBody = response.extract().asString();
        issueId = response.extract().path("id");
    }

     //Add the comment and receive the commentId
     @Test (dependsOnMethods = "createIssueTest")
     public void addCommentIssueTest() {

         JSONObject issueComment = new JSONObject();
         issueComment.put("body", "This is a comment was add via APT test.");

         ValidatableResponse responseAddComment = given().
                 header("Content-Type", "application/json").
                 header("Cookie", "JSESSIONID=" + sessionId).
                 body(issueComment.toString()).
                 when().
                 post("/rest/api/2/issue/" + issueId + "/comment").
                 then().
                 statusCode(201).log().all(); //Code 201: Returned if add was successful
            commentId = responseAddComment.extract().path("id");
        }

        //Check the comment
        @Test (dependsOnMethods = "addCommentIssueTest")
        public void getCommentIssueTest() {

            ValidatableResponse responseGetComment = given().
                    header("Content-Type", "application/json").
                    header("Cookie", "JSESSIONID=" + sessionId).
                    when().
                    get("/rest/api/2/issue/" + issueId + "/comment/" + commentId).
                    then().
                    statusCode(200).log().all(); //Code 200: Returns a full representation of a Jira comment in JSON format.
            Assert.assertEquals(responseGetComment.extract().path("body"),"This is a comment was add via APT test.");
        }

    //Update priority
    @Test (dependsOnMethods = "createIssueTest")
    public void updatePriorityIssueTest() {
        JSONObject updatePriority = new JSONObject();
        JSONObject update = new JSONObject();
        JSONObject priorityArray = new JSONObject();
        JSONArray priority = new JSONArray();
        JSONObject set = new JSONObject();

        updatePriority.put("update", update);
        update.put("priority",priority);
        priority.add(priorityArray);
        priorityArray.put("set", set);
        set.put("name","High");

        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                body(updatePriority.toString()).//"{\"update\":{\"priority\":[{\"set\":{\"name\":\"High\"}}]}}"
                when().
                put("/rest/api/2/issue/"+issueId).
                then().
                statusCode(204).log().all(); //Code 204:Returned if the request is successfully.
        String responseBody = response.extract().asString();
        System.out.println(responseBody);
    }

    //Check the priority
    @Test (dependsOnMethods = "updatePriorityIssueTest")
    public void getIssuePriorityTest() {
        ValidatableResponse responsegetIssuePriority = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get("/rest/api/2/issue/" + issueId ).
                then().
                statusCode(200).log().all(); //Code 200: Returns a full representation of an issue in JSON format.
        Assert.assertEquals(responsegetIssuePriority.extract().path("fields.priority.name"),"High");
    }

    //Delete issue
    @AfterTest
    public void deleteIssueTest() {
        ValidatableResponse responseDelete = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                delete("/rest/api/2/issue/" + issueId).
                then().
                statusCode(204).log().all(); //Code 204: Returned if the issue was successfully removed.
    }

    //Check that issue not found.
    @AfterSuite
    public void getIssueNegativeTest() {
        ValidatableResponse responsegetIssuePriority = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get("/rest/api/2/issue/" + issueId ).
                then().
                statusCode(404).log().all(); //Code 404: Returned if the requested issue was not found, or the user does not have permission to view it.
    }
}
