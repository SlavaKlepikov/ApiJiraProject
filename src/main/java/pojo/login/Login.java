package pojo.login;

import pojo.Pojo;

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
