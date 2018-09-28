package utils.api;

import io.restassured.response.ValidatableResponse;
import pojo.issue.addComment.AddComment;
import pojo.issue.createIssue.CreateIssue;
import pojo.issue.updatePriority.UpdatePriority;
import pojo.login.Login;
import pojo.search.Search;

public class JiraApiActions {

    public static ValidatableResponse createIssue(CreateIssue issuePOJO) {
        ValidatableResponse response = HTTPRequestSender.post(APIPathes.issue, issuePOJO);
        return response;
    }

    public static ValidatableResponse addComment(AddComment addCommentPojo, String issueId) {
        ValidatableResponse response = HTTPRequestSender.post(APIPathes.issue+ issueId + "/comment", addCommentPojo);
        return response;
    }

    public static ValidatableResponse getComment(String issueId, String commentId) {
        ValidatableResponse response = HTTPRequestSender.get(APIPathes.issue+ issueId + "/comment/"+ commentId);
        return response;
    }

    public static ValidatableResponse getGroups() {
        ValidatableResponse response = HTTPRequestSender.get(APIPathes.groups+"picker");
        return response;
    }

    public static ValidatableResponse updatePriorityIssue(String issueId,UpdatePriority updatePriorityIssuePojo) {
        ValidatableResponse response = HTTPRequestSender.put(APIPathes.issue+issueId, updatePriorityIssuePojo );
        return response;
    }

    public static ValidatableResponse getIssuePriority(String issueId) {
        ValidatableResponse response = HTTPRequestSender.get(APIPathes.issue+ issueId);
        return response;
    }

    public static ValidatableResponse getIdsProject() {
        ValidatableResponse response = HTTPRequestSender.get(APIPathes.createmeta);
        return response;
    }

    public static ValidatableResponse getProject(String projectKey) {
        ValidatableResponse response = HTTPRequestSender.get(APIPathes.project+projectKey);
        return response;
    }

    public static ValidatableResponse getUser(String user) {
        ValidatableResponse response = HTTPRequestSender.get(APIPathes.user+user);
        return response;
    }

    public static ValidatableResponse searchIssueJql(Search searchIssueJqlPojo) {
        ValidatableResponse response = HTTPRequestSender.post(APIPathes.search, searchIssueJqlPojo);
        return response;
    }

    public static ValidatableResponse login(Login loginPojo){
    ValidatableResponse response = HTTPRequestSender.post(APIPathes.login, loginPojo);
        return response;
    }


//    public static String getIssue(String issueKey) {
//        ValidatableResponse response = HTTPRequestSender.get(APIPathes.issue + issueKey);
//        Assert.assertEquals(response.extract().statusCode(), 200);
//        Assert.assertTrue(response.extract().contentType().contains(ContentType.JSON.toString()));
//        return response.extract().asString();
//    }

    public static ValidatableResponse getNonExistingIssue(String issueId) {
        ValidatableResponse response = HTTPRequestSender.get(APIPathes.issue + issueId);
        return response;
    }

    public static ValidatableResponse deleteIssue(String issueId) {
        ValidatableResponse response = HTTPRequestSender.delete(APIPathes.issue + issueId);
        return response;
    }

}
