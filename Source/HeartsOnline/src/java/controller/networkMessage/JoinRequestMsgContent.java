package controller.networkMessage;

import java.io.Serializable;

public class JoinRequestMsgContent implements Serializable {
    private String name;

    public JoinRequestMsgContent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
