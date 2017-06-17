package controller.networkMessage;

import model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class CardDeskMsgContent {
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
