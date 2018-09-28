package pojo.issue.createissue;

import pojo.Pojo;

public class Assignee implements Pojo {
    public String name;

    public Assignee(String id) {
        this.name = id;
    }
}
