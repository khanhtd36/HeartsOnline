package controller;

import controller.connection.ConnectionCallback;
import controller.connection.Connector;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import model.HeartGame;
import org.apache.commons.lang3.text.WordUtils;

import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayingRoomController implements Initializable, ConnectionCallback {
    private HeartGame gameModel = new HeartGame();
    private Connector connector = new Connector(this);
    private StringProperty displayName = new SimpleStringProperty("Thánh Bài");
    private boolean inRoom = false;

    public void initialize(URL location, ResourceBundle resources) {
        setTheme("skin1");
        btnExitRoom.disableProperty().bind(btnOpenRoom.disabledProperty().isNotEqualTo(new SimpleBooleanProperty(true)));
        txtNameMe.textProperty().bind(displayName);
    }

    //Xử lý Các hành động do người dùng trực tiếp tác động lên view ---------------

    public void updateDisplayName() {
        displayName.set(txtDisplayName.getText());
        txtChatTextField.requestFocus();
        //To Do: Gửi tên mới tới các người chơi còn lại
    }

    public void openRoom() {
        setDisableCommand(true);
        connector.openListener();
    }

    public void joinRoom() {
        setDisableCommand(true);
        String connectionString = txtConnectionString.getText();
        connector.connectTo(connectionString);
    }

    public void startGame() {

    }

    public void toTheWest() {

    }

    public void toTheNorth() {

    }


    public void toTheEast() {

    }

    public void clickOnCard() {

    }

    public void submitChat() {
        String chatLine = txtChatTextField.getText();
        if (chatLine.length() > 0) {
            addChatLine(chatLine);
            txtChatTextField.setText("");

            //TEST : Sau này cần chuyển về chuẩn class Message, loại thông điệp là chat
            connector.sendToAll(displayName + ": " + chatLine);
        }
    }

    public void exitRoom() {
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

    public synchronized void onListenerOpenFailed() {
        setDisableCommand(false);
        addChatLine("-- Mở sòng không được.");
    }

    public synchronized void onListenerOpenSucceeded(String connectionString) {
        inRoom = true;
        txtConnectionString.setText(connectionString);
        addChatLine("-- Mở sòng Thành công, Gửi bạn bè Chuỗi kết nối phía trên để vào Sòng.");
    }

    public synchronized void onConnectionReceived(Socket socketToClient) {
        //TODO: Có người kết nối tới, Xem đã đủ người chơi chưa, hay game đã bắt đầu chưa, Nếu chưa thì Tạo player Và mở MessageReceiver ở connector tới người đó, Nếu rồi thì gửi thông báo cho người đó, rồi đóng kết nối.
        addChatLine("-- Một người chơi đã kết nối");
        connector.stopListen();
        try {
            socketToClient.getOutputStream().write(123);
        }
        catch (Exception e) {

        }
    }

    public synchronized void onConnectToServerSucceeded(Socket socketToServer) {
        addChatLine("-- Kết nối Thành công. Chờ Thông tin từ Chủ phòng");
        //TODO: Set lại view theo kiểu khách, không phải chủ phòng (Không có nút bắt đầu game)
    }

    public synchronized void onConnectToServerFailed() {
        addChatLine("-- Kết nối Không thành công!");
        connector.close();
        setDisableCommand(false);
    }

    public synchronized void onConnectionToAClientLost(Socket socketToClient) {
        if(inRoom) addChatLine("-- Một người chơi đã thoát");
        //TODO: Chuyển Player tại vị trí của người đó thành BOT, Đóng kết nối với người đó, Thông báo tới những Player khác.
    }

    public synchronized void onConnectionToServerLost(Socket socketToServer) {
        inRoom = false;
        addChatLine("-- Mất kết nối với Chủ phòng. Game kết thúc!");
        connector.close();
        //TODO: reset game, reset view
    }

    public synchronized void onMsgReceived(Object msg) {
        //TEST : Sau này cần xử lý, nếu là Message kiểu chat thì mới lấy dòng chat ra rồi add lên ChatBox
        addChatLine(msg.toString());
        //TODO: Xử lý khi nhận Thông điệp
    }

    //End Xử lý các Xử kiện ở các thread khác gửi qua ------------------------------



    //view controller API -----------------------------------------------------------

    public synchronized void addChatLine(String message) {
        lstChatView.getItems().add(WordUtils.wrap(message, 45));
        lstChatView.scrollTo(lstChatView.getItems().size() - 1);
    }

    public void setTheme(String themeName) {
        themeName = themeName.toLowerCase();
        switch (themeName) {
            case "skin1":
                rootSceneNode.getStylesheets().set(1, "styles/CardSkin1.css");
                break;
            case "skin2":
                rootSceneNode.getStylesheets().set(1, "styles/CardSkin2.css");
                break;
        }
    }

    public void showPopUpMessage(String message) {
    }

    private void resetView() {

    }

    private void setDisableCommand(boolean b) {
        btnOpenRoom.setDisable(b);
        txtConnectionString.setEditable(!b);
        btnJoinRoom.setDisable(b);
    }

    private void setNodeToGone(Node card) {
        card.getStyleClass().set(2, "invisible-img");
        card.setMouseTransparent(true);
    }

    private void setNodeToAppear(Node card) {
        card.getStyleClass().remove(2);
        card.setMouseTransparent(false);
    }

    private void setCardFace(Node node, String cardName) {
        node.getStyleClass().set(1, cardName);
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

    //End các control trên view cần can thiệp ---------------------------------------
}
