package pojo.issue.updatepriority;

import pojo.Pojo;

import java.util.ArrayList;

public class Update implements Pojo {
    public ArrayList<Object> priority =new ArrayList<>();

    public Update setPriority(Object set) {
        this.priority.add(set);
        return this;
    }
}
