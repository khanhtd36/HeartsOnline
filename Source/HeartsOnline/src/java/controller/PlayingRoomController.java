package controller;

import controller.connection.ConnectionCallback;
import controller.connection.Connector;
import controller.message.ChatMsgContent;
import controller.message.Message;
import controller.message.MessageType;
import controller.message.UpdateNameMsgContent;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import model.HeartGame;
import model.player.Player;
import model.player.Position;
import org.apache.commons.lang3.text.WordUtils;

import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PlayingRoomController implements Initializable, ConnectionCallback {
    private HeartGame gameModel = new HeartGame();
    private Player[] players = new Player[4];

    private Connector connector = new Connector(this);

    private boolean host = false; //Có là chủ phòng hay không
    private boolean inRoom = false;

    public void initialize(URL location, ResourceBundle resources) {
        setTheme("skin1");
        btnExitRoom.disableProperty().bind(btnOpenRoom.disabledProperty().isNotEqualTo(new SimpleBooleanProperty(true)));
        players[0] = new Player(Position.SOUTH, "Thánh Bài");
        txtNameMe.textProperty().bind(players[0].nameProperty);
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

        btnStart.getStyleClass().set(0, "button-img");
        btnStart.getStyleClass().set(1, "start");
        btnStart.getStyleClass().set(2, "visible-img");

        leftArrow.getStyleClass().set(0, "button-img");
        leftArrow.getStyleClass().set(1, "left-arrow");
        leftArrow.getStyleClass().set(2, "invisible-img");

        upArrow.getStyleClass().set(0, "button-img");
        upArrow.getStyleClass().set(1, "up-arrow");
        upArrow.getStyleClass().set(2, "invisible-img");

        rightArrow.getStyleClass().set(0, "button-img");
        rightArrow.getStyleClass().set(1, "right-arrow");
        rightArrow.getStyleClass().set(2, "invisible-img");


        for(ImageView card : cardMe) {
            card.getStyleClass().set(0, "my-card");
            card.getStyleClass().set(1, "card-back");
            card.getStyleClass().set(2, "visible-img");
            card.getStyleClass().set(3, "unchosen-card");
        }

        for(ImageView card : cardWest) {
            card.getStyleClass().set(0, "others-card");
            card.getStyleClass().set(1, "card-back");
            card.getStyleClass().set(2, "visible-img");
        }
        for(ImageView card : cardNorth) {
            card.getStyleClass().set(0, "others-card");
            card.getStyleClass().set(1, "card-back");
            card.getStyleClass().set(2, "visible-img");
        }
        for(ImageView card : cardEast) {
            card.getStyleClass().set(0, "others-card");
            card.getStyleClass().set(1, "card-back");
            card.getStyleClass().set(2, "visible-img");
        }
    }

    //Xử lý Các hành động do người dùng trực tiếp tác động lên view ---------------

    public void updateDisplayName() {
        String newName = txtDisplayName.getText();
        players[0].nameProperty.set(newName);
        txtChatTextField.requestFocus();
        //TODO: Gửi tên mới tới các người chơi còn lại
        connector.sendMessageToAll(new Message(MessageType.UPDATE_NAME, new UpdateNameMsgContent(players[0].getId(), newName)));
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

    public void startGame() {

    }

    public void toTheLeft() {

    }

    public void toFront() {

    }

    public void toTheRight() {

    }

    public void clickOnCard() {

    }

    public void submitChat() {
        String chatLine = txtChatTextField.getText();
        if (chatLine.length() > 0) {
            addChatLine("Bạn: " + chatLine);
            txtChatTextField.setText("");

            //TEST : Sau này cần chuyển về chuẩn class message, loại thông điệp là chat
            connector.sendMessageToAll(new Message(MessageType.CHAT, new ChatMsgContent(players[0].nameProperty.get() + ": " + chatLine)));
        }
    }

    public void exitRoom() {
        host = false;
        inRoom = false;
        connector.close();
        addChatLine("-- Bạn đã rời Sòng.");
        gameModel.reset();
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
        inRoom = true;
        txtConnectionString.setText(connectionString);
        addChatLine("-- Mở sòng Thành công, Gửi bạn bè Chuỗi kết nối phía trên để vào Sòng.");
    }

    public synchronized void onConnectionReceived(Socket socketToClient) {
        //TODO: Có người kết nối tới, Xem đã đủ người chơi chưa, hay game đã bắt đầu chưa, Nếu chưa thì Tạo player Và mở MessageReceiver ở connector tới người đó, Nếu rồi thì gửi thông báo cho người đó, rồi đóng kết nối.
        addChatLine("-- Một người chơi đã kết nối");


    }

    public void onConnectToServerSucceeded(Socket socketToServer) {
        addChatLine("-- Kết nối Thành công. Chờ Thông tin từ Chủ phòng");
        //TODO: Set lại view theo kiểu khách, không phải chủ phòng (Không có nút bắt đầu game)
    }

    public void onConnectToServerFailed() {
        addChatLine("-- Kết nối Không thành công!");
        connector.close();
        setDisableCommand(false);
    }

    public void onConnectionToAClientLost(Socket socketToClient) {
        if(inRoom) addChatLine("-- Một người chơi đã thoát");
        //TODO: Chuyển Player tại vị trí của người đó thành BOT, Đóng kết nối với người đó, Thông báo tới những Player khác.
    }

    public void onConnectionToServerLost(Socket socketToServer) {
        inRoom = false;
        addChatLine("-- Mất kết nối với Chủ phòng. Game kết thúc!");
        connector.close();
        exitRoom();
    }

    public void onMsgReceived(Object msg, Socket fromSocket) {
        //TODO: Xử lý khi nhận Thông điệp
        Message message = (Message)msg;
        switch (message.getType()) {
            case CHAT:
                String chatLine = ((ChatMsgContent)message.getContent()).getChatLine();
                addChatLine(chatLine);
                if(host) connector.sendMessageToAllExcept(msg, fromSocket);
                break;
            case UPDATE_NAME:

                break;

        }
    }

    //End Xử lý các Xử kiện ở các thread khác gửi qua ------------------------------



    //view controller API -----------------------------------------------------------

    public void addChatLine(String message) {
        Platform.runLater(()->{
            lstChatView.getItems().add(WordUtils.wrap(message, 45));
            lstChatView.scrollTo(lstChatView.getItems().size() - 1);
        });
    }

    public void setTheme(String themeName) {
        themeName = themeName.toLowerCase();
        switch (themeName) {
            case "skin1":
                Platform.runLater(()->rootSceneNode.getStylesheets().set(1, "styles/CardSkin1.css"));
                break;
            case "skin2":
                Platform.runLater(()->rootSceneNode.getStylesheets().set(1, "styles/CardSkin2.css"));
                break;
        }
    }

    public void showPopUpMessage(String message) {
    }

    private void resetView() {
        setNodeToAppear(btnStart);
        setNodeToGone(leftArrow, rightArrow, upArrow);
        for(ImageView card : cardWest) {
            setNodeToAppear(card);
            setCardFace(card, "card-back");
        }
        for(ImageView card : cardNorth) {
            setNodeToAppear(card);
            setCardFace(card, "card-back");
        }
        for(ImageView card : cardEast) {
            setNodeToAppear(card);
            setCardFace(card, "card-back");
        }
        for(ImageView card : cardMe) {
            setNodeToAppear(card);
            setCardFace(card, "card-back");
        }
    }

    private void setDisableCommand(boolean b) {
        Platform.runLater(()->{
            btnOpenRoom.setDisable(b);
            txtConnectionString.setEditable(!b);
            btnJoinRoom.setDisable(b);
        });
    }

    private void setNodeToGone(Node... nodes) {
        for(Node node: nodes) {
            Platform.runLater(()->{
                node.getStyleClass().set(2, "invisible-img");
                node.setMouseTransparent(true);
            });
        }
    }

    private void setNodeToAppear(Node... nodes) {
        for(Node node : nodes) {
            Platform.runLater(()->{
                node.getStyleClass().set(2, "visible-img");
                node.setMouseTransparent(false);
            });
        }
    }

    private void setCardFace(Node node, String cardName) {
        Platform.runLater(()-> node.getStyleClass().set(1, cardName));
    }

    public void setDisplayName(String name, Position position) {
        switch (position) {
            case EAST:
                Platform.runLater(()->txtNameEast.setText(name));
                break;
            case NORTH:
                Platform.runLater(()->txtNameNorth.setText(name));
                break;
            case WEST:
                Platform.runLater(()->txtNameWest.setText(name));
                break;
            case SOUTH:
                Platform.runLater(()->txtNameMe.setText(name));
                break;
        }
    }

    //End View Controller API -------------------------------------------------------


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

    //End các control trên view cần can thiệp ---------------------------------------
}
