package controller.networkmessage.msgcontent;

import model.player.Position;

import java.io.Serializable;

public class APlayerExitMsgContent implements Serializable {
    Position position;

    public APlayerExitMsgContent(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
