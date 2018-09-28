package utils.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import pojo.Pojo;
import pojo.issue.addComment.AddComment;
import pojo.issue.createIssue.CreateIssue;
import pojo.issue.createIssue.Fields;
import pojo.issue.updatePriority.Priority;
import pojo.issue.updatePriority.Set;
import pojo.issue.updatePriority.Update;
import pojo.issue.updatePriority.UpdatePriority;
import pojo.login.Login;
import pojo.search.Search;

public class JSONFixture {

    public static String extractPOJO(Pojo pojo) {
    ObjectMapper mapper = new ObjectMapper();
    String result = null;
    try {
        result = mapper.writeValueAsString(pojo);
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }
    return result;
    }

    public static CreateIssue generateJSONForCreateIssue(String projectKey, String fieldsSummary, String issueTypeId, String assigneeName) {

        Fields fieldsPojo = new Fields();
        fieldsPojo.setSummary(fieldsSummary);
        fieldsPojo.setIssueType(issueTypeId);
        fieldsPojo.setProject(projectKey);
        fieldsPojo.setAssignee(assigneeName);
        CreateIssue createIssuePojo = new CreateIssue(fieldsPojo);
        return createIssuePojo;
    }

    public static AddComment generateJSONForAddComment(String comment){
        AddComment addCommentPojo = new AddComment();
        addCommentPojo.setBody(comment);
        return addCommentPojo;
    }

    public static UpdatePriority generateJSONForUpdatePriorityIssue(String comment){
        Set setPojo =new Set(comment);
        Priority priorityPojo = new Priority(setPojo);
        Update updatePojo = new Update();
        updatePojo.setPriority(priorityPojo);
        UpdatePriority updatePriorityPojo = new UpdatePriority(updatePojo);
        return updatePriorityPojo;
    }

    public static Search generateSearchIssueJql(String jql){
        Search searchIssueJqlPojo =new Search(jql);
        return searchIssueJqlPojo;
    }

    public static Login generateLogin(String username, String password){
        Login loginPojo = new Login();
        loginPojo.setUsername(username);
        loginPojo.setPassword(password);
        return loginPojo;
    }

}
