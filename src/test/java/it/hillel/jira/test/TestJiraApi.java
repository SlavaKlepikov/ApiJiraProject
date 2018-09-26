package it.hillel.jira.test;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import pojo.issue.addComment.AddComment;
import pojo.issue.createIssue.Fields;
import pojo.issue.createIssue.CreateIssue;
import pojo.login.Login;
import pojo.search.Search;
import pojo.issue.updatePriority.*;
import utils.api.APIPathes;

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

        Login loginPojo = new Login();
        loginPojo.setUsername(username);
        loginPojo.setPassword(password);

        sessionId = given().
            header("Content-Type", "application/json").
            body(loginPojo).
            when().
            post(APIPathes.login).
            then().
            statusCode(200).log().all().
            extract().path("session.value");
        }

    //Check correct URL and status code 401
    @Test
    public void validateAuthUrlTest() {
        given().get(APIPathes.login).then().statusCode(401).log().all(); //Code 401: Returned if the authentication credentials are incorrect or missing.
    }

    //Get Id's, Projects, Type and check correct response
    @Test
    public void getIdsProjectTypeTest() {
        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get(APIPathes.createmeta).
                then().
                statusCode(200).log().all(); //Status code 200: Returned if the request is successful.
        String responseIdsProjectType = response.extract().asString();
    }

    //Check status code 401 on wrong username
    @Test
    public void loginWrongUsernameTest() {

        Login loginPojo = new Login();
        loginPojo.setUsername("WrongUsername");
        loginPojo.setPassword(password);

        ValidatableResponse responseLoginWrongUsername = given().
                header("Content-Type", "application/json").
                body(loginPojo).
                when().
                post(APIPathes.login).
                then().
                statusCode(401).log().all(); //Code 401: Returned if the authentication credentials are incorrect or missing.
    }

    //Check status code 401 on wrong password.
    @Test
    public void loginWrongPasswordTest() {
        Login loginPojo = new Login();
        loginPojo.setUsername(username);
        loginPojo.setPassword("WrongPassword");

        ValidatableResponse responseLoginWrongPassword  = given().
                header("Content-Type", "application/json").
                body(loginPojo).
                when().
                post(APIPathes.login).
                then().
                statusCode(401).log().all(); //Code 401: Returned if the authentication credentials are incorrect or missing.
    }

    //Create the issue and receive issueId.
    @Test
    public void createIssueTest() {

        String fieldsSummary = "API test somethings wrong";
        String issueTypeId = "10105";
        String assigneeName = "Klepikov_Vjacheslav";

        Fields fieldsPojo = new Fields();
        fieldsPojo.setSummary(fieldsSummary);
        fieldsPojo.setIssueType(issueTypeId);
        fieldsPojo.setProject(projectKey);
        fieldsPojo.setAssignee(assigneeName);
        CreateIssue createIssuePojo = new CreateIssue(fieldsPojo);

        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                body(createIssuePojo).
                when().
                post(APIPathes.issue).
                then().
                statusCode(201).log().all(); // Code 201: Returns a link to the created issue.
        issueId = response.extract().path("id");
    }

     //Add the comment and receive the commentId
     @Test (dependsOnMethods = "createIssueTest")
     public void addCommentIssueTest() {

         AddComment addCommentPojo = new AddComment();
         addCommentPojo.setBody("This is a comment was add via APT test.");

         ValidatableResponse responseAddComment = given().
                 header("Content-Type", "application/json").
                 header("Cookie", "JSESSIONID=" + sessionId).
                 body(addCommentPojo).
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
                    get(APIPathes.issue + issueId + "/comment/" + commentId).
                    then().
                    statusCode(200).log().all(); //Code 200: Returns a full representation of a Jira comment in JSON format.
            Assert.assertEquals(responseGetComment.extract().path("body"),"This is a comment was add via APT test.");
        }

    //Update priority
    @Test (dependsOnMethods = "createIssueTest")
    public void updatePriorityIssueTest() {

        Set setPojo =new Set("High");
        Priority priorityPojo = new Priority(setPojo);
        Update updatePojo = new Update();
        updatePojo.setPriority(priorityPojo);
        UpdatePriority updatePriorityPojo = new UpdatePriority(updatePojo);
        
        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                body(updatePriorityPojo).//"{\"update\":{\"priority\":[{\"set\":{\"name\":\"High\"}}]}}"
                when().
                put(APIPathes.issue+issueId).
                then().
                statusCode(204).log().all(); //Code 204:Returned if the request is successfully.
    }

    //Check the priority
    @Test (dependsOnMethods = "updatePriorityIssueTest")
    public void getIssuePriorityTest() {
        ValidatableResponse responsegetIssuePriority = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get(APIPathes.issue + issueId ).
                then().
                statusCode(200).log().all(); //Code 200: Returns a full representation of an issue in JSON format.
        Assert.assertEquals(responsegetIssuePriority.extract().path("fields.priority.name"),"High");
    }

    //Search for issue using JQL
    @Test (dependsOnMethods = "createIssueTest")
    public void searchIssueJqlTest() {

        Search searchIssueJqlPojo =new Search("project = QAAUT6 AND summary  ~ \"API test somethings wrong\"");

        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                body(searchIssueJqlPojo).
                when().
                post(APIPathes.search).
                then().
                statusCode(200).log().all(); //Code 200: Returned if the request is successful.
        Assert.assertEquals(response.extract().path("issues[0].id"),issueId);
    }

    //Get current User, Check name
    @Test
    public void getUserTest() {
        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get (APIPathes.user +"myself").
                then() .
                statusCode(200).log().all(); //Code 200: Returned if the request is successful.
        Assert.assertEquals(response.extract().path("name"),username);
    }

    //Get Project, Check status code 200 and check project key
    @Test
    public void getProgectTest() {
        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get (APIPathes.project+projectKey).
                then() .
                statusCode(200).log().all(); //Code 200: Returned if the request is successful.
        Assert.assertEquals(response.extract().path("key"),projectKey);
    }

    //Get Groups, Check status code 200
    @Test
    public void getGroupsTest() {
        ValidatableResponse response = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get (APIPathes.groups+"picker").
                then() .
                statusCode(200).log().all(); //Code 200: Returned if the request is successful.
    }

    //Delete issue
    @AfterTest
    public void deleteIssueTest() {
        ValidatableResponse responseDelete = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                delete(APIPathes.issue + issueId).
                then().
                statusCode(204).log().all(); //Code 204: Returned if the issue was successfully removed.
    }

    //Check that issue was delete and not found.
    @AfterSuite
    public void getIssueNegativeTest() {
        ValidatableResponse responsegetIssuePriority = given().
                header("Content-Type", "application/json").
                header("Cookie", "JSESSIONID=" + sessionId).
                when().
                get(APIPathes.issue  + issueId ).
                then().
                statusCode(404).log().all(); //Code 404: Returned if the requested issue was not found, or the user does not have permission to view it.
    }

}
