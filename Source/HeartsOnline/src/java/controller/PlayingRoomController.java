package controller;

import connection.ConnectionCallback;
import connection.Connector;
import connection.networkmessage.*;
import connection.networkmessage.msgcontent.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.GameModelCallback;
import model.GameState;
import model.HeartGame;
import model.card.Card;
import model.card.CardName;
import model.player.Player;
import model.player.Position;
import org.apache.commons.lang3.text.WordUtils;

import java.net.Socket;
import java.net.URL;
import java.util.*;

public class PlayingRoomController implements Initializable, ConnectionCallback, GameModelCallback {

    //Các control trên view cần can thiệp -------------------------------------------

    public AnchorPane rootSceneNode;
    public Text txtCurRoundPoint;
    public ImageView cardWest01, cardWest02, cardWest03, cardWest04, cardWest05, cardWest06, cardWest07, cardWest08, cardWest09, cardWest10, cardWest11, cardWest12, cardWest13;
    public ImageView cardNorth01, cardNorth02, cardNorth03, cardNorth04, cardNorth05, cardNorth06, cardNorth07, cardNorth08, cardNorth09, cardNorth10, cardNorth11, cardNorth12, cardNorth13;
    public ImageView cardEast01, cardEast02, cardEast03, cardEast04, cardEast05, cardEast06, cardEast07, cardEast08, cardEast09, cardEast10, cardEast11, cardEast12, cardEast13;
    public ImageView cardMe01, cardMe02, cardMe03, cardMe04, cardMe05, cardMe06, cardMe07, cardMe08, cardMe09, cardMe10, cardMe11, cardMe12, cardMe13;
    public ImageView cardTrickWest, cardTrickNorth, cardTrickEast, cardTrickMe;
    public ImageView btnStart, leftArrow, rightArrow, upArrow;
    public Text txtNameMe, txtNameWest, txtNameNorth, txtNameEast;
    public ListView lstChatView;
    public TextField txtChatTextField;
    public TextField txtDisplayName, txtConnectionString;
    public Button btnOpenRoom, btnJoinRoom, btnExitRoom;
    private ArrayList<ImageView> cardWest = new ArrayList<>();
    private ArrayList<ImageView> cardNorth = new ArrayList<>();
    private ArrayList<ImageView> cardEast = new ArrayList<>();
    private ArrayList<ImageView> cardMe = new ArrayList<>();
    private ArrayList<ArrayList<ImageView>> cardDesks = new ArrayList<>();
    private Stage stage;

    //End các control trên view cần can thiệp ---------------------------------------

    private HeartGame gameModel = new HeartGame(this);
    private boolean host = true; //Có là chủ phòng hay không
    private Map<Socket, Position> socketPositionMap = new HashMap<>();

    private Connector connector = new Connector(this);

    public void initialize(URL location, ResourceBundle resources) {
        setTheme("skin1");
        btnExitRoom.disableProperty().bind(btnOpenRoom.disabledProperty().isNotEqualTo(new SimpleBooleanProperty(true)));

        cardWest.add(cardWest01);
        cardWest.add(cardWest02);
        cardWest.add(cardWest03);
        cardWest.add(cardWest04);
        cardWest.add(cardWest05);
        cardWest.add(cardWest06);
        cardWest.add(cardWest07);
        cardWest.add(cardWest08);
        cardWest.add(cardWest09);
        cardWest.add(cardWest10);
        cardWest.add(cardWest11);
        cardWest.add(cardWest12);
        cardWest.add(cardWest13);

        cardNorth.add(cardNorth01);
        cardNorth.add(cardNorth02);
        cardNorth.add(cardNorth03);
        cardNorth.add(cardNorth04);
        cardNorth.add(cardNorth05);
        cardNorth.add(cardNorth06);
        cardNorth.add(cardNorth07);
        cardNorth.add(cardNorth08);
        cardNorth.add(cardNorth09);
        cardNorth.add(cardNorth10);
        cardNorth.add(cardNorth11);
        cardNorth.add(cardNorth12);
        cardNorth.add(cardNorth13);

        cardEast.add(cardEast01);
        cardEast.add(cardEast02);
        cardEast.add(cardEast03);
        cardEast.add(cardEast04);
        cardEast.add(cardEast05);
        cardEast.add(cardEast06);
        cardEast.add(cardEast07);
        cardEast.add(cardEast08);
        cardEast.add(cardEast09);
        cardEast.add(cardEast10);
        cardEast.add(cardEast11);
        cardEast.add(cardEast12);
        cardEast.add(cardEast13);

        cardMe.add(cardMe01);
        cardMe.add(cardMe02);
        cardMe.add(cardMe03);
        cardMe.add(cardMe04);
        cardMe.add(cardMe05);
        cardMe.add(cardMe06);
        cardMe.add(cardMe07);
        cardMe.add(cardMe08);
        cardMe.add(cardMe09);
        cardMe.add(cardMe10);
        cardMe.add(cardMe11);
        cardMe.add(cardMe12);
        cardMe.add(cardMe13);

        cardDesks.add(cardMe);
        cardDesks.add(cardWest);
        cardDesks.add(cardNorth);
        cardDesks.add(cardEast);

        btnStart.getStyleClass().set(1, "button-img");
        btnStart.getStyleClass().set(2, "start");
        btnStart.getStyleClass().set(3, "visible-img");

        leftArrow.getStyleClass().set(1, "button-img");
        leftArrow.getStyleClass().set(2, "left-arrow");
        leftArrow.getStyleClass().set(3, "invisible-img");

        upArrow.getStyleClass().set(1, "button-img");
        upArrow.getStyleClass().set(2, "up-arrow");
        upArrow.getStyleClass().set(3, "invisible-img");

        rightArrow.getStyleClass().set(1, "button-img");
        rightArrow.getStyleClass().set(2, "right-arrow");
        rightArrow.getStyleClass().set(3, "invisible-img");


        for (ImageView card : cardMe) {
            card.getStyleClass().set(1, "my-card");
            card.getStyleClass().set(2, "card-back");
            card.getStyleClass().set(3, "visible-img");
            card.getStyleClass().set(4, "unselected-card");
        }

        for (ImageView card : cardWest) {
            card.getStyleClass().set(1, "others-card");
            card.getStyleClass().set(2, "card-back");
            card.getStyleClass().set(3, "visible-img");
        }
        for (ImageView card : cardNorth) {
            card.getStyleClass().set(1, "others-card");
            card.getStyleClass().set(2, "card-back");
            card.getStyleClass().set(3, "visible-img");
        }
        for (ImageView card : cardEast) {
            card.getStyleClass().set(1, "others-card");
            card.getStyleClass().set(2, "card-back");
            card.getStyleClass().set(3, "visible-img");
        }
    }

