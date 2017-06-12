package model;

import model.card.CardName;
import model.player.Position;

import java.util.*;

public class HeartGame {
    private boolean started = false;
    private int hand = 0;
    private int trick = 0;

    private Map<Position, List<CardName>> cardDesks = new HashMap<>();
    private Map<Position, List<CardName>> eatenCards = new HashMap<>();
    private Map<Position, List<CardName>> exchangeCards = new HashMap<>();
    private Map<Position, CardName> trickCards = new HashMap<>();

    private Map<Position, Integer> accumulatedPoints = new HashMap<>();

    public HeartGame() {
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

        trickCards.put(Position.SOUTH, CardName.UNKNOWN);
        trickCards.put(Position.WEST, CardName.UNKNOWN);
        trickCards.put(Position.NORTH, CardName.UNKNOWN);
        trickCards.put(Position.EAST, CardName.UNKNOWN);

        accumulatedPoints.put(Position.SOUTH, 0);
        accumulatedPoints.put(Position.WEST, 0);
        accumulatedPoints.put(Position.NORTH, 0);
        accumulatedPoints.put(Position.EAST, 0);
    }

    public void reset() {

    }

    public void generateCard() {
        List<Integer> indexList = new ArrayList<>(52);
        for(int i = 0; i < 52; i++) {
            indexList.set(i, i);
        }
        for(int i = 0; i < 51; i++) {
            int indexToExchange = (int)(Math.random()*(51 - i) + i + 1);
            int tmp = indexList.get(i);
            indexList.set(i, indexList.get(indexToExchange));
            indexList.set(indexToExchange, tmp);
        }


    }

    public List<CardName> getCardDeskOf(Position position) {
        return cardDesks.get(position);
    }

}
