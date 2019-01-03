package connection.networkmessage.msgcontent;

import java.io.Serializable;

public class ChatMsgContent implements Serializable {
    String chatLine;

    public ChatMsgContent(String chatLine) {
        this.chatLine = chatLine;
    }

    public String getChatLine() {
        return chatLine;
    }
}
