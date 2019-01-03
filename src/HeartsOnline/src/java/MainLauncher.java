import controller.PlayingRoomController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainLauncher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/PlayingRoom.fxml"));
        Parent root = loader.load();
        PlayingRoomController controller = loader.getController();
        controller.setStageAndSetupListens(primaryStage);
        primaryStage.setTitle("Hearts Casino");
        primaryStage.getIcons().add(new Image("images/icon.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
