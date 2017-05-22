package controller.connection;

import java.net.Socket;

public interface MessageReceiveCallback {
    void onMsgReceived(Object msg, Socket fromSocket);
    void onStreamClosed(Socket socket);
}
