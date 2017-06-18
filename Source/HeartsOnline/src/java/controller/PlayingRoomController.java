package controller;

import controller.connection.ConnectionCallback;
import controller.connection.Connector;
import controller.networkMessage.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
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

public class PlayingRoomController implements Initializable, ConnectionCallback {

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

    //End các control trên view cần can thiệp ---------------------------------------

    private ThreadMessage msgToGameLoopThread = new ThreadMessage("");
    private HeartGame gameModel = new HeartGame();
    private GameLoopThread gameLoopThread = null;
    private boolean host = true; //Có là chủ phòng hay không
    private Map<Socket, Position> socketPositionMap = new HashMap<>();

    private Connector connector = new Connector(this);

    public void initialize(URL location, ResourceBundle resources) {
        setTheme("skin1");
        btnExitRoom.disableProperty().bind(btnOpenRoom.disabledProperty().isNotEqualTo(new SimpleBooleanProperty(true)));

        txtCurRoundPoint.textProperty().bind(new SimpleStringProperty("Điểm ván hiện tại: ").concat(gameModel.getPlayer(Position.SOUTH).curHandPoint));

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
            card.getStyleClass().set(4, "unchosen-card");
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

        gameLoopThread = new GameLoopThread(gameModel, this, msgToGameLoopThread);
        gameLoopThread.start();
    }

    public synchronized void exchangeCards() {
        List<Card> exchangeCards = gameModel.getExchangeCards(gameModel.getMyPosition());
        if (exchangeCards.size() == 3) {
            gameModel.setGameState(GameState.OTHER_EXCHANGING);
            setNodeToGone(leftArrow, upArrow, rightArrow);
            for (Card card : exchangeCards) {
                //TODO: sửa lại cái remove card méo hoạt động đúng
                gameModel.getPlayer(gameModel.getMyPosition()).removeACardInCardDesk(card);
            }

            Message msg = new Message(MessageType.EXCHANGE_CARD, new ExchangeCardsMsgContent(exchangeCards));
            if (host) {
                Position destPosition = gameModel.destPositionOfExchange(gameModel.getMyPosition());
                if (!gameModel.getPlayer(destPosition).isBot()) {
                    Socket destSocket = getSocketByPosition(destPosition);
                    connector.sendMessageTo(msg, destSocket);
                }
            } else {
                connector.sendMessageToAll(msg);
            }

            refreshMyCardDesk();

            //TODO: - Xem xét việc notify cho thằng gameLoop để chạy bước tiếp theo
//            msgToGameLoopThread.setMsg("OK");
//            synchronized (msgToGameLoopThread) {
//                msgToGameLoopThread.notify();
//            }
        } else {
            addChatLine("-- chọn đủ 3 lá bài đi cha.");
        }
    }

    public synchronized void clickOnCard(MouseEvent e) {
        if (gameModel.getGameState().equals(GameState.NEW)) {
            addChatLine("-- bắt đầu game đi rồi chơi cha nội");
            return;
        }

        if (gameModel.getGameState().equals(GameState.OTHER_EXCHANGING)) {
            addChatLine("-- chờ tí đi cha, nóng quá.");
            return;
        }

        Position myPosition = gameModel.getMyPosition();
        if ((gameModel.getGameState().equals(GameState.PLAYING) && !myPosition.equals(gameModel.getPositionToGo()))) {
            addChatLine("-- chưa tới lượt đâu cha, chờ xíu đi");
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
                //TODO: chỉnh lại cái remove chosenCard méo hoạt động đúng
                gameModel.getPlayer(myPosition).removeACardInExchangeCards(chosenCard);
                return;
            }

            if (gameModel.getExchangeCards(myPosition).size() >= 3) {
                addChatLine("-- chọn đủ 3 lá rồi cha.");
                return;
            }

            setCardChosen(true, cardView);
            gameModel.getExchangeCards(myPosition).add(chosenCard);
            return;
        }

        mePlayCard(gameModel.getTrick(), chosenCard);
        gameModel.getPlayer(myPosition).playACard(chosenCard);
        gameModel.next();
        Message msg = new Message(MessageType.PLAY_CARD, new PlayACardMsgContent(myPosition, chosenCard));
        connector.sendMessageToAll(msg);

