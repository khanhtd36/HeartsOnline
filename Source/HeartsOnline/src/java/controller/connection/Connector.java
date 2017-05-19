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
    private String connectionString = "127.0.0.1:4444";
    private Listener listenerThread = null;
    private ServerSocket listener = null;
    private ArrayList<Socket> clients = new ArrayList<>();
    private Map<Socket, MessageReceiver> messageReceiver = new HashMap<>();
    private Map<Socket, ObjectOutputStream> outputStreams = new HashMap<>();
    private Socket socketToServer = null;

    private ConnectionCallback connectionCallback;

    public Connector() {
    }

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

            connectionCallback.onListenerOpenSucceeded(connectionString);
        } catch (Exception e) {
            connectionCallback.onListenerOpenFailed();
        }
    }

    public void stopListen() {
        //Hàm này để listener dừng lại không accept thêm Kết nối nữa, chứ không đóng server lại.
        listenerThread.stopListen();
    }

    public void resumeListen() {
        listenerThread.resumeListen();
    }

    public synchronized void connectTo(String connectionString) {
        try {
            String hostAddress = connectionString.split(":")[0];
            int port = Integer.parseInt(connectionString.split(":")[1]);

            socketToServer = new Socket(hostAddress, port);
            outputStreams.put(socketToServer, new ObjectOutputStream(socketToServer.getOutputStream()));
            MessageReceiver msgReceiver = new MessageReceiver(this, socketToServer);
            messageReceiver.put(socketToServer, msgReceiver);
            messageReceiver.get(socketToServer).start(); //Nếu là client kết nối tới server thì mở thread lắng nghe Thông điệp từ server gửi tới ngay.

            connectionCallback.onConnectToServerSucceeded(socketToServer);
        } catch (Exception e) {
            socketToServer = null;
            messageReceiver.remove(socketToServer);
            outputStreams.remove(socketToServer);
            connectionCallback.onConnectToServerFailed();
        }
    }

    public void close() {
        try {
            if (listenerThread != null) {
                listenerThread.stopListen();
            }
            if (listener != null) {
                for (Socket client : clients) {
                    client.shutdownOutput();
                    client.shutdownInput();
                    client.close();
                }
                listener.close();
            }
            if (socketToServer != null) {
                socketToServer.shutdownInput();
                socketToServer.shutdownOutput();
                socketToServer.close();
            }
        } catch (Exception e) {

        } finally {
            clients.clear();
            messageReceiver.clear();
            outputStreams.clear();
            listenerThread = null;
            listener = null;
            socketToServer = null;
        }
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionCallback(ConnectionCallback callback) {
        this.connectionCallback = callback;
    }

    public void openMessageReceiver(Socket socket) {
        messageReceiver.get(socket).start();
    }

    public void sendMessageTo(Object msg, Socket socket) {
        try {
            outputStreams.get(socket).writeObject(msg);
        } catch (Exception e) {
            if (socket != socketToServer) {
                connectionCallback.onConnectionToAClientLost(socket);
            } else {
                connectionCallback.onConnectionToServerLost(socket);
            }
        }
    }

    public void sendToAll(Object msg) {
        if(socketToServer != null) {
            try {
                outputStreams.get(socketToServer).writeObject(msg);
            }
            catch (Exception e) {
                connectionCallback.onConnectionToServerLost(socketToServer);
            }
        }
        else {
            for (Socket client : clients) {
                try {
                    outputStreams.get(client).writeObject(msg);
                }
                catch (Exception e) {
                    connectionCallback.onConnectionToAClientLost(client);
                }
            }
        }
    }


    //Xử lý sự kiện ở Các Listener -------------------------------------

    public void onConnectionReceived(Socket socketToClient) {
        try {
            clients.add(socketToClient);
            outputStreams.put(socketToClient, new ObjectOutputStream(socketToClient.getOutputStream()));

            connectionCallback.onConnectionReceived(socketToClient);
        } catch (Exception e) {
            clients.remove(socketToClient);
            messageReceiver.remove(socketToClient);
            outputStreams.remove(socketToClient);
        }
    }

    public void onMsgReceived(Object msg) {
        connectionCallback.onMsgReceived(msg);
    }

    public void onStreamClosed(Socket socket) {
        if (socket != socketToServer) {
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
    private Thread thread;
    private boolean running = true;

    private ListenCallback listenCallback;

    public void run() {
        try {
            if (listener == null) {
                listener = new ServerSocket(0);
            }
            while (running) {
                Socket socket = listener.accept();
                //Gọi callback nhận được kết nối.
                Thread callbackThread = new Thread(()->listenCallback.onConnectionReceived(socket));
                callbackThread.start();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public synchronized void start() {
        thread = new Thread(this, "Listener");
        thread.start();
    }

    public synchronized void stopListen() {
        running = false;
    }

    public synchronized void resumeListen() {
        thread = new Thread(this, "Listener");
        thread.start();
    }

    public void setListener(ServerSocket listener) {
        this.listener = listener;
    }

    public void setListenCallback(ListenCallback callback) {
        this.listenCallback = callback;
    }
}

//Class này chỉ để nhận message
class MessageReceiver extends Thread {
    private Socket socket;
    private ObjectInputStream inputStream;
    private Thread thread;
    private MessageReceiveCallback msgCallback;

    public MessageReceiver(MessageReceiveCallback msgCallback, Socket socket) {
        this.msgCallback = msgCallback;
        this.socket = socket;
    }

    public void run() {
        while (true) {
            try {
                inputStream = new ObjectInputStream(socket.getInputStream());
                Object msg = inputStream.readObject();
                Thread callBackThread = new Thread(()-> msgCallback.onMsgReceived(msg));
                callBackThread.start();
            } catch (Exception e) {
                Thread callBackThread = new Thread(()->msgCallback.onStreamClosed(socket));
                callBackThread.start();
                return;
            }
        }
    }

    @Override
    public synchronized void start() {
        thread = new Thread(this, "Message Receiver");
        thread.start();
    }
}
