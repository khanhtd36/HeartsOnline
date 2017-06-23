package controller.networkmessage.msgcontent;

import model.card.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CardDeskMsgContent implements Serializable {
    List<Card> cardDesk = new ArrayList<>();

    public CardDeskMsgContent(List<Card> cardDesk) {
        this.cardDesk = cardDesk;
    }

    public List<Card> getCardDesk() {
        return cardDesk;
    }

    public void setCardDesk(List<Card> cardDesk) {
        this.cardDesk = cardDesk;
    }
}
