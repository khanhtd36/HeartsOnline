package connection;

import java.net.Socket;

public interface ConnectionCallback {
    void onListenerOpenFailed();
    void onListenerOpenSucceeded(String connectionString);

    void onConnectionReceived(Socket socketToClient);

    void onConnectToServerSucceeded(Socket socketToServer);
    void onConnectToServerFailed();

    void onConnectionToAClientLost(Socket socketToClient);
    void onConnectionToServerLost(Socket socketToServer);

    void onMsgReceived(Object msg, Socket fromSocket);
}