    public void setStageAndSetupListens(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest((a) -> exitRoom());
    }

    //Xử lý Các hành động do người dùng trực tiếp tác động lên view ---------------

    public void updateDisplayName() {
        String newName = txtDisplayName.getText();
        gameModel.setPlayerName(gameModel.getMyPosition(), newName);
        setPlayerDisplayName(newName, Position.SOUTH);
        txtChatTextField.requestFocus();

        connector.sendMessageToAll(new Message(MessageType.UPDATE_NAME, new UpdateNameMsgContent(gameModel.getMyPosition(), newName)));
    }

    public void openRoom() {
        setDisableCommand(true);
        connector.openListener();
        host = true;
    }

    public void joinRoom() {
        setDisableCommand(true);
        String connectionString = txtConnectionString.getText();
        connector.connectTo(connectionString);
    }

    public synchronized void startGame() {
        host = true;
        try {
            setDisableCommand(true);
            setNodeToGone(btnStart);
            connector.stopListen();
        } catch (Exception e) {
        }

        gameModel.init();
        startHand();
    }

    public synchronized void exchangeCards() {
        List<Card> exchangeCards = gameModel.getExchangeCards(gameModel.getMyPosition());
        if (exchangeCards.size() == 3) {
            gameModel.setGameState(GameState.WAITING);
            setNodeToGone(leftArrow, upArrow, rightArrow);

            Position srcPosition = gameModel.getMyPosition();
            Position destPosition = gameModel.destPositionOfExchange(srcPosition);

            gameModel.exchangeCards(srcPosition, destPosition);

            Message exchangeCardsMsg = new Message(MessageType.EXCHANGE_CARD, new ExchangeCardsMsgContent(exchangeCards));
            if (host) {
                if (!gameModel.getPlayer(destPosition).isBot()) {
                    Socket destSocket = getSocketByPosition(destPosition);
                    connector.sendMessageTo(exchangeCardsMsg, destSocket);
                }
            } else {
                connector.sendMessageToAll(exchangeCardsMsg);
            }

            refreshMyCardDesk();

            if (gameModel.exchangeDone()) {
                gameModel.startTurn();
                Message startTurnMsg = new Message(MessageType.TURN, new StartTurnMsgContent(gameModel.getPositionToGo()));
                connector.sendMessageToAll(startTurnMsg);
            }
        } else {
            addChatLine("-- chọn đủ 3 lá bài đi cha.");
        }
    }