        //TODO: notify gameLoop nếu cần
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
    }

    public void onConnectionToServerLost(Socket socketToServer) {
        addChatLine("-- Mất kết nối với Chủ phòng. Game kết thúc!");
        connector.close();
        exitRoom();
    }

    public void onMsgReceived(Object msg, Socket fromSocket) {
        //TODO: Xử lý khi nhận Thông điệp
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
        }
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
        gameModel.getPlayer(position).changeToBot();
        refreshPlayersDisplayName();

        addChatLine("-- " + name + " vừa thoát");
    }

    private void onReceiveCardDesk(Object msg, Socket fromSocket) {
        List<Card> cardDesk = ((CardDeskMsgContent) ((Message) msg).getContent()).getCardDesk();
        gameModel.setCardDesk(cardDesk, gameModel.getMyPosition());
        distributeCard(gameModel.getCardDesk(gameModel.getMyPosition()));

        //TODO: liên lạc với GameLoopThread, chỉnh gameModel các kiểu.
    }

    private void onReceiveExchangeCards(Object msg, Socket fromSocket) {
        if (host) {
            //TODO: - chỉnh dữ liệu gameModel cho hợp. - Nếu người nhận không phải mình thì gửi qua cho thằng nhận.
        } else {
            //TODO: - chỉnh dữ liệu gameModel cho hợp. - Hiển thị lại bài của mình
        }
    }

    private void onHeartBroken(Object msg, Socket fromSocket) {
        addChatLine("-- Trái tim Tan vỡ :3");
    }

    //End Xử lý Message -------------------------------------------------------------


    //view controller API -----------------------------------------------------------

    public void addChatLine(String message) {
        Platform.runLater(() -> {
            lstChatView.getItems().add(WordUtils.wrap(message, 45));
            lstChatView.scrollTo(lstChatView.getItems().size() - 1);
        });
    }

    public void setTheme(String themeName) {
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

    private void resetView() {
        setNodeToAppear(btnStart);
        setNodeToGone(leftArrow, rightArrow, upArrow);
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

    private void setDisableCommand(boolean b) {
        Platform.runLater(() -> {
            btnOpenRoom.setDisable(b);
            txtConnectionString.setEditable(!b);
            btnJoinRoom.setDisable(b);
        });
    }

    private void setNodeToGone(Node... nodes) {
        for (Node node : nodes) {
            Platform.runLater(() -> {
                node.getStyleClass().set(3, "invisible-img");
                node.setMouseTransparent(true);
            });
        }
    }

    private void setNodeToAppear(Node... nodes) {
        for (Node node : nodes) {
            Platform.runLater(() -> {
                node.getStyleClass().set(3, "visible-img");
                node.setMouseTransparent(false);
            });
        }
    }

    private void setCardFace(Node node, String cardName) {
        Platform.runLater(() -> node.getStyleClass().set(2, cardName));
    }

    public void setPlayerName(String name, Position position) {
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

    public void westPlayCard(int trick, Card card) {
        setNodeToGone(cardWest.get(12 - trick));
        setCardFace(cardTrickWest, card.getCssClassName());
        setNodeToAppear(cardTrickWest);
    }

    public void northPlayCard(int trick, Card card) {
        setNodeToGone(cardNorth.get(12 - trick));
        setCardFace(cardTrickNorth, card.getCssClassName());
        setNodeToAppear(cardTrickNorth);
    }

    public void eastPlayCard(int trick, Card card) {
        setNodeToGone(cardEast.get(12 - trick));
        setCardFace(cardTrickEast, card.getCssClassName());
        setNodeToAppear(cardTrickEast);
    }

    public void mePlayCard(int trick, Card card) {
        setNodeToGone(cardMe.get(12 - trick));
        setCardFace(cardTrickMe, card.getCssClassName());
        setNodeToAppear(cardTrickMe);
    }

    public void collectCard() {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
                setNodeToGone(cardTrickWest, cardTrickNorth, cardTrickEast, cardTrickMe);
            } catch (Exception e) {

            }
        });
        thread.start();
    }

    public void distributeCard(List<Card> cards) {
        Thread thread = new Thread(() -> {
            for (ArrayList<ImageView> cardDesk : cardDesks) {
                for (Node card : cardDesk) {
                    setNodeToGone(card);
                }
            }

            for (int i = 0; i < 13; i++) {
                try {
                    setCardFace(cardWest.get(i), CardName.UNKNOWN.getCssClassName());
                    setNodeToAppear(cardWest.get(i));
                    Thread.sleep(3);

                    setCardFace(cardNorth.get(i), CardName.UNKNOWN.getCssClassName());
                    setNodeToAppear(cardNorth.get(i));
                    Thread.sleep(3);

                    setCardFace(cardEast.get(i), CardName.UNKNOWN.getCssClassName());
                    setNodeToAppear(cardEast.get(i));
                    Thread.sleep(3);

                    setCardFace(cardMe.get(i), cards.get(i).getCssClassName());
                    setNodeToAppear(cardMe.get(i));
                    Thread.sleep(3);
                } catch (Exception e) {
                    continue;
                }
            }
        });
        thread.start();
    }

    public void refreshMyCardDesk() {
        List<Card> cards = gameModel.getCardDesk(gameModel.getMyPosition());
        Thread thread = new Thread(() -> {
            for (Node card : cardMe) {
                setCardChosen(false, card);
                setNodeToGone(card);
            }

            for (int i = 0; i < cards.size(); i++) {
                try {
                    setCardFace(cardMe.get(i), cards.get(i).getCssClassName());
                    setNodeToAppear(cardMe.get(i));
                    Thread.sleep(3);
                } catch (Exception e) {
                    continue;
                }
            }
        });
        thread.start();
    }

    public void setExchangeCardButton(int hand) {
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

    public Connector getConnector() {
        return connector;
    }

    public boolean isHost() {
        return host;
    }

    private boolean isSelected(Node card) {
        return card.getStyleClass().get(4).equals("chosen-card");
    }

    private Card getCardByView(Node cardView) {
        return new Card(cardView.getStyleClass().get(2));
    }

    private int getCardIndexByView(Node cardView) {
        int clickedCardIndex = -1;
        for (int i = 0; i < 13; i++) {
            if (cardMe.get(i).equals(cardView)) {
                clickedCardIndex = i;
                break;
            }
        }

        return clickedCardIndex;
    }

    private void setCardChosen(boolean b, Node... cards) {
        String styleClass = "chosen-card";
        if (!b) styleClass = "unchosen-card";

        for (Node card : cards) {
            card.getStyleClass().set(4, styleClass);
        }
    }

    //End View Controller API -------------------------------------------------------
}
