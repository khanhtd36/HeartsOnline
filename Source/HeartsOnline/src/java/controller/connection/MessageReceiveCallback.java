package controller.connection;

import java.net.Socket;

public interface MessageReceiveCallback {
    void onMsgReceived(Object msg);
    void onStreamClosed(Socket socket);
}