    public synchronized void clickOnCard(MouseEvent e) {
        if (gameModel.getGameState().equals(GameState.NEW)) {
//            addChatLine("-- bắt đầu game đi rồi chơi cha nội");
            return;
        }

        if (gameModel.getGameState().equals(GameState.WAITING)) {
//            addChatLine("-- chờ xíu đi cha, nóng quá.");
            return;
        }

        Position myPosition = gameModel.getMyPosition();
        if ((gameModel.getGameState().equals(GameState.PLAYING) && !myPosition.equals(gameModel.getPositionToGo()))) {
//            addChatLine("-- chưa tới lượt đâu cha, chờ xíu đi");
            return;
        }

        if (gameModel.getGameState().equals(GameState.SHOWING_RESULT)) {
            return;
        }

        ImageView cardView = ((ImageView) e.getSource());

        //Đang chuyển bài cho nhau
        Card chosenCard = getCardByView(cardView);
        if (gameModel.getGameState().equals(GameState.EXCHANGING)) {
            if (isSelected(cardView)) {
                setCardChosen(false, cardView);
                gameModel.unselectACard(chosenCard);
                return;
            }

            if (gameModel.getExchangeCards(myPosition).size() >= 3) {
//                addChatLine("-- đủ 3 lá rồi cha, chọn chọn quài");
                return;
            }

            setCardChosen(true, cardView);
            gameModel.selectACard(chosenCard);
            return;
        }

        //Đang trong ván, đúng lượt đi
        if (gameModel.canIPlay(gameModel.getMyPosition(), chosenCard)) {
            gameModel.setGameState(GameState.WAITING);
            gameModel.playACard(myPosition, chosenCard);
            mePlayCard(gameModel.getTrick(), chosenCard);
            Message msg = new Message(MessageType.PLAY_CARD, new PlayACardMsgContent(myPosition, chosenCard));
            connector.sendMessageToAll(msg);

            Thread thread = new Thread(() -> gameModel.next(host, false));
            thread.start();
        } else {
            return;
        }
    }

    public void submitChat() {
        String chatLine = txtChatTextField.getText();
        if (chatLine.length() > 0) {
            addChatLine("Bạn: " + chatLine);
            txtChatTextField.setText("");

            connector.sendMessageToAll(new Message(MessageType.CHAT, new ChatMsgContent(gameModel.getName(gameModel.getMyPosition()) + ": " + chatLine)));
        }
    }

    public void exitRoom() {
        host = true;
        connector.close();
        addChatLine("-- Bạn đã rời Sòng.");
        gameModel.resetAll();
        resetView();
        setDisableCommand(false);
    }

    public void chooseSkin1() {
        setTheme("skin1");
    }

    public void chooseSkin2() {
        setTheme("skin2");
    }

    //End Xử lý Các hành động do người dùng trực tiếp tác động lên view ------------


    //Xử lý các Xử kiện ở các thread khác gửi qua ----------------------------------

    public void onListenerOpenFailed() {
        setDisableCommand(false);
        addChatLine("-- Mở sòng không được.");
    }

    public void onListenerOpenSucceeded(String connectionString) {
        host = true;
        txtConnectionString.setText(connectionString);
        addChatLine("-- Mở sòng Thành công, Gửi bạn bè Chuỗi kết nối phía trên để vào Sòng.");
    }

    public synchronized void onConnectionReceived(Socket socketToClient) {
        Position availablePosition = gameModel.getAvailablePosition();
        if (availablePosition != null) {
            addChatLine("-- Một người chơi đã kết nối");
            socketPositionMap.put(socketToClient, availablePosition);
            gameModel.getPlayer(availablePosition).setBot(false);

            ArrayList<Player> otherPlayers = ((ArrayList<Player>) gameModel.getPlayers().clone());
            otherPlayers.remove(gameModel.getPlayer(availablePosition));

            Message msg = new Message(MessageType.JOIN_ACCEPT, new JoinAcceptMsgContent(availablePosition, otherPlayers));

            connector.sendMessageTo(msg, socketToClient);
        } else {
            connector.shutdownConnectionTo(socketToClient);
        }
    }

    public void onConnectToServerSucceeded(Socket socketToServer) {
        host = false;
        addChatLine("-- Kết nối Thành công. Chờ Thông tin từ Chủ phòng");
        setNodeToGone(btnStart);

        String name = gameModel.getPlayer(gameModel.getMyPosition()).getName();
        connector.sendMessageTo(new Message(MessageType.JOIN_REQUEST, new JoinRequestMsgContent(name)), socketToServer);
    }

    public void onConnectToServerFailed() {
        addChatLine("-- Kết nối Không thành công!");
        connector.close();
        setDisableCommand(false);
        host = true;
    }

