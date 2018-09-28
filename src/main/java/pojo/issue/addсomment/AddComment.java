package pojo.issue.addсomment;

import pojo.Pojo;

public class AddComment implements Pojo {
    public String body;

    public AddComment setBody(String body) {
        this.body = body;
        return this;
    }
}