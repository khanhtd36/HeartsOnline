package connection.networkmessage.msgcontent;

import model.player.Player;
import model.player.Position;

import java.io.Serializable;
import java.util.List;

public class JoinAcceptMsgContent implements Serializable {
    private Position myPosition;
    private Player[] otherPlayers = new Player[3];

    public JoinAcceptMsgContent(Position myPosition, List<Player> otherPlayers) {
        this.myPosition = myPosition;
        int index = 0;
        for(Player player : otherPlayers) {
            this.otherPlayers[index] = player;
            index++;
        }
    }

    public Position getMyPosition() {
        return myPosition;
    }

    public void setMyPosition(Position myPosition) {
        this.myPosition = myPosition;
    }

    public Player[] getOtherPlayers() {
        return otherPlayers;
    }

    public void setOtherPlayers(Player[] otherPlayers) {
        this.otherPlayers = otherPlayers;
    }
}