    public void onConnectionToAClientLost(Socket socketToClient) {
        addChatLine("-- Một người chơi đã thoát");

        Position position = getPositionBySocket(socketToClient);
        gameModel.getPlayer(position).changeToBot();

        refreshPlayersDisplayName();
        socketPositionMap.remove(position);
        connector.shutdownConnectionTo(socketToClient);

        connector.sendMessageToAll(new Message(MessageType.A_PLAYER_EXIT, new APlayerExitMsgContent(position)));

        if (gameModel.getGameState().equals(GameState.SHOWING_RESULT) || gameModel.getGameState().equals(GameState.NEW)) return;

        if (gameModel.getPositionToGo().equals(position)) {
            Card playCard = gameModel.getPlayer(position).autoPlayACard(gameModel.getTrick(), gameModel.getCardTypeOfTrick(), gameModel.getCardsOnBoard());
            onABotPlayedACard(position, playCard);
        } else if (!gameModel.exchangeDone()) {
            gameModel.getPlayer(position).autoExchangeCards();
            Position destPosition = gameModel.destPositionOfExchange(position);

            gameModel.exchangeCards(position, destPosition);

            if (!destPosition.equals(gameModel.getMyPosition())) {
                Socket destSocket = getSocketByPosition(destPosition);
                Message msgToDest = new Message(MessageType.EXCHANGE_CARD, new ExchangeCardsMsgContent(gameModel.getExchangeCards(position)));
                connector.sendMessageTo(msgToDest, destSocket);
            } else {
                refreshMyCardDesk();
            }

            if (gameModel.exchangeDone()) {
                gameModel.startTurn();
                Message startTurnMsg = new Message(MessageType.TURN, new StartTurnMsgContent(gameModel.getPositionToGo()));
                connector.sendMessageToAll(startTurnMsg);
            }
        }
    }

    public void onConnectionToServerLost(Socket socketToServer) {
        addChatLine("-- Mất kết nối với Chủ phòng. Game kết thúc!");
        connector.close();
        exitRoom();
    }

    public void onMsgReceived(Object msg, Socket fromSocket) {
        Message message = (Message) msg;
        switch (message.getType()) {
            case CHAT:
                onChat(msg, fromSocket);
                break;

            case UPDATE_NAME:
                onUpdateName(msg, fromSocket);
                break;

            case JOIN_REQUEST:
                onJoinRequest(msg, fromSocket);
                break;

            case JOIN_ACCEPT:
                onJoinAccept(msg, fromSocket);
                break;

            case NEW_PLAYER_JOIN:
                onNewPlayerJoin(msg, fromSocket);
                break;

            case A_PLAYER_EXIT:
                onAPlayerExit(msg, fromSocket);
                break;

            case RECEIVE_CARD_DESK:
                onReceiveCardDesk(msg, fromSocket);
                break;

            case EXCHANGE_CARD:
                onReceiveExchangeCards(msg, fromSocket);
                break;

            case HEART_BROKEN:
                onHeartBroken(msg, fromSocket);
                break;

            case TURN:
                onTurnReceived(msg, fromSocket);
                break;

            case PLAY_CARD:
                onAPlayerPlayACard(msg, fromSocket);
                break;
        }
    }

    public synchronized void onTrickDone(Position positionToEat) {
        gameModel.eatCards(positionToEat);
        gameModel.increaseTrick();
        collectCard(positionToEat);
    }

    public synchronized void onHandDone() {
        Thread thread = new Thread(() -> {
            gameModel.calcResultPoints();
            if (gameModel.getPlayer(gameModel.getMyPosition()).doesShootTheMoon()) {
                onMyCurHandPointChanged();
            }
            setNodeToGone(cardTrickMe, cardTrickWest, cardTrickNorth, cardTrickEast);
            showEatenCards();
            addChatLine("--------- Tổng kết: --------");
            addChatLine(gameModel.getName(Position.SOUTH) + ": " + gameModel.getPlayer(Position.SOUTH).getCurHandPoint() + " -> tổng: " + gameModel.getPlayer(Position.SOUTH).getAccumulatedPoint());
            addChatLine(gameModel.getName(Position.WEST) + ": " + gameModel.getPlayer(Position.WEST).getCurHandPoint() + " -> tổng: " + gameModel.getPlayer(Position.WEST).getAccumulatedPoint());
            addChatLine(gameModel.getName(Position.NORTH) + ": " + gameModel.getPlayer(Position.NORTH).getCurHandPoint() + " -> tổng: " + gameModel.getPlayer(Position.NORTH).getAccumulatedPoint());
            addChatLine(gameModel.getName(Position.EAST) + ": " + gameModel.getPlayer(Position.EAST).getCurHandPoint() + " -> tổng: " + gameModel.getPlayer(Position.EAST).getAccumulatedPoint());

            if (gameModel.isOver()) {
                addChatLine("-- đề mề Trùm hôm nay là: " + gameModel.winner().getName());
                gameModel.resetAllExceptPersonalInfo();
            } else {
                gameModel.resetHand();
                gameModel.increaseHand();
            }
            addChatLine("--------- Tổng kết: --------");

            if (host) {
                try {
                    Thread.sleep(3500);
                    setNodeToAppear(btnStart);
                } catch (Exception e) {

                }
            }
        });
        thread.start();
    }

