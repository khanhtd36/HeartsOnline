package controller.networkMessage;

import model.card.Card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExchangeCardsMsgContent implements Serializable {
    List<Card> exchangeCards = new ArrayList<>();

    public ExchangeCardsMsgContent(List<Card> exchangeCards) {
        this.exchangeCards = exchangeCards;
    }

    public List<Card> getExchangeCards() {
        return exchangeCards;
    }

    public void setExchangeCards(List<Card> exchangeCards) {
        this.exchangeCards = exchangeCards;
    }
}
