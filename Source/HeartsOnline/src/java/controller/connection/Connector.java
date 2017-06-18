package controller.connection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Connector implements ListenCallback, MessageReceiveCallback {
    private String connectionString = "";
    private Listener listenerThread = null;
    private ServerSocket listener = null;
    private ArrayList<Socket> sockets = new ArrayList<>();

    private Map<Socket, MessageReceiver> messageReceivers = new HashMap<>();
    private Map<Socket, ObjectOutputStream> outputStreams = new HashMap<>();

    private ConnectionCallback connectionCallback;

    public Connector(ConnectionCallback callback) {
        this.connectionCallback = callback;
    }

    public synchronized void openListener() {
        try {
            listener = new ServerSocket(0);
            connectionString = InetAddress.getLocalHost().getHostAddress() + ":" + listener.getLocalPort();

            listenerThread = new Listener();
            listenerThread.setListener(listener);
            listenerThread.setListenCallback(this);
            listenerThread.start();

            Thread thread = new Thread(() -> connectionCallback.onListenerOpenSucceeded(connectionString));
            thread.start();
        } catch (Exception e) {
            connectionCallback.onListenerOpenFailed();
        }
    }

    public void stopListen() {
        listenerThread.stopListen();
    }

    public synchronized void connectTo(String connectionString) {
        try {
            listener = null;
            String hostAddress = connectionString.split(":")[0];
            int port = Integer.parseInt(connectionString.split(":")[1]);

            Socket socket = new Socket(hostAddress, port);
            sockets.add(socket);
            outputStreams.put(socket, new ObjectOutputStream(socket.getOutputStream()));
            MessageReceiver msgReceiver = new MessageReceiver(this, socket);
            messageReceivers.put(socket, msgReceiver);
            messageReceivers.get(socket).start();

            Thread thread = new Thread(() -> connectionCallback.onConnectToServerSucceeded(socket));
            thread.start();
        } catch (Exception e) {
            Thread thread = new Thread(() -> connectionCallback.onConnectToServerFailed());
            thread.start();
        }
    }

    public synchronized void close() {
        try {
            if (listenerThread != null) {
                listenerThread.stopListen();
            }
            if (listener != null) {
                listener.close();
            }
            for (Socket socket : sockets) {
                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
            }
        } catch (Exception e) {

        } finally {
            sockets.clear();
            messageReceivers.clear();
            outputStreams.clear();
            listenerThread = null;
            listener = null;
        }
    }

    public void shutdownConnectionTo(Socket socket) {
        try {
            outputStreams.get(socket).close();
            socket.close();
        } catch (Exception e) {

        } finally {
            outputStreams.remove(socket);
            messageReceivers.remove(socket);
            sockets.remove(socket);
        }
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionCallback(ConnectionCallback callback) {
        this.connectionCallback = callback;
    }

    public void sendMessageTo(Object msg, Socket... sockets) {
        for (Socket socket : sockets) {
            if (socket != null) {
                try {
                    outputStreams.get(socket).writeObject(msg);
                } catch (Exception e) {
                    if (listener != null) {
                        connectionCallback.onConnectionToAClientLost(socket);
                    } else {
                        connectionCallback.onConnectionToServerLost(socket);
                    }
                }
            }
        }
    }

    public void sendMessageToAllExcept(Object msg, Socket socket) {
        for (Socket socketToSend : sockets) {
            try {
                if (!socketToSend.equals(socket) && socket != null) {
                    outputStreams.get(socketToSend).writeObject(msg);
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    public void sendMessageToAll(Object msg) {
        for (Socket socketToSend : sockets) {
            try {
                if (socketToSend != null) {
                    outputStreams.get(socketToSend).writeObject(msg);
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    //Xử lý sự kiện ở Các Listener -------------------------------------

    public void onConnectionReceived(Socket socketToClient) {
        try {
            sockets.add(socketToClient);
            outputStreams.put(socketToClient, new ObjectOutputStream(socketToClient.getOutputStream()));
            MessageReceiver msgReceiver = new MessageReceiver(this, socketToClient);
            messageReceivers.put(socketToClient, msgReceiver);
            messageReceivers.get(socketToClient).start();

            Thread thread = new Thread(() -> connectionCallback.onConnectionReceived(socketToClient));
            thread.start();
        } catch (Exception e) {
            sockets.remove(socketToClient);
            messageReceivers.remove(socketToClient);
            outputStreams.remove(socketToClient);
        }
    }

    public void onMsgReceived(Object msg, Socket fromSocket) {
        Thread thread = new Thread(() -> connectionCallback.onMsgReceived(msg, fromSocket));
        thread.start();
    }

    public void onStreamClosed(Socket socket) {
        if (listener != null) {
            connectionCallback.onConnectionToAClientLost(socket);
        } else {
            connectionCallback.onConnectionToServerLost(socket);
        }
    }

    //End Xử lý sự kiện ở Các Listener ---------------------------------
}

//Class này chỉ để Lắng nghe kết nối
class Listener extends Thread {
    private ServerSocket listener = null;

    private ListenCallback listenCallback;

    public void run() {
        try {
            if (listener == null) {
                listener = new ServerSocket(0);
            }
            while (true) {
                Socket socket = listener.accept();
                Thread callbackThread = new Thread(() -> listenCallback.onConnectionReceived(socket));
                callbackThread.start();
            }
        } catch (Exception e) {
        }
    }

    public synchronized void stopListen() {
        try {
            listener.close();
        } catch (Exception e) {

        }
    }

    public void setListener(ServerSocket listener) {
        this.listener = listener;
    }

    public void setListenCallback(ListenCallback callback) {
        this.listenCallback = callback;
    }
}

//Class này chỉ để nhận networkMessage
class MessageReceiver extends Thread {
    private Socket socket;
    private ObjectInputStream inputStream;
    private MessageReceiveCallback msgCallback;

    public MessageReceiver(MessageReceiveCallback msgCallback, Socket socket) {
        this.msgCallback = msgCallback;
        this.socket = socket;
    }

    public void run() {
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            Thread callBackThread = new Thread(() -> msgCallback.onStreamClosed(socket));
            callBackThread.start();
            return;
        }

        while (true) {
            try {
                Object msg = inputStream.readObject();
                Thread callBackThread = new Thread(() -> msgCallback.onMsgReceived(msg, socket));
                callBackThread.start();
            } catch (Exception e) {
                Thread callBackThread = new Thread(() -> msgCallback.onStreamClosed(socket));
                callBackThread.start();
                break;
            }
        }
    }
}
