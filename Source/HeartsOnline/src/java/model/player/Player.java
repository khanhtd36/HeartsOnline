package model.player;

import model.card.Card;
import model.card.CardName;
import model.card.CardType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    List<Card> cardDesk = new ArrayList<>();
    List<Card> eatenCards = new ArrayList<>();
    Card trickCard = new Card(CardName.UNKNOWN);
    List<Card> exchangeCards = new ArrayList<>();

    private String name = "BOT";
    private int curHandPoint = 0;
    private Position position;
    private int accumulatedPoint = 0;
    private boolean bot = false;

    public Player(Position position) {
        this.position = position;
        this.bot = true;
    }

    public Player(Position position, String name) {
        this.position = position;
        this.name = name;
        this.bot = false;
    }

    public void sortCards() {
        cardDesk.sort((card1, card2) -> {
            if (card1.getCardTypeOrder() == card2.getCardTypeOrder()) {
                return Integer.compare(card1.getValue(), card2.getValue());
            } else {
                return Integer.compare(card1.getCardTypeOrder(), card2.getCardTypeOrder());
            }
        });
    }

    public void eatCards(Card... cards) {
        for (Card card : cards) {
            if (card.getPoint() > 0) {
                eatenCards.add(card);
                curHandPoint += card.getPoint();
            }
        }
    }

    public Card autoPlayACard(int trickNum, CardType trickCardType, Card... cardsOnBoard) {
        //TODO: code máy chơi
        Card cardToPlay = new Card(CardName.UNKNOWN);

        boolean amILeadTrick = trickCardType == null;
        if (amILeadTrick) {
            //Dẫn đầu lượt đầu tiên
            if (trickNum == 0) {
                cardToPlay = cardDesk.get(0);
            }
            //Dẫn đầu lượt khác
            else {
                cardToPlay = cardDesk.get(0);
                for (Card card : cardDesk) {
                    if (card.getValue() < cardToPlay.getValue() && !card.getCardType().equals(CardType.HEARTS)) {
                        cardToPlay = card;
                    }
                }
            }
        } else {
            //Theo lượt đầu tiên
            if (trickNum == 0) {
                //Nếu có chuồn, đi chuồn cao nhất
                if (hasCardType(trickCardType)) {
                    for (Card card : cardDesk) {
                        if (card.getCardType().equals(trickCardType)) {
                            cardToPlay = card;
                        }
                    }
                } else {
                    //Nếu không có chuồn thì đi con cao nhất cho phép
                    if(allAreHearts()) cardToPlay = cardDesk.get(cardDesk.size() - 1);
                    else {
                        cardToPlay = cardDesk.get(0);
                        for(Card card: cardDesk) {
                            if (card.getValue() > cardToPlay.getValue() && card.getPoint() == 0) {
                                cardToPlay = card;
                            }
                        }

                    }
                }
            }
            //Theo lượt khác
            else {
                Card highestSameTypeCardOnBoard = cardsOnBoard[0];
                for (Card cardOnBoard : cardsOnBoard) {
                    if (cardOnBoard.getCardType().equals(trickCardType)) {
                        if (!highestSameTypeCardOnBoard.getCardType().equals(trickCardType)
                                || cardOnBoard.getValue() > highestSameTypeCardOnBoard.getValue()) {
                            highestSameTypeCardOnBoard = cardOnBoard;
                        }
                    }
                }

            }
        }


        playACard(cardToPlay);
        return cardToPlay;
    }

    public void autoExchangeCards() {
        List<Card> cardsToExchange = new ArrayList<>();
        int index1, index2, index3;
        index1 = index2 = index3 = 0;

        for (int i = 0; i < cardDesk.size(); i++) {
            if (cardDesk.get(i).getValue() > index1) {
                index3 = index2;
                index2 = index1;
                index1 = i;
            } else if (cardDesk.get(i).getValue() > index2) {
                index3 = index2;
                index2 = i;
            } else if (cardDesk.get(i).getValue() > index3) {
                index3 = i;
            } else if (index2 == index1) {
                index2 = i;
            } else if (index3 == index1) {
                index3 = i;
            }
        }

        cardsToExchange.add(cardDesk.get(index1));
        cardsToExchange.add(cardDesk.get(index2));
        cardsToExchange.add(cardDesk.get(index3));

        exchangeCards = cardsToExchange;
    }

    public void playACard(Card card) {
        removeACardInCardDesk(card);
        trickCard = card;
    }

    public void changeToBot() {
        name = "BOT";
        bot = true;
    }

    public void resetAll() {
        name = "BOT";
        bot = true;
        accumulatedPoint = 0;
        curHandPoint = 0;
        cardDesk.clear();
        eatenCards.clear();
        trickCard = new Card(CardName.UNKNOWN);
        exchangeCards.clear();
    }

    public void resetAllExceptPersonalInfo() {
        accumulatedPoint = 0;
        curHandPoint = 0;
        cardDesk.clear();
        eatenCards.clear();
        trickCard = new Card(CardName.UNKNOWN);
        exchangeCards.clear();
    }

    public void resetHand() {
        curHandPoint = 0;
        cardDesk.clear();
        eatenCards.clear();
        trickCard = new Card(CardName.UNKNOWN);
        exchangeCards.clear();
    }

    public void clearCardDesks() {
        cardDesk.clear();
    }

    public void removeACardInCardDesk(Card card) {
        for (int i = 0; i < cardDesk.size(); i++) {
            if (cardDesk.get(i).getCardName().equals(card.getCardName())) {
                cardDesk.remove(i);
                break;
            }
        }
    }

    public void removeACardInExchangeCards(Card card) {
        for (int i = 0; i < exchangeCards.size(); i++) {
            if (exchangeCards.get(i).getCardName().equals(card.getCardName())) {
                exchangeCards.remove(i);
                break;
            }
        }
    }

    public void receiveExchangeCards(List<Card> receivedCards) {
        for (Card card : receivedCards) {
            cardDesk.add(card);
        }
        sortCards();
    }

    public void calcCurHandPoint() {
        if (doesShootTheMoon()) {
            curHandPoint = 0;
            return;
        }

        curHandPoint = 0;
        for (Card eatenCard : eatenCards) {
            curHandPoint += eatenCard.getPoint();
        }
    }

    public void calcAccumulatedPoint() {
        if (doesShootTheMoon()) {
            curHandPoint = 0;
        }

        accumulatedPoint += curHandPoint;
    }

    public boolean doesShootTheMoon() {
        if(eatenCards.size() != 13) return false;
        for (Card card : eatenCards) {
            if (card.getCardName().equals(CardName.QUEEN_OF_SPADES))
                return true;
        }

        return false;
    }

    public boolean allAreHearts() {
        for (Card card : cardDesk) {
            if (!card.getCardType().equals(CardType.HEARTS))
                return false;
        }
        return true;
    }

    //Getter và setter -----------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasCardType(CardType type) {
        for (Card card : cardDesk) {
            if (card.getCardType().equals(type))
                return true;
        }
        return false;
    }

    public List<Card> getEatenCards() {
        return eatenCards;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position pos) {
        this.position = pos;
    }

    public int getAccumulatedPoint() {
        return accumulatedPoint;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public int getCurHandPoint() {
        return curHandPoint;
    }

    public void setCurHandPoint(int curHandPoint) {
        this.curHandPoint = curHandPoint;
    }

    public List<Card> getCardDesk() {
        return cardDesk;
    }

    public void setCardDesk(List<Card> cardDesk) {
        this.cardDesk = cardDesk;
    }

    public Card getTrickCard() {
        return trickCard;
    }

    public void setTrickCard(Card trickCard) {
        this.trickCard = trickCard;
    }

    public List<Card> getExchangeCards() {
        return exchangeCards;
    }

    public void setExchangeCards(List<Card> exchangeCards) {
        this.exchangeCards = exchangeCards;
    }

    //End Getter và setter -------------------------------------------
}
