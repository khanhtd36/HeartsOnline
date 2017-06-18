package model;

import java.io.Serializable;

public enum GameState implements Serializable {
    NEW,
    EXCHANGING,
    OTHER_EXCHANGING,
    SOUNTH_GO,
    WEST_GO,
    NORTH_GO,
    EAST_GO,
    SHOWING_RESULT,
}
