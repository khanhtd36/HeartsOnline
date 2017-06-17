package controller.networkMessage;

import model.player.Position;

import java.io.Serializable;

public class NewPlayerJoinMsgContent implements Serializable {
    private Position position;
    private String name = "UNKNOWN";

    public NewPlayerJoinMsgContent(Position position, String name) {
        this.position = position;
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
