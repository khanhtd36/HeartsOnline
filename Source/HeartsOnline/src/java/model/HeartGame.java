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

    private Position startPositionOfTrick = Position.SOUTH;
    private Position positionToGo = Position.SOUTH;
    private int hand = 0;
    private int trick = 0;
    private boolean heartBroken = false;
    private GameState gameState = GameState.NEW;
    private GameModelCallback callback = null;

    private ArrayList<Player> players = new ArrayList<>();

    public HeartGame(GameModelCallback callback) {
        this.callback = callback;

        players.clear();
        players.add(new Player(Position.SOUTH, "Thánh Bài"));
        players.add(new Player(Position.WEST));
        players.add(new Player(Position.NORTH));
        players.add(new Player(Position.EAST));
    }


    public void init() {
        gameState = GameState.NEW;
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

    public void resetAllExceptPersonalInfo() {
        gameState = GameState.NEW;
        heartBroken = false;
        hand = 0;
        trick = 0;

        players.get(0).resetAllExceptPersonalInfo();
        players.get(1).resetAllExceptPersonalInfo();
        players.get(2).resetAllExceptPersonalInfo();
        players.get(3).resetAllExceptPersonalInfo();
    }

    public void resetHand() {
        gameState = GameState.WAITING;
        trick = 0;
        heartBroken = false;

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

    public boolean trickDone() {
        return positionToGo.next().equals(startPositionOfTrick);
    }

    public boolean handDone() {
        return (trick == 12) && trickDone();
    }

    public void calcTurn(boolean startHand) {
        if (startHand) {
            for (Player player : players) {
                if (player.getCardDesk().get(0).getCardName().equals(CardName.TWO_OF_CLUBS)) {
                    startPositionOfTrick = player.getPosition();
                    positionToGo = player.getPosition();
                    return;
                }
            }
        } else {
            positionToGo = positionToGo.next();
        }
    }

    public boolean isOver() {
        boolean result = false;

        for (Player player : players) {
            if (player.getAccumulatedPoint() >= 100) {
                result = true;
                break;
            }
        }

        return result;
    }


    public CardType getCardTypeOfTrick() {
        return getPlayer(startPositionOfTrick).getTrickCard().getCardType();
    }

    public List<Card> getCardsOnBoard() {
        List<Card> onBoardCards = new ArrayList<>();

        for (Player player : players) {
            onBoardCards.add(player.getTrickCard());
        }

        return onBoardCards;
    }


    public void selectACard(Card card) {
        if (getPlayer(myPosition).getExchangeCards().size() < 3) {
            getPlayer(myPosition).getExchangeCards().add(card);
        }
    }

    public void unselectACard(Card card) {
        getPlayer(myPosition).removeACardInExchangeCards(card);
    }

    public void exchangeCards(Position srcPos, Position destPos) {
        for (Card cardToExchange : getPlayer(srcPos).getExchangeCards()) {
            getPlayer(srcPos).removeACardInCardDesk(cardToExchange);
            getPlayer(destPos).getCardDesk().add(cardToExchange);
        }
        getPlayer(destPos).sortCards();
    }

    public void receiveExchangeCards(List<Card> receivedCards) {
        getPlayer(myPosition).receiveExchangeCards(receivedCards);
    }

    public void playACard(Position position, Card card) {
        getPlayer(position).playACard(card);
    }

    public void eatCards(Position positionToEat) {
        List<Card> cardsToEat = new ArrayList<>();
        for (Player player : players) {
            if (player.getTrickCard().getPoint() > 0) {
                cardsToEat.add(player.getTrickCard());
            }

        }

        for (Card cardToEat : cardsToEat) {
            getPlayer(positionToEat).eatCards(cardToEat);
        }

        for (Player player : players) {
            player.setTrickCard(new Card(CardName.UNKNOWN));
        }

        if (callback != null && positionToEat.equals(myPosition) && cardsToEat.size() > 0) {
            Thread thread = new Thread(() -> callback.onMyCurHandPointChanged());
            thread.start();
        }
    }

    public Position positionToEat() {
        Position positionToEat = startPositionOfTrick;

        CardType cardTypeOfTrick = getCardTypeOfTrick();
        for (Player player : players) {
            if (player.getTrickCard().getCardType().equals(cardTypeOfTrick) && player.getTrickCard().getValue() > getPlayer(positionToEat).getTrickCard().getValue()) {
                positionToEat = player.getPosition();
            }
        }

        return positionToEat;
    }

    public void calcResultPoints() {
        Player playerShotTheMoon = null;
        for (Player player : players) {
            player.calcCurHandPoint();
            if (player.doesShootTheMoon()) {
                playerShotTheMoon = player;
                break;
            }
        }

        if (playerShotTheMoon != null) {
            for (Player player : players) {
                if (!player.equals(playerShotTheMoon)) {
                    player.setCurHandPoint(26);
                }
            }
        }

        for (Player player : players) {
            player.calcAccumulatedPoint();
        }
    }

    public Player winner() {
        Player winner = players.get(0);
        for (Player player : players) {
            if (player.getAccumulatedPoint() < winner.getAccumulatedPoint()) {
                winner = player;
            }
        }

        return winner;
    }

    public boolean canIPlay(Position position, Card card) {
        CardType cardTypeOfTrick = getCardTypeOfTrick();

        //Ở trick đầu tiên, không được đi quân có điểm
        if (trick == 0) {
            if (card.getCardName().equals(CardName.QUEEN_OF_SPADES)) return false;
        }

        //Bài giống loại, duyệt
        if (card.getCardType().equals(cardTypeOfTrick)) return true;
        else {
            boolean hasCardTypeOfTrick = getPlayer(position).hasCardType(cardTypeOfTrick);

            //Có bài giống loại mà không chịu đi, biến
            if (hasCardTypeOfTrick) return false;

            //Không có bài giống loại, mà đi lá bài không phải Loại Cơ, duyệt
            if (!card.getCardType().equals(CardType.HEARTS)) return true;

            //Không có bài giống loại, đi lá bài loại cơ, thì tim phải tan vỡ rồi mới được đi
            detectHeartBroken(position, card);
            return isHeartBroken();
        }
    }

    public boolean exchangeDone() {
        boolean result = true;

        for (Player player : players) {
            if (player.getExchangeCards().size() < 3) {
                return false;
            }
        }

        return result;
    }

    //Linh hồn của gameModel
    public void next(boolean host, boolean doneTrickLately) {
        //Dành cho trong ván, mỗi khi có 1 thằng nào đó đi 1 quân bài thì gọi next
        //Nếu vừa đi xong 1 lá bài, mà vừa xong ván luôn, thì k tính toán gì, chỉ báo lại cho controller
        if (handDone()) {
            Thread thread = new Thread(() -> {
                callback.onHandDone();
            });
            thread.start();
            return;
        }

        if (trickDone()) {
            Thread thread = new Thread(() -> {
                callback.onTrickDone(positionToEat());
            });
            thread.start();
            return;
        }

        if (!doneTrickLately) {
            calcTurn(false);
        }
        setGameState(GameState.PLAYING);
        if (host) {
            detectBotGo();
        }
    }

    public void startTurn() {
        calcTurn(true);
        setGameState(GameState.PLAYING);
        detectBotGo();
    }

    private void detectBotGo() {
        if (isThisTurnForBot()) {
            List<Card> cardsOnBoard = getCardsOnBoard();
            Card card = getPlayer(positionToGo).autoPlayACard(trick, getCardTypeOfTrick(), cardsOnBoard);
            detectHeartBroken(positionToGo, card);
            Thread thread = new Thread(() -> callback.onABotPlayedACard(positionToGo, card));
            thread.start();
        }
    }

    public boolean isThisTurnForBot() {
        return getPlayer(positionToGo).isBot();
    }

    public boolean amILeadTrick(Position position) {
        return position.equals(startPositionOfTrick);
    }

    //Getter và setter ----------------------------------------------

    public List<Card> getCardDesk(Position position) {
        return getPlayer(position).getCardDesk();
    }

    public void setCardDesk(List<Card> cards, Position position) {
        getPlayer(position).setCardDesk(cards);
    }

    public List<Card> getExchangeCards(Position position) {
        return getPlayer(position).getExchangeCards();
    }

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

    public void increaseHand() {
        hand++;
    }

    public int getTrick() {
        return trick;
    }

    public void increaseTrick() {
        trick++;
        if (trick >= 13) trick = 0;
    }

    public boolean isHeartBroken() {
        return heartBroken;
    }

    public boolean detectHeartBroken(Position position, Card card) {
        if (heartBroken) return true;

        //Nếu có bất kì người nào chỉ có bài loại cơ thì tim vỡ
        for(Player player : players) {
            if (player.allAreHearts()) {
                heartBroken = true;
                if (callback != null) {
                    Thread thread = new Thread (() -> callback.onHeartBroken());
                    thread.start();
                }
                return true;
            }
        }

        //Nếu tôi đi lá bài cơ hợp lệ thì tim vỡ. (không dẫn đầu trick, không ở lượt đầu tiên)
        if(!amILeadTrick(position) && trick > 0 && !getPlayer(position).hasCardType(getCardTypeOfTrick()) && card.getCardType().equals(CardType.HEARTS)) {
            heartBroken = true;
            if (callback != null) {
                Thread thread = new Thread (() -> callback.onHeartBroken());
                thread.start();
            }
            return true;
        }

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

    public void setPositionToGo(Position positionToGo) {
        this.positionToGo = positionToGo;
    }

    public Position getStartPositionOfTrick() {
        return startPositionOfTrick;
    }

    public void setStartPositionOfTrick(Position startPositionOfTrick) {
        this.startPositionOfTrick = startPositionOfTrick;
    }



    //End Getter và setter ------------------------------------------
}
