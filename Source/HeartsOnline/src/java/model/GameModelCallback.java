package model;

import model.player.Position;

public interface GameModelCallback {
    void onTrickDone(Position positionToEat);
    void onHandDone();
    void onMyCurHandPointChanged();
}
