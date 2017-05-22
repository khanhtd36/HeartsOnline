package controller.message;

import java.io.Serializable;

public class UpdateNameMsgContent implements Serializable {
    private int id;
    private String name;

    public UpdateNameMsgContent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
