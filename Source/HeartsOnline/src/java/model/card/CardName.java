package model.card;

public enum CardName {
    TWO_OF_SPADES(CardType.SPADES, 0, 0, "two-of-spades"),
    TWO_OF_CLUBS(CardType.CLUBS, 0, 0, "two-of-clubs"),
    TWO_OF_DIAMONDS(CardType.DIAMONDS, 0, 0, "two-of-diamonds"),
    TWO_OF_HEARTS(CardType.HEARTS, 0, 1, "two-of-hearts"),

    THREE_OF_SPADES(CardType.SPADES, 1, 0, "three-of-spades"),
    THREE_OF_CLUBS(CardType.CLUBS, 1, 0, "three-of-clubs"),
    THREE_OF_DIAMONDS(CardType.DIAMONDS, 1, 0, "three-of-diamonds"),
    THREE_OF_HEARTS(CardType.HEARTS, 1, 1, "three-of-hearts"),

    FOUR_OF_SPADES(CardType.SPADES, 2, 0, "four-of-spades"),
    FOUR_OF_CLUBS(CardType.CLUBS, 2, 0, "four-of-clubs"),
    FOUR_OF_DIAMONDS(CardType.DIAMONDS, 2, 0, "four-of-diamonds"),
    FOUR_OF_HEARTS(CardType.HEARTS, 2, 1, "four-of-hearts"),

    FIVE_OF_SPADES(CardType.SPADES, 3, 0, "five-of-spades"),
    FIVE_OF_CLUBS(CardType.CLUBS, 3, 0, "five-of-clubs"),
    FIVE_OF_DIAMONDS(CardType.DIAMONDS, 3, 0, "five-of-diamonds"),
    FIVE_OF_HEARTS(CardType.HEARTS, 3, 1, "five-of-hearts"),

    SIX_OF_SPADES(CardType.SPADES, 4, 0, "six-of-spades"),
    SIX_OF_CLUBS(CardType.CLUBS, 4, 0, "six-of-clubs"),
    SIX_OF_DIAMONDS(CardType.DIAMONDS, 4, 0, "six-of-diamonds"),
    SIX_OF_HEARTS(CardType.HEARTS, 4, 1, "six-of-hearts"),

    SEVEN_OF_SPADES(CardType.SPADES, 5, 0, "seven-of-spades"),
    SEVEN_OF_CLUBS(CardType.CLUBS, 5, 0, "seven-of-clubs"),
    SEVEN_OF_DIAMONDS(CardType.DIAMONDS, 5, 0, "seven-of-diamonds"),
    SEVEN_OF_HEARTS(CardType.HEARTS, 5, 1, "seven-of-hearts"),

    EIGHT_OF_SPADES(CardType.SPADES, 6, 0, "eight-of-spades"),
    EIGHT_OF_CLUBS(CardType.CLUBS, 6, 0, "eight-of-clubs"),
    EIGHT_OF_DIAMONDS(CardType.DIAMONDS, 6, 0, "eight-of-diamonds"),
    EIGHT_OF_HEARTS(CardType.HEARTS, 6, 1, "eight-of-hearts"),

    NINE_OF_SPADES(CardType.SPADES, 7, 0, "nine-of-spades"),
    NINE_OF_CLUBS(CardType.CLUBS, 7, 0, "nine-of-clubs"),
    NINE_OF_DIAMONDS(CardType.DIAMONDS, 7, 0, "nine-of-diamonds"),
    NINE_OF_HEARTS(CardType.HEARTS, 7, 1, "nine-of-hearts"),

    TEN_OF_SPADES(CardType.SPADES, 8, 0, "ten-of-spades"),
    TEN_OF_CLUBS(CardType.CLUBS, 8, 0, "ten-of-clubs"),
    TEN_OF_DIAMONDS(CardType.DIAMONDS, 8, 0, "ten-of-diamonds"),
    TEN_OF_HEARTS(CardType.HEARTS, 8, 1, "ten-of-hearts"),

    JACK_OF_SPADES(CardType.SPADES, 9, 0, "jack-of-spades"),
    JACK_OF_CLUBS(CardType.CLUBS, 9, 0, "jack-of-clubs"),
    JACK_OF_DIAMONDS(CardType.DIAMONDS, 9, 0, "jack-of-diamonds"),
    JACK_OF_HEARTS(CardType.HEARTS, 9, 1, "jack-of-hearts"),

    QUEEN_OF_SPADES(CardType.SPADES, 10, 13, "queen-of-spades"),
    QUEEN_OF_CLUBS(CardType.CLUBS, 10, 0, "queen-of-clubs"),
    QUEEN_OF_DIAMONDS(CardType.DIAMONDS, 10, 0, "queen-of-diamonds"),
    QUEEN_OF_HEARTS(CardType.HEARTS, 10, 1, "queen-of-hearts"),

    KING_OF_SPADES(CardType.SPADES, 11, 0, "king-of-spades"),
    KING_OF_CLUBS(CardType.CLUBS, 11, 0, "king-of-clubs"),
    KING_OF_DIAMONDS(CardType.DIAMONDS, 11, 0, "king-of-diamonds"),
    KING_OF_HEARTS(CardType.HEARTS, 11, 1, "king-of-hearts"),

    ACE_OF_SPADES(CardType.SPADES, 12, 0, "ace-of-spades"),
    ACE_OF_CLUBS(CardType.CLUBS, 12, 0, "ace-of-clubs"),
    ACE_OF_DIAMONDS(CardType.DIAMONDS, 12, 0, "ace-of-diamonds"),
    ACE_OF_HEARTS(CardType.HEARTS, 12, 1, "ace-of-hearts");

    private int value;
    private int point;
    private String cssClassName;
    private CardType cardType;

    CardName(CardType cardType, int value, int point, String cssClassName) {
        this.value = value;
        this.point = point;
        this.cardType = cardType;
        this.cssClassName = cssClassName;
    }

    public int getValue() {
        return value;
    }

    public int getPoint() {
        return point;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public CardType getCardType() {
        return cardType;
    }
}
