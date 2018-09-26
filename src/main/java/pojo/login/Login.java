package pojo.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import pojo.Pojo;
import pojo.issue.createIssue.Fields;
import pojo.issue.createIssue.Project;

public class Login implements Pojo {


    public String username;
    public String password;

    public Login() {
    }

    public Login setUsername(String username) {
        this.username = username;
        return this;
    }

    public Login setPassword(String password) {
        this.password = password;
        return this;
    }
}