    public void onMyCurHandPointChanged() {
        Thread thread = new Thread(() -> {
            if (gameModel.getPlayer(gameModel.getMyPosition()).getCurHandPoint() == 0) {
                txtCurRoundPoint.setText("Đưu Bắn mặt trăng luôn");
            } else {
                Platform.runLater(() -> txtCurRoundPoint.setFill(Color.RED));
                for (int i = 0; i < 6; i++) {
                    Platform.runLater(() -> txtCurRoundPoint.setText(""));

                    try {
                        Thread.sleep(270);
                    } catch (Exception e) {
                    }
                    Platform.runLater(() -> txtCurRoundPoint.setText("Điểm ván hiện tại: " + gameModel.getPlayer(gameModel.getMyPosition()).getCurHandPoint()));

                    try {
                        Thread.sleep(270);
                    } catch (Exception e) {
                    }
                }
                Platform.runLater(() -> txtCurRoundPoint.setFill(Color.BLACK));
            }
        });
        thread.start();
    }

    //End Xử lý các Xử kiện ở các thread khác gửi qua -------------------------------


    //Xử lý Message -----------------------------------------------------------------

    private void onChat(Object msg, Socket fromSocket) {
        String chatLine = ((ChatMsgContent) ((Message) msg).getContent()).getChatLine();
        addChatLine(chatLine);
        if (host) connector.sendMessageToAllExcept(msg, fromSocket);
    }

    private void onUpdateName(Object msg, Socket fromSocket) {
        Position positionOfPlayer;
        if (host) {
            positionOfPlayer = getPositionBySocket(fromSocket);
        } else {
            positionOfPlayer = ((UpdateNameMsgContent) ((Message) msg).getContent()).getPosition();
        }
        String oldName = gameModel.getPlayer(positionOfPlayer).getName();
        String newNameOfPlayer = ((UpdateNameMsgContent) ((Message) msg).getContent()).getName();

        setPlayerName(newNameOfPlayer, positionOfPlayer);

        if (host) {
            ((UpdateNameMsgContent) ((Message) msg).getContent()).setPosition(positionOfPlayer);
            connector.sendMessageToAllExcept(msg, fromSocket);
        }

        addChatLine("-- " + oldName + " đã đổi danh tánh thành " + newNameOfPlayer);
    }

    private void onJoinRequest(Object msg, Socket fromSocket) {
        String name = ((JoinRequestMsgContent) ((Message) msg).getContent()).getName();
        Position position = getPositionBySocket(fromSocket);
        if (position != null) {
            connector.sendMessageToAllExcept(new Message(MessageType.NEW_PLAYER_JOIN, new NewPlayerJoinMsgContent(position, name)), fromSocket);
            gameModel.setPlayerName(position, name);

            refreshPlayersDisplayName();

            addChatLine("-- " + name + " ngồi ở ghế " + position.getName());
        }
    }

    private void onJoinAccept(Object msg, Socket fromSocket) {
        Position myPosition = ((JoinAcceptMsgContent) ((Message) msg).getContent()).getMyPosition();
        gameModel.setMyPosition(myPosition);
        gameModel.getPlayers().get(0).setPosition(myPosition);
        Player[] otherPlayers = ((JoinAcceptMsgContent) ((Message) msg).getContent()).getOtherPlayers();
        for (Player player : otherPlayers) {
            setPlayer(myPosition, player);
        }

        addChatLine("-- Bạn đã tham gia Sòng.");
    }

    private void onNewPlayerJoin(Object msg, Socket fromSocket) {
        Position position = ((NewPlayerJoinMsgContent) ((Message) msg).getContent()).getPosition();
        String name = ((NewPlayerJoinMsgContent) ((Message) msg).getContent()).getName();

        gameModel.getPlayer(position).setName(name);
        refreshPlayersDisplayName();

        addChatLine("-- " + name + " vừa vào Sòng.");
    }

    private void onAPlayerExit(Object msg, Socket fromSocket) {
        Position position = ((APlayerExitMsgContent) ((Message) msg).getContent()).getPosition();
        String name = gameModel.getName(position);
        addChatLine("-- " + name + " vừa thoát");

        gameModel.getPlayer(position).changeToBot();
        refreshPlayersDisplayName();

    }

    private void onReceiveCardDesk(Object msg, Socket fromSocket) {
        List<Card> cardDesk = ((CardDeskMsgContent) ((Message) msg).getContent()).getCardDesk();
        gameModel.setCardDesk(cardDesk, gameModel.getMyPosition());
        distributeCard(gameModel.getCardDesk(gameModel.getMyPosition()));

        if ((gameModel.getHand() + 1) % 4 == 0) {
            gameModel.setGameState(GameState.WAITING);
        } else {
            gameModel.setGameState(GameState.EXCHANGING);
            setExchangeCardButton(gameModel.getHand());
        }
    }

