package controller;

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

import java.net.URL;
import java.util.ResourceBundle;

public class PlayingRoomController implements Initializable {
    public Text txtNameWest, txtNameNorth, txtNameEast, txtNameMe;
    public Text txtCurRoundPoint;
    public ImageView cardWest01, cardWest02, cardWest03, cardWest04, cardWest05, cardWest06, cardWest07, cardWest08, cardWest09, cardWest10, cardWest11, cardWest12, cardWest13;
    public ImageView cardNorth01, cardNorth02, cardNorth03, cardNorth04, cardNorth05, cardNorth06, cardNorth07, cardNorth08, cardNorth09, cardNorth10, cardNorth11, cardNorth12, cardNorth13;

    //Xử lý Các hành động do người dùng trực tiếp tác động lên view ---------------
    public ImageView cardEast01, cardEast02, cardEast03, cardEast04, cardEast05, cardEast06, cardEast07, cardEast08, cardEast09, cardEast10, cardEast11, cardEast12, cardEast13;
    public ImageView cardMe01, cardMe02, cardMe03, cardMe04, cardMe05, cardMe06, cardMe07, cardMe08, cardMe09, cardMe10, cardMe11, cardMe12, cardMe13;
    public ImageView cardTrickWest, cardTrickNorth, cardTrickEast, cardTrickMe;
    public ImageView btnStart, leftArrow, rightArrow, upArrow;
    public ListView lstChatView;
    public TextField txtChatTextField;
    public AnchorPane rootSceneNode;
    public TextField txtDisplayName, txtPort, txtConnectionString;
    public Button btnOpenRoom, btnJoinRoom, btnExitRoom;
    private HeartGame gameModel = new HeartGame();
    private Connector connector = new Connector();
    private StringProperty displayName = new SimpleStringProperty("Thánh Bài");

    //End Xử lý Các hành động do người dùng trực tiếp tác động lên view ------------


    //


    //view controller API -----------------------------------------------------------

    public void initialize(URL location, ResourceBundle resources) {
        setTheme("skin1");
        btnExitRoom.disableProperty().bind(btnOpenRoom.disabledProperty().isNotEqualTo(new SimpleBooleanProperty(true)));
        txtNameMe.textProperty().bind(displayName);
    }

    public void updateDisplayName() {
        displayName.set(txtDisplayName.getText());
        txtChatTextField.requestFocus();
        //To Do: Gửi tên mới tới các người chơi còn lại
    }

    public void openRoom() {
        try {
            setDisableCommand(true);

            int port = Integer.parseInt(txtPort.getText());
            connector.openListener(port);

            addChatLine("-- Mở sòng Thành công, Gửi bạn bè Chuỗi kết nối phía trên để vào Sòng.");
            txtConnectionString.setText(connector.getConnectionString());
        } catch (Exception e) {
            addChatLine("-- Mở sòng Không thành công.");
            connector.close();

            setDisableCommand(false);
        }
    }

    public void joinRoom() {
        try {
            setDisableCommand(true);

            String connectionString = txtConnectionString.getText();
            connector.connectTo(connectionString);

            addChatLine("-- Bạn đã tham gia Sòng.");
        } catch (Exception e) {
            addChatLine("-- Không vào Sòng được.");
            connector.close();

            setDisableCommand(false);
        }
    }

    public void startGame() {

    }

    public void toTheWest() {

    }

    public void toTheNorth() {

    }

    //End View Controller API -------------------------------------------------------


    //Các control trên view cần can thiệp -------------------------------------------

    public void toTheEast() {

    }

    public void clickOnCard() {

    }

    public void submitChat() {
        if (txtChatTextField.getText().length() > 0) {
            addChatLine(txtChatTextField.getText());
            txtChatTextField.setText("");
        }
    }

    public void exitRoom() {
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

    private void resetView() {

    }

    private void setDisableCommand(boolean b) {
        txtPort.setDisable(b);
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

    //End các control trên view cần can thiệp ---------------------------------------
}
