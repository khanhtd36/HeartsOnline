package connection.networkmessage.msgcontent;

import model.card.Card;
import model.card.CardName;
import model.player.Position;

import java.io.Serializable;

public class PlayACardMsgContent implements Serializable {
    private Position position = Position.SOUTH;
    private Card card = new Card(CardName.UNKNOWN);

    public PlayACardMsgContent(Position position, Card card) {
        this.position = position;
        this.card = card;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