    private void onReceiveExchangeCards(Object msg, Socket fromSocket) {
        List<Card> receivedCards = ((ExchangeCardsMsgContent) ((Message) msg).getContent()).getExchangeCards();
        if (host) {
            Position srcPosition = getPositionBySocket(fromSocket);
            Position destPosition = gameModel.destPositionOfExchange(srcPosition);

            gameModel.getPlayer(srcPosition).setExchangeCards(receivedCards);
            gameModel.exchangeCards(srcPosition, destPosition);

            if (!destPosition.equals(gameModel.getMyPosition())) {
                Socket destSocket = getSocketByPosition(destPosition);
                Message msgToDest = new Message(MessageType.EXCHANGE_CARD, new ExchangeCardsMsgContent(receivedCards));
                connector.sendMessageTo(msgToDest, destSocket);
            } else {
                refreshMyCardDesk();
            }

            if (gameModel.exchangeDone()) {
                gameModel.startTurn();
                Message startTurnMsg = new Message(MessageType.TURN, new StartTurnMsgContent(gameModel.getPositionToGo()));
                connector.sendMessageToAll(startTurnMsg);
            }
        } else {
            gameModel.receiveExchangeCards(receivedCards);
            refreshMyCardDesk();
        }
    }

    private void onHeartBroken(Object msg, Socket fromSocket) {
        gameModel.setHeartBroken(true);
        addChatLine("-- Trái tim Tan vỡ :3");

        if (host) {
            connector.sendMessageToAll(msg);
        }
    }

    private void onTurnReceived(Object msg, Socket fromSocket) {
        Position positionToGo = ((StartTurnMsgContent) ((Message) msg).getContent()).getPosition();
        gameModel.setPositionToGo(positionToGo);
        gameModel.setGameState(GameState.PLAYING);
    }

    private void onAPlayerPlayACard(Object msg, Socket fromSocket) {
        Position position = ((PlayACardMsgContent) ((Message) msg).getContent()).getPosition();
        Card card = ((PlayACardMsgContent) ((Message) msg).getContent()).getCard();

        gameModel.playACard(position, card);
        playACard(gameModel.getTrick(), card, modelPositionToViewPosition(position));

        if (host) {
            connector.sendMessageToAllExcept(msg, fromSocket);
        }

        Thread thread = new Thread(() -> gameModel.next(host, false));
        thread.start();
    }

    public void onABotPlayedACard(Position position, Card card) {
        playACard(gameModel.getTrick(), card, position);
        Message msg = new Message(MessageType.PLAY_CARD, new PlayACardMsgContent(position, card));
        connector.sendMessageToAll(msg);

        Thread thread = new Thread(() -> gameModel.next(host, false));
        thread.start();
    }

    public void onHeartBroken() {
        gameModel.setHeartBroken(true);
        addChatLine("-- Trái tim Tan vỡ :3");
        Message msg = new Message(MessageType.HEART_BROKEN, new HeartBrokenMsgContent());
        connector.sendMessageToAll(msg);
    }

    //End Xử lý Message -------------------------------------------------------------


    //view controller API -----------------------------------------------------------

    public synchronized void addChatLine(String message) {
        Platform.runLater(() -> {
            lstChatView.getItems().add(WordUtils.wrap(message, 45));
            lstChatView.scrollTo(lstChatView.getItems().size() - 1);
        });
    }

    public synchronized void setTheme(String themeName) {
        themeName = themeName.toLowerCase();
        switch (themeName) {
            case "skin1":
                Platform.runLater(() -> rootSceneNode.getStylesheets().set(1, "styles/CardSkin1.css"));
                break;
            case "skin2":
                Platform.runLater(() -> rootSceneNode.getStylesheets().set(1, "styles/CardSkin2.css"));
                break;
        }
    }

    private synchronized void resetView() {
        setNodeToAppear(btnStart);
        setNodeToGone(leftArrow, rightArrow, upArrow);
        setNodeToGone(cardTrickMe, cardTrickWest, cardTrickNorth, cardTrickEast);
        for (ImageView card : cardWest) {
            setNodeToAppear(card);
            setCardFace(card, "card-back");
        }
        for (ImageView card : cardNorth) {
            setNodeToAppear(card);
            setCardFace(card, "card-back");
        }
        for (ImageView card : cardEast) {
            setNodeToAppear(card);
            setCardFace(card, "card-back");
        }
        for (ImageView card : cardMe) {
            setNodeToAppear(card);
            setCardFace(card, "card-back");
            setCardChosen(false, card);
        }
    }

    private synchronized void setDisableCommand(boolean b) {
        Platform.runLater(() -> {
            btnOpenRoom.setDisable(b);
            txtConnectionString.setEditable(!b);
            btnJoinRoom.setDisable(b);
        });
    }

    private synchronized void setNodeToGone(Node... nodes) {
        for (Node node : nodes) {
            Platform.runLater(() -> {
                node.getStyleClass().set(3, "invisible-img");
                node.setMouseTransparent(true);
            });
        }
    }

