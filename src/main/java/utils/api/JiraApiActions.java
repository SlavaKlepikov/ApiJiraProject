package utils.api;

import io.restassured.response.ValidatableResponse;
import pojo.issue.addcomment.AddComment;
import pojo.issue.createissue.CreateIssue;
import pojo.issue.updatepriority.UpdatePriority;
import pojo.login.Login;
import pojo.search.Search;

public class JiraApiActions {

    public static ValidatableResponse createIssue(CreateIssue issuePOJO) {
        return HTTPRequestSender.post(APIPathes.issue, issuePOJO);
    }

    public static ValidatableResponse addComment(AddComment addCommentPojo, String issueId) {
        return HTTPRequestSender.post(APIPathes.issue+ issueId + "/comment", addCommentPojo);
    }

    public static ValidatableResponse getComment(String issueId, String commentId) {
        return HTTPRequestSender.get(APIPathes.issue+ issueId + "/comment/"+ commentId);
    }

    public static ValidatableResponse getGroups() {
        return HTTPRequestSender.get(APIPathes.groups+"picker");
    }

    public static ValidatableResponse updatePriorityIssue(String issueId,UpdatePriority updatePriorityIssuePojo) {
        return HTTPRequestSender.put(APIPathes.issue+issueId, updatePriorityIssuePojo );
    }

    public static ValidatableResponse getIssuePriority(String issueId) {
        return HTTPRequestSender.get(APIPathes.issue+ issueId);
    }

    public static ValidatableResponse getIdsProject() {
        return HTTPRequestSender.get(APIPathes.createmeta);
    }

    public static ValidatableResponse getProject(String projectKey) {
        return HTTPRequestSender.get(APIPathes.project+projectKey);
    }

    public static ValidatableResponse getUser(String user) {
        return HTTPRequestSender.get(APIPathes.user+user);
    }

    public static ValidatableResponse searchIssueJql(Search searchIssueJqlPojo) {
        return HTTPRequestSender.post(APIPathes.search, searchIssueJqlPojo);
    }

    public static ValidatableResponse login(Login loginPojo){
        return HTTPRequestSender.post(APIPathes.login, loginPojo);
    }

    public static ValidatableResponse getNonExistingIssue(String issueId) {
        return HTTPRequestSender.get(APIPathes.issue + issueId);
    }

    public static ValidatableResponse deleteIssue(String issueId) {
        return HTTPRequestSender.delete(APIPathes.issue + issueId);
    }

}
