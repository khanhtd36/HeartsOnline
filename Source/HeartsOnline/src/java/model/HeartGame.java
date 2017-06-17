package model;

import model.card.Card;
import model.card.CardName;
import model.player.Player;
import model.player.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeartGame implements Serializable {
    private Position myPosition = Position.SOUTH;

    private int hand = 0;
    private int trick = 0;

    private ArrayList<Player> players = new ArrayList<>();
    private Map<Position, List<Card>> cardDesks = new HashMap<>();
    private Map<Position, List<Card>> eatenCards = new HashMap<>();
    private Map<Position, List<Card>> exchangeCards = new HashMap<>();
    private Map<Position, Card> trickCards = new HashMap<>();

    private Map<Position, Integer> accumulatedPoints = new HashMap<>();

    public HeartGame() {
        players.clear();
        players.add(new Player(Position.SOUTH, "Thánh Bài"));
        players.add(new Player(Position.WEST));
        players.add(new Player(Position.NORTH));
        players.add(new Player(Position.EAST));
    }

    public void init() {
        cardDesks.put(Position.SOUTH, new ArrayList<>());
        cardDesks.put(Position.WEST, new ArrayList<>());
        cardDesks.put(Position.NORTH, new ArrayList<>());
        cardDesks.put(Position.EAST, new ArrayList<>());

        eatenCards.put(Position.SOUTH, new ArrayList<>());
        eatenCards.put(Position.WEST, new ArrayList<>());
        eatenCards.put(Position.NORTH, new ArrayList<>());
        eatenCards.put(Position.EAST, new ArrayList<>());

        exchangeCards.put(Position.SOUTH, new ArrayList<>());
        exchangeCards.put(Position.WEST, new ArrayList<>());
        exchangeCards.put(Position.NORTH, new ArrayList<>());
        exchangeCards.put(Position.EAST, new ArrayList<>());

        trickCards.put(Position.SOUTH, new Card(CardName.UNKNOWN));
        trickCards.put(Position.WEST, new Card(CardName.UNKNOWN));
        trickCards.put(Position.NORTH, new Card(CardName.UNKNOWN));
        trickCards.put(Position.EAST, new Card(CardName.UNKNOWN));

        accumulatedPoints.put(Position.SOUTH, 0);
        accumulatedPoints.put(Position.WEST, 0);
        accumulatedPoints.put(Position.NORTH, 0);
        accumulatedPoints.put(Position.EAST, 0);
    }

    public void resetAll() {
        cardDesks.clear();
        eatenCards.clear();
        exchangeCards.clear();
        trickCards.clear();
    }

    public void resetHand() {

    }

    public void generateCard() {
        cardDesks.get(Position.SOUTH).clear();
        cardDesks.get(Position.WEST).clear();
        cardDesks.get(Position.NORTH).clear();
        cardDesks.get(Position.EAST).clear();

        List<Integer> indexList = new ArrayList<>(52);
        for(int i = 0; i < 52; i++) {
            indexList.add(i);
        }
        for(int i = 0; i < 51; i++) {
            int indexToExchange = (int)(Math.random()*(51 - i) + i + 1);
            int tmp = indexList.get(i);
            indexList.set(i, indexList.get(indexToExchange));
            indexList.set(indexToExchange, tmp);
        }

        for(int i = 0; i < 13; i++) {
            int indexOfCardName = indexList.get(i);
            CardName cardName = CardName.values()[indexOfCardName];
            Card card = new Card(cardName);
            cardDesks.get(Position.SOUTH).add(card);
        }
        for(int i = 13; i < 26; i++) {
            int indexOfCardName = indexList.get(i);
            CardName cardName = CardName.values()[indexOfCardName];
            Card card = new Card(cardName);
            cardDesks.get(Position.WEST).add(card);
        }
        for(int i = 26; i < 39; i++) {
            int indexOfCardName = indexList.get(i);
            CardName cardName = CardName.values()[indexOfCardName];
            Card card = new Card(cardName);
            cardDesks.get(Position.NORTH).add(card);
        }
        for(int i = 39; i < 52; i++) {
            int indexOfCardName = indexList.get(i);
            CardName cardName = CardName.values()[indexOfCardName];
            Card card = new Card(cardName);
            cardDesks.get(Position.EAST).add(card);
        }

        players.get(0).setCards(cardDesks.get(Position.SOUTH));
        players.get(1).setCards(cardDesks.get(Position.WEST));
        players.get(2).setCards(cardDesks.get(Position.NORTH));
        players.get(3).setCards(cardDesks.get(Position.EAST));

        for (Player player : players) {
            player.sortCards();
        }

    }

    public List<Card> getCardDeskOf(Position position) {
        return cardDesks.get(position);
    }

    public void setPlayerName(Position position, String name) {
        for(Player player : players) {
            if(player.getPosition().equals(position)) {
                player.setName(name);
                break;
            }
        }
    }

    public Position getMyPosition() {
        return myPosition;
    }

    public String getName(Position position) {
        return "Thánh bài";
    }

    public synchronized Position getAvailablePosition() {
        Position availablePosition = null;
        for(Player player : players) {
            if(player.isBot()) {
                availablePosition = player.getPosition();
                break;
            }
        }
        return availablePosition;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(Position position) {
        for(Player player : players) {
            if(player.getPosition().equals(position)) {
                return player;
            }
        }
        return null;
    }

    public void setMyPosition(Position myPosition) {
        this.myPosition = myPosition;
    }
}
