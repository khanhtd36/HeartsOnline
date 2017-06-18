package model.card;

import java.io.Serializable;

public class Card implements Serializable {
    CardName cardName = CardName.UNKNOWN;

    public Card(CardName cardName) {
        this.cardName = cardName;
    }

    public Card(String styleClass) {
        for(CardName cardName1 : CardName.values()) {
            if(cardName1.getCssClassName().equals(styleClass)) {
                cardName = cardName1;
                break;
            }
        }
    }

    public void setCardName(CardName cardName) {
        this.cardName = cardName;
    }

    public CardName getCardName() {
        return cardName;
    }

    public int getValue() {
        return cardName.getValue();
    }

    public int getPoint() {
        return cardName.getPoint();
    }

    public String getCssClassName() {
        return cardName.getCssClassName();
    }

    public CardType getCardType() {
        return cardName.getCardType();
    }

    public int getCardTypeOrder() { return cardName.getCardType().getOrder();}
}