    private synchronized void setNodeToAppear(Node... nodes) {
        for (Node node : nodes) {
            Platform.runLater(() -> {
                node.getStyleClass().set(3, "visible-img");
                node.setMouseTransparent(false);
            });
        }
    }

    private synchronized void setCardFace(Node node, String cardName) {
        Platform.runLater(() -> node.getStyleClass().set(2, cardName));
    }

    public synchronized void setPlayerName(String name, Position position) {
        gameModel.getPlayer(position).setName(name);
        refreshPlayersDisplayName();
    }

    public Position getPositionBySocket(Socket socket) {
        return socketPositionMap.get(socket);
    }

    public Socket getSocketByPosition(Position position) {
        for (Socket socket : socketPositionMap.keySet()) {
            if (socketPositionMap.get(socket).equals(position)) {
                return socket;
            }
        }

        return null;
    }

    private void setPlayer(Position myPositon, Player player) {
        int dP = player.getPosition().getOrder() - myPositon.getOrder();
        if (dP < 0) dP += 4;
        gameModel.getPlayers().set(dP, player);
        refreshPlayersDisplayName();
    }

    private void setPlayerDisplayName(String name, Position positionInView) {
        switch (positionInView) {
            case SOUTH:
                Platform.runLater(() -> txtNameMe.setText(name));
                break;

            case WEST:
                Platform.runLater(() -> txtNameWest.setText(name));
                break;

            case NORTH:
                Platform.runLater(() -> txtNameNorth.setText(name));
                break;

            case EAST:
                Platform.runLater(() -> txtNameEast.setText(name));
                break;
        }
    }

    private void refreshPlayersDisplayName() {
        Platform.runLater(() -> txtNameMe.setText(gameModel.getPlayers().get(0).getName()));
        Platform.runLater(() -> txtNameWest.setText(gameModel.getPlayers().get(1).getName()));
        Platform.runLater(() -> txtNameNorth.setText(gameModel.getPlayers().get(2).getName()));
        Platform.runLater(() -> txtNameEast.setText(gameModel.getPlayers().get(3).getName()));
    }

    public synchronized void westPlayCard(int trick, Card card) {
        setNodeToGone(cardWest.get(12 - trick));
        setCardFace(cardTrickWest, card.getCssClassName());
        setNodeToAppear(cardTrickWest);
    }

    public synchronized void northPlayCard(int trick, Card card) {
        setNodeToGone(cardNorth.get(12 - trick));
        setCardFace(cardTrickNorth, card.getCssClassName());
        setNodeToAppear(cardTrickNorth);
    }

    public synchronized void eastPlayCard(int trick, Card card) {
        setNodeToGone(cardEast.get(12 - trick));
        setCardFace(cardTrickEast, card.getCssClassName());
        setNodeToAppear(cardTrickEast);
    }

    public synchronized void mePlayCard(int trick, Card card) {
        try {
            collectingCardsThread.join();
        } catch (Exception e) {

        }
        setCardFace(cardTrickMe, card.getCssClassName());
        setNodeToAppear(cardTrickMe);
        refreshMyCardDesk();
    }

    public synchronized void playACard(int trick, Card card, Position positionInView) {
        try {
            collectingCardsThread.join();
        } catch (Exception e) {

        }
        switch (positionInView) {
            case SOUTH:
                mePlayCard(trick, card);
                break;

            case WEST:
                westPlayCard(trick, card);
                break;

            case NORTH:
                northPlayCard(trick, card);
                break;

            case EAST:
                eastPlayCard(trick, card);
                break;
        }
    }

