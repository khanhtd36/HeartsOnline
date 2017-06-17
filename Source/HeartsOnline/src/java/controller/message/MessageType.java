package controller.message;

import java.io.Serializable;

public enum MessageType implements Serializable {
    JOIN_REQUEST,
    JOIN_ACCEPT,
    NEW_PLAYER_JOIN,
    A_PLAYER_EXIT,
    CHAT,
    UPDATE_NAME,
}
