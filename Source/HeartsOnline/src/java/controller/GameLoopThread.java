package controller;

import controller.networkMessage.CardDeskMsgContent;
import controller.networkMessage.Message;
import controller.networkMessage.MessageType;
import model.GameState;
import model.HeartGame;
import model.card.Card;
import model.player.Position;

import java.util.List;

public class GameLoopThread extends Thread {
    private ThreadMessage msgFromController = null;
    private HeartGame gameModel = null;
    private PlayingRoomController controller = null;

    public GameLoopThread(HeartGame gameModel, PlayingRoomController controller, ThreadMessage threadMessage) {
        this.gameModel = gameModel;
        this.controller = controller;
        this.msgFromController = threadMessage;
    }

    @Override
    public void run() {
        super.run();
        if(controller.isHost()) {
            startGameLoopInHost();
        }
        else {
            startGameLoopInClient();
        }
    }

    public void startGameLoopInHost() {
        //TODO: đổi điều kiện vòng lặp game lại cho mẫu mực hơn
//        while (true) {
            //Sốc bài
            gameModel.generateCard();
            List<Card> cardNames = gameModel.getCardDesk(gameModel.getMyPosition());
            //Phát bài trên view của mình
            controller.distributeCard(cardNames);

            //Phát bài cho các client
            for (int i = 1; i < 4; i++) {
                if (!gameModel.getPlayers().get(i).isBot()) {
                    Position position = Position.values()[i];
                    Message msg = new Message(MessageType.RECEIVE_CARD_DESK, new CardDeskMsgContent(gameModel.getCardDesk(position)));
                    controller.getConnector().sendMessageTo(msg, controller.getSocketByPosition(position));
                }
            }

            //Kiểm tra hand thứ mấy để đổi bài cho nhau
            controller.setExchangeCardButton(gameModel.getHand());
            if((gameModel.getHand() + 1) % 4 != 0) {
                gameModel.setGameState(GameState.EXCHANGING);
            }


//            synchronized (msgFromController) {
//                try {
//                    msgFromController.wait();
//                }
//                catch (Exception e) {
//                }
//            }

            //End Game Loop

//        }
    }

    public void startGameLoopInClient() {

    }
}
