package model.card;

public class Card {
    CardName cardName;

    public Card(CardName cardName) {
        this.cardName = cardName;
    }

    public void setCardName(CardName cardName) {
        this.cardName = cardName;
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
