package model;

import java.io.Serializable;

public enum GameState implements Serializable {
    NEW,
    EXCHANGING,
    PLAYING,
    WAITING,
    SHOWING_RESULT,
}
