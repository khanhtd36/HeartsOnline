package controller.networkMessage;

import java.io.Serializable;

public enum MessageType implements Serializable {
    JOIN_REQUEST,
    JOIN_ACCEPT,
    NEW_PLAYER_JOIN,
    A_PLAYER_EXIT,
    CHAT,
    UPDATE_NAME,
    RECEIVE_CARD_DESK,
    EXCHANGE_CARD,
    HEART_BROKEN,
    PLAY_CARD,
    TURN,
}
