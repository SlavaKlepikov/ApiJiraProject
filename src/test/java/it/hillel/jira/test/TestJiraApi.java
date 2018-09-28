package it.hillel.jira.test;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import pojo.issue.addсomment.AddComment;
import pojo.issue.createissue.CreateIssue;
import pojo.issue.updatepriority.UpdatePriority;
import pojo.login.Login;
import pojo.search.Search;
import utils.api.APIPathes;
import utils.api.Authorization;
import utils.api.JiraApiActions;
import utils.data.JSONFixture;

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
        Authorization.loginToJIRA();
    }


    @Test
    public void validateAuthUrlTest() {
        given().get(APIPathes.login).then().statusCode(401).log().all(); //Code 401: Returned if the authentication credentials are incorrect or missing.
    }

    //Get Id's, Projects, Type and check correct response
    @Test
    public void getIdsProjectTest() {
        ValidatableResponse responseIdsProject = JiraApiActions.getIdsProject();

        Assert.assertEquals(responseIdsProject.extract().statusCode(), 200);
        Assert.assertTrue(responseIdsProject.extract().contentType().contains(ContentType.JSON.toString()));
    }

    //Check status code 401 on wrong username
    @Test
    public void loginWrongUsernameTest() {
        String username="WrongUsername";
        String password="webinar5";

        Login loginPojo = JSONFixture.generateLogin(username,password);
        ValidatableResponse responseloginWrongUsername = JiraApiActions.login(loginPojo);

        Assert.assertEquals(responseloginWrongUsername.extract().statusCode(), 401);
        Assert.assertTrue(responseloginWrongUsername.extract().contentType().contains(ContentType.JSON.toString()));
    }

    //Check status code 401 on wrong password.
    @Test
    public void loginWrongPasswordTest() {
        String username="webinar5";
        String password="WrongPassword";

        Login loginPojo = JSONFixture.generateLogin(username,password);
        ValidatableResponse responseloginWrongPassword = JiraApiActions.login(loginPojo);

        Assert.assertEquals(responseloginWrongPassword.extract().statusCode(), 401);
        Assert.assertTrue(responseloginWrongPassword.extract().contentType().contains(ContentType.JSON.toString()));
    }

    //Create the issue and receive issueId.
    @Test
    public void createIssueTest() {

        String fieldsSummary = "API test somethings wrong";
        String issueTypeId = "10105";
        String assigneeName = "Klepikov_Vjacheslav";

        CreateIssue issuePOJO = JSONFixture.generateJSONForCreateIssue(projectKey, fieldsSummary, issueTypeId, assigneeName);
        ValidatableResponse responseCreateIssue =JiraApiActions.createIssue(issuePOJO);

        responseCreateIssue.statusCode(201);
        responseCreateIssue.contentType(ContentType.JSON);

        issueId=responseCreateIssue.extract().path("id");
    }

     //Add the comment and receive the commentId
     @Test (dependsOnMethods = "createIssueTest")
     public void addCommentIssueTest() {

         String comment = "This is a comment was add via APT test.";

         AddComment addCommentPojo = JSONFixture.generateJSONForAddComment(comment);
         ValidatableResponse responseAddComment = JiraApiActions.addComment(addCommentPojo, issueId);

         responseAddComment.statusCode(201);
         responseAddComment.contentType(ContentType.JSON);

         commentId=responseAddComment.extract().path("id");
     }

     //Check the comment
     @Test (dependsOnMethods = "addCommentIssueTest")
     public void getCommentIssueTest() {

         ValidatableResponse responseGetComment = JiraApiActions.getComment(issueId, commentId);

         responseGetComment.statusCode(200);
         responseGetComment.contentType(ContentType.JSON);
         Assert.assertEquals(responseGetComment.extract().path("body"), "This is a comment was add via APT test.");
     }

    //Update priority
    @Test (dependsOnMethods = "createIssueTest")
     public void updatePriorityIssueTest() {

        String priority = "High";

        UpdatePriority updatePriorityIssuePojo = JSONFixture.generateJSONForUpdatePriorityIssue(priority);
        ValidatableResponse responseUpdatePriorityIssue = JiraApiActions.updatePriorityIssue(issueId, updatePriorityIssuePojo);

        responseUpdatePriorityIssue.statusCode(204);
        responseUpdatePriorityIssue.contentType(ContentType.JSON);
    }

    //Check the priority
    @Test (dependsOnMethods = {"updatePriorityIssueTest","createIssueTest"})
    public void getIssuePriorityTest() {

        ValidatableResponse responseIssuePriority = JiraApiActions.getIssuePriority(issueId);

        responseIssuePriority.statusCode(200);
        responseIssuePriority.contentType(ContentType.JSON);
        Assert.assertEquals(responseIssuePriority.extract().path("fields.priority.name"),"High");
    }

    //Search for issue using JQL
    @Test (dependsOnMethods = "createIssueTest")
    public void searchIssueJqlTest() {
        String jql = "project = QAAUT6 AND summary  ~ \"API test somethings wrong\"";

        Search searchIssueJqlPojo = JSONFixture.generateSearchIssueJql(jql);
        ValidatableResponse responseSearchIssueJql = JiraApiActions.searchIssueJql(searchIssueJqlPojo);

        responseSearchIssueJql.statusCode(200);
        responseSearchIssueJql.contentType(ContentType.JSON);

        Assert.assertEquals(responseSearchIssueJql.extract().path("issues[0].id"),issueId);
    }

    //Get current User, Check name
    @Test
    public void getUserTest() {
        String user = "myself";

        ValidatableResponse responseUserTest = JiraApiActions.getUser(user);

        responseUserTest.statusCode(200);
        responseUserTest.contentType(ContentType.JSON);
        Assert.assertEquals(responseUserTest.extract().path("name"),username);
    }

    //Get Project, Check status code 200 and check project key
    @Test
    public void getProgectTest() {
        ValidatableResponse responseProgect = JiraApiActions.getProject(projectKey);

        responseProgect.statusCode(200);
        responseProgect.contentType(ContentType.JSON);
        Assert.assertEquals(responseProgect.extract().path("key"),projectKey);
    }

    //Get Groups, Check status code 200
    @Test
    public void getGroupsTest() {

        ValidatableResponse responseGroups = JiraApiActions.getGroups();

        responseGroups.statusCode(200);
        responseGroups.contentType(ContentType.JSON);
    }

    //Delete issue
    @AfterTest
    public void deleteIssueTest() {

        ValidatableResponse responseDeleteIssue = JiraApiActions.deleteIssue(issueId);

        responseDeleteIssue.statusCode(204);
        responseDeleteIssue.contentType(ContentType.JSON);
    }

    //Check that issue was delete and not found.
    @AfterSuite
    public void getNonExistingIssue() {
        ValidatableResponse responseNonExistingIssue = JiraApiActions.deleteIssue(issueId);
        Assert.assertEquals(responseNonExistingIssue.extract().statusCode(), 404);
        Assert.assertTrue(responseNonExistingIssue.extract().contentType().contains(ContentType.JSON.toString()));
    }

}