    Thread collectingCardsThread = new Thread();
    public synchronized void collectCard(Position positionToEat) {
        collectingCardsThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
                setNodeToGone(cardTrickWest, cardTrickNorth, cardTrickEast, cardTrickMe);
                gameModel.setStartPositionOfTrick(positionToEat);
                gameModel.setPositionToGo(positionToEat);
                gameModel.setGameState(GameState.PLAYING);
                gameModel.next(host, true);
            } catch (Exception e) {

            }
        });
        collectingCardsThread.start();
    }

    public synchronized void distributeCard(List<Card> cards) {
        for (ArrayList<ImageView> cardDesk : cardDesks) {
            for (Node card : cardDesk) {
                setNodeToGone(card);
            }
        }

        for (int i = 0; i < 13; i++) {
            try {
                setCardFace(cardWest.get(i), CardName.UNKNOWN.getCssClassName());
                setNodeToAppear(cardWest.get(i));

                setCardFace(cardNorth.get(i), CardName.UNKNOWN.getCssClassName());
                setNodeToAppear(cardNorth.get(i));

                setCardFace(cardEast.get(i), CardName.UNKNOWN.getCssClassName());
                setNodeToAppear(cardEast.get(i));

                setCardFace(cardMe.get(i), cards.get(i).getCssClassName());
                setNodeToAppear(cardMe.get(i));
            } catch (Exception e) {
                continue;
            }
        }
    }

    public synchronized void refreshMyCardDesk() {
        List<Card> cards = gameModel.getCardDesk(gameModel.getMyPosition());
        for (Node card : cardMe) {
            setCardChosen(false, card);
        }

        for (int i = 0; i < cards.size() && i < 13; i++) {
            try {
                setCardFace(cardMe.get(i), cards.get(i).getCssClassName());
                setNodeToAppear(cardMe.get(i));
            } catch (Exception e) {
                continue;
            }
        }

        for (int i = cards.size(); i < 13; i++) {
            setNodeToGone(cardMe.get(i));
        }
    }

    public synchronized void showEatenCards() {
        List<Card> meEatenCards = gameModel.getPlayers().get(0).getEatenCards();
        List<Card> westEatenCards = gameModel.getPlayers().get(1).getEatenCards();
        List<Card> northEatenCards = gameModel.getPlayers().get(2).getEatenCards();
        List<Card> eastEatenCards = gameModel.getPlayers().get(3).getEatenCards();

        List<List<Card>> eatenCards = new ArrayList<>();

        eatenCards.add(meEatenCards);
        eatenCards.add(westEatenCards);
        eatenCards.add(northEatenCards);
        eatenCards.add(eastEatenCards);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < eatenCards.get(i).size() && j < 13; j++) {
                try {
                    setCardFace(cardDesks.get(i).get(j), eatenCards.get(i).get(j).getCssClassName());
                    setNodeToAppear(cardDesks.get(i).get(j));
                } catch (Exception e) {
                    continue;
                }
            }
        }
    }

    public synchronized void setExchangeCardButton(int hand) {
        hand %= 4;
        switch (hand) {
            case 0:
                setNodeToAppear(leftArrow);
                setNodeToGone(upArrow, rightArrow);
                break;

            case 1:
                setNodeToAppear(rightArrow);
                setNodeToGone(leftArrow, upArrow);
                break;

            case 2:
                setNodeToAppear(upArrow);
                setNodeToGone(leftArrow, rightArrow);
                break;

            case 3:
                setNodeToGone(leftArrow, upArrow, rightArrow);
                break;
        }
    }

    private boolean isSelected(Node card) {
        return card.getStyleClass().get(4).equals("selected-card");
    }

    private Card getCardByView(Node cardView) {
        return new Card(cardView.getStyleClass().get(2));
    }

    private void setCardChosen(boolean b, Node... cards) {
        final String styleClass = b ? "selected-card" : "unselected-card";

        for (Node card : cards) {
            Platform.runLater(() -> card.getStyleClass().set(4, styleClass));
        }
    }

    private Position modelPositionToViewPosition(Position modelPosition) {
        Position myPosition = gameModel.getMyPosition();
        int dP = modelPosition.getOrder() - myPosition.getOrder();
        if (dP < 0) dP += 4;

        return Position.values()[dP];
    }

    private void startHand() {
        Thread thread = new Thread(() -> {
            Platform.runLater(() -> txtCurRoundPoint.setText("Điểm ván hiện tại: 0"));
            gameModel.generateCard();
            List<Card> cardNames = gameModel.getCardDesk(gameModel.getMyPosition());
            //Phát bài trên view của mình
            distributeCard(cardNames);

            //Phát bài cho các client
            for (int i = 1; i < 4; i++) {
                if (!gameModel.getPlayers().get(i).isBot()) {
                    Position position = Position.values()[i];
                    Message msg = new Message(MessageType.RECEIVE_CARD_DESK, new CardDeskMsgContent(gameModel.getCardDesk(position)));
                    connector.sendMessageTo(msg, getSocketByPosition(position));
                }
            }

            //Kiểm tra hand thứ mấy để đổi bài cho nhau
            setExchangeCardButton(gameModel.getHand());
            if ((gameModel.getHand() + 1) % 4 != 0) {
                gameModel.setGameState(GameState.EXCHANGING);
                for (Player player : gameModel.getPlayers()) {
                    if (player.isBot()) {
                        player.autoExchangeCards();
                        Position positionToReceive = gameModel.destPositionOfExchange(player.getPosition());
                        gameModel.exchangeCards(player.getPosition(), positionToReceive);
                        if (positionToReceive.equals(Position.SOUTH)) {
                            refreshMyCardDesk();
                        }

                        Message exchangeCardsMsg = new Message(MessageType.EXCHANGE_CARD, new ExchangeCardsMsgContent(player.getExchangeCards()));
                        Socket destSocket = getSocketByPosition(positionToReceive);
                        connector.sendMessageTo(exchangeCardsMsg, destSocket);
                    }
                }
            } else {
                gameModel.startTurn();
            }
        });
        thread.start();
    }

    //End View Controller API -------------------------------------------------------
}
