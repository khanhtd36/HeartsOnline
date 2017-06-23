package connection;

import java.net.Socket;

public interface ListenCallback {
    void onConnectionReceived(Socket socketToClient);
}
