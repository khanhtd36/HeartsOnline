package model;

import model.card.Card;
import model.card.CardName;
import model.card.CardType;
import model.player.Player;
import model.player.Position;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HeartGame implements Serializable {
    private Position myPosition = Position.SOUTH;

    private Position positionToGo = Position.SOUTH;
    private int hand = 0;
    private int trick = 0;
    private boolean heartBroken = false;
    private GameState gameState = GameState.NEW;

    private ArrayList<Player> players = new ArrayList<>();

    public HeartGame() {
        players.clear();
        players.add(new Player(Position.SOUTH, "Thánh Bài"));
        players.add(new Player(Position.WEST));
        players.add(new Player(Position.NORTH));
        players.add(new Player(Position.EAST));
    }


    public void init() {
        gameState = GameState.NEW;
        hand = 0;
        trick = 0;
    }

    public void resetAll() {
        gameState = GameState.NEW;
        heartBroken = false;
        hand = 0;
        trick = 0;

        players.get(0).resetAllExceptPersonalInfo();
        players.get(1).resetAll();
        players.get(2).resetAll();
        players.get(3).resetAll();
    }

    public void resetHand() {
        trick = 0;

        players.get(0).resetHand();
        players.get(1).resetHand();
        players.get(2).resetHand();
        players.get(3).resetHand();

    }

    public void generateCard() {
        players.get(0).clearCardDesks();
        players.get(1).clearCardDesks();
        players.get(2).clearCardDesks();
        players.get(3).clearCardDesks();

        List<Integer> indexList = new ArrayList<>(52);
        for (int i = 0; i < 52; i++) {
            indexList.add(i);
        }
        for (int i = 0; i < 51; i++) {
            int indexToExchange = (int) (Math.random() * (51 - i) + i + 1);
            int tmp = indexList.get(i);
            indexList.set(i, indexList.get(indexToExchange));
            indexList.set(indexToExchange, tmp);
        }

        for (int i = 0; i < 13; i++) {
            int indexOfCardName = indexList.get(i);
            CardName cardName = CardName.values()[indexOfCardName];
            Card card = new Card(cardName);
            getCardDesk(Position.SOUTH).add(card);
        }
        for (int i = 13; i < 26; i++) {
            int indexOfCardName = indexList.get(i);
            CardName cardName = CardName.values()[indexOfCardName];
            Card card = new Card(cardName);
            getCardDesk(Position.WEST).add(card);
        }
        for (int i = 26; i < 39; i++) {
            int indexOfCardName = indexList.get(i);
            CardName cardName = CardName.values()[indexOfCardName];
            Card card = new Card(cardName);
            getCardDesk(Position.NORTH).add(card);
        }
        for (int i = 39; i < 52; i++) {
            int indexOfCardName = indexList.get(i);
            CardName cardName = CardName.values()[indexOfCardName];
            Card card = new Card(cardName);
            getCardDesk(Position.EAST).add(card);
        }

        for (Player player : players) {
            player.sortCards();
        }

    }

    public Position destPositionOfExchange(Position sourcePosition) {
        int dP = hand;
        dP %= 4;

        switch (dP) {
            case 0:
                dP = sourcePosition.getOrder() + 1;
                if (dP >= 4) dP -= 4;
                break;

            case 1:
                dP = sourcePosition.getOrder() + 3;
                if (dP >= 4) dP -= 4;
                break;

            case 2:
                dP = sourcePosition.getOrder() + 2;
                if (dP >= 4) dP -= 4;
                break;

            case 3:
                dP = sourcePosition.getOrder();
                break;
        }

        return Position.values()[dP];
    }

    public List<Card> getCardDesk(Position position) {
        return getPlayer(position).getCards();
    }

    public void setCardDesk(List<Card> cards, Position position) {
        getPlayer(position).setCards(cards);
    }

    public Card getTrickCard(Position position) {
        return getPlayer(position).getTrickCard();
    }

    public void setTrickCard(Position position, Card card) {
        getPlayer(position).setTrickCard(card);
    }

    public List<Card> getExchangeCards(Position position) {
        return getPlayer(position).getExchangeCards();
    }

    public void setExchangeCards(Position position, List<Card> cards) {
        getPlayer(position).setExchangeCards(cards);
    }

    public void next() {
        if (isOver()) {
            gameState = GameState.SHOWING_RESULT;
            return;
        }

        increaseTrick();
        if (trick == 0) increaseHand();

        if ((trick % 4) == 0) {
            setTurn();
        }


    }

    private void setTurn() {
        if (trick == 0) {
            for (Player player : players) {
                if (player.getCards().get(0).getCardName().equals(CardName.TWO_OF_CLUBS)) {
                    positionToGo = player.getPosition();
                    break;
                }
            }
        } else {
            CardType cardTypeOfTrick = getPlayer(positionToGo).getTrickCard().getCardType();
            Position positionToGoNext = positionToGo;

            for (Player player : players) {
                if (player.getTrickCard().getCardType().equals(cardTypeOfTrick)) {
                    if (player.getTrickCard().getCardTypeOrder() > getPlayer(positionToGoNext).getTrickCard().getCardTypeOrder()) {
                        positionToGoNext = player.getPosition();
                    }
                }
            }

            positionToGo = positionToGoNext;
        }
    }

    private boolean isOver() {
        boolean result = false;

        for(Player player : players) {
            if (player.getAccumulatedPoint() >= 100) {
                result = true;
                break;
            }
        }

        return result;
    }


    //Getter và setter ----------------------------------------------

    public void setPlayerName(Position position, String name) {
        for (Player player : players) {
            if (player.getPosition().equals(position)) {
                player.setName(name);
                break;
            }
        }
    }

    public Position getMyPosition() {
        return myPosition;
    }

    public void setMyPosition(Position myPosition) {
        this.myPosition = myPosition;
    }

    public String getName(Position position) {
        return getPlayer(position).getName();
    }

    public synchronized Position getAvailablePosition() {
        Position availablePosition = null;
        for (Player player : players) {
            if (player.isBot()) {
                availablePosition = player.getPosition();
                break;
            }
        }
        return availablePosition;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Player getPlayer(Position position) {
        for (Player player : players) {
            if (player.getPosition().equals(position)) {
                return player;
            }
        }
        return null;
    }

    public int getHand() {
        return hand;
    }

    public void setHand(int hand) {
        this.hand = hand;
    }

    public void increaseHand() {
        hand++;
    }

    public int getTrick() {
        return trick;
    }

    public void setTrick(int trick) {
        this.trick = trick;
    }

    public void increaseTrick() {
        trick++;
        if (trick >= 13) trick = 0;
    }

    public boolean isHeartBroken() {
        return heartBroken;
    }

    public void setHeartBroken(boolean heartBroken) {
        this.heartBroken = heartBroken;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Position getPositionToGo() {
        return positionToGo;
    }

    //End Getter và setter ------------------------------------------
}
