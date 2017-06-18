package model.player;

import model.card.Card;
import model.card.CardName;
import model.card.CardType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    public String name = "BOT";
    private Position position;
    private int accumulatedPoint = 0;
    private boolean bot = false;
    public int curHandPoint = 0;

    List<Card> cards = new ArrayList<>();
    List<Card> eatenCards = new ArrayList<>();
    Card trickCard = new Card(CardName.UNKNOWN);
    List<Card> exchangeCards = new ArrayList<>();

    public Player(){
        bot = true;
    }

    public Player(Position position) {
        this.position = position;
        this.bot = true;
    }

    public Player(Position position, String name) {
        this.position = position;
        this.name = name;
        this.bot = false;
    }

    public Player(Player oldPlayer, boolean bot) {
        this.position = oldPlayer.getPosition();
        this.accumulatedPoint = oldPlayer.getAccumulatedPoint();
        this.bot = bot;
    }

    public void sortCards() {
        cards.sort((card1, card2)->{
            if(card1.getCardTypeOrder() == card2.getCardTypeOrder()) {
                return Integer.compare(card1.getValue(), card2.getValue());
            }
            else {
                return Integer.compare(card1.getCardTypeOrder(), card2.getCardTypeOrder());
            }
        });
    }

    public void eatCards(Card... cards) {
        for(Card card : cards) {
            if(card.getPoint() > 0)
                eatenCards.add(card);
        }
    }

    public Card autoPlayACard(int trickNum, CardType trickCardType, Card... cardsOnBoard) {
        //TODO: code máy chơi
        return cards.get(0);
    }

    public void playACard(Card card) {
        cards.remove(card);
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
        cards.clear();
        eatenCards.clear();
        trickCard = new Card(CardName.UNKNOWN);
        exchangeCards.clear();
    }

    public void resetAllExceptPersonalInfo() {
        accumulatedPoint = 0;
        curHandPoint = 0;
        cards.clear();
        eatenCards.clear();
        trickCard = new Card(CardName.UNKNOWN);
        exchangeCards.clear();
    }

    public void resetHand() {
        curHandPoint = 0;
        cards.clear();
        eatenCards.clear();
        trickCard = new Card(CardName.UNKNOWN);
        exchangeCards.clear();
    }

    public void clearCardDesks() {
        cards.clear();
    }

    public void removeACardInCardDesk(Card card) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCardName().equals(card.getCardName())) {
                cards.remove(i);
                break;
            }
        }
    }

    public void removeACardInEatenCards(Card card) {
        for (int i = 0; i < eatenCards.size(); i++) {
            if (eatenCards.get(i).getCardName().equals(card.getCardName())) {
                eatenCards.remove(i);
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

    //Getter và setter -----------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasCardType(CardType type) {
        for(Card card : cards) {
            if(card.getCardType().equals(type))
                return true;
        }
        return false;
    }

    public List<Card> getEatenCards() {
        return eatenCards;
    }

    public void setPosition(Position pos) {
        this.position = pos;
    }

    public Position getPosition() {
        return position;
    }

    public int getCurrentHandPoint() {
        int result = 0;
        for(Card card : eatenCards) {
            result += card.getPoint();
        }
        return result;
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

    public void setAccumulatedPoint(int accumulatedPoint) {
        this.accumulatedPoint = accumulatedPoint;
    }

    public int getCurHandPoint() {
        return curHandPoint;
    }

    public void setCurHandPoint(int curHandPoint) {
        this.curHandPoint = curHandPoint;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void setEatenCards(List<Card> eatenCards) {
        this.eatenCards = eatenCards;
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
