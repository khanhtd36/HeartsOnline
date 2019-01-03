package model;

import model.card.Card;
import model.player.Position;

public interface GameModelCallback {
    void onTrickDone(Position positionToEat);
    void onHandDone();
    void onMyCurHandPointChanged();
    void onABotPlayedACard(Position position, Card card);
    void onHeartBroken();
}
