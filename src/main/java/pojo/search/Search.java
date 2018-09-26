package pojo.search;

import pojo.Pojo;

public class Search implements Pojo {
    public String jql;

    public Search(String jql) {
        this.jql = jql;
    }
}
