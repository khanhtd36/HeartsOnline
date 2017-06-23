package controller.networkmessage.msgcontent;

import model.player.Position;

import java.io.Serializable;

public class StartTurnMsgContent implements Serializable {
    public Position position = Position.SOUTH;

    public StartTurnMsgContent(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
