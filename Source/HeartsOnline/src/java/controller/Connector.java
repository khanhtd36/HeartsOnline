package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector {
    public StringProperty connectionString = new SimpleStringProperty();
    private ServerSocket listener = null;
    private Socket[] clients = new Socket[4];

    public void openListener(int port) throws IOException {
        listener = new ServerSocket(port);
        connectionString.set(InetAddress.getLocalHost().getHostAddress() + ":" + listener.getLocalPort());

        Thread listenerThread = new Thread(() -> {
            try {
                listener.accept();
            } catch (Exception e) {

            }
        });

        listenerThread.start();
    }

    public void connectTo(String connectionString) throws Exception {
        String hostAddress = connectionString.split(":")[0];
        int port = Integer.parseInt(connectionString.split(":")[1]);
        clients[0] = new Socket(hostAddress, port);
        clients[0].wait(10000);
    }

    public void close() {
        try {
            if (listener != null) {
                listener.close();
                listener = null;
            } else {
                if (clients[0] != null) {
                    clients[0].close();
                    clients[0] = null;
                }
            }
        } catch (Exception e) {

        }
    }

    public String getConnectionString() {
        return connectionString.get();
    }
}
