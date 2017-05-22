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
    private Socket socketToServer = null;
    private Map<Socket, MessageReceiver> messageReceiver = new HashMap<>();
    private Map<Socket, ObjectOutputStream> outputStreams = new HashMap<>();


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
        if(socketToServer != null) return;
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

    public synchronized void close() {
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

    public void sendMessageTo(Object msg, Socket... sockets) {
        //TEST
        System.out.println("Connector - sendMessageTo A Specific Person");
        for(Socket socket : sockets) {
            try {
                outputStreams.get(socket).writeObject(msg);
            } catch (Exception e) {
                //TEST
                System.out.println("Connector.sendMessageTo - catch exception while writing msg to output stream");
                if (socket != socketToServer) {
                    //TEST
                    System.out.println("Connector.sendMessageTo - calling lost connection to client callback");
                    connectionCallback.onConnectionToAClientLost(socket);
                } else {
                    //TEST
                    System.out.println("Connector.sendMessageTo - calling lost connection to server callback");
                    connectionCallback.onConnectionToServerLost(socket);
                }
            }
        }
    }

    public void sendMessageToAllExcept(Object msg, Socket socket) {
        //TEST
        System.out.println("Connector - sendMessageToAllExcept called");
        if(socketToServer != null) {
            if(socketToServer != socket) {
                try {
                    //TEST
                    System.out.println("Connector - sendMessageToAllExcept - send to Server");
                    outputStreams.get(socketToServer).writeObject(msg);
                } catch (Exception e) {
                    //TEST
                    System.out.println("Connector - sendMessageToAll - send to server catch an exception");
                    System.out.println("Connector - calling onConnectionToServerLost callback");
                    connectionCallback.onConnectionToServerLost(socketToServer);
                }
            }
        }
        else {
            for (Socket client : clients) {
                if(!client.equals(socket)) {
                    try {
                        //TEST
                        System.out.println("Connector.sendMessageToAllExcept - send to a Client");
                        outputStreams.get(client).writeObject(msg);
                    } catch (Exception e) {
                        //TEST
                        System.out.println("Connector.sendMessageToAllExcept - send to Client catch an exception");
                        System.out.println("Connector - calling onConnectionToAClientLost callback");
                        connectionCallback.onConnectionToAClientLost(client);
                    }
                }
            }
        }
    }

    public void sendMessageToAll(Object msg) {
        //TEST
        System.out.println("Connector - sendMessageToAll called");
        if(socketToServer != null) {
            try {
                //TEST
                System.out.println("Connector - sendMessageToAll - send to Server");
                outputStreams.get(socketToServer).writeObject(msg);
            }
            catch (Exception e) {
                //TEST
                System.out.println("Connector - sendMessageToAll - send to server catch an exception");
                System.out.println("Connector - calling onConnectionToServerLost callback");
                connectionCallback.onConnectionToServerLost(socketToServer);
            }
        }
        else {
            for (Socket client : clients) {
                try {
                    //TEST
                    System.out.println("Connector.sendMessageToAll - send to a Client");
                    outputStreams.get(client).writeObject(msg);
                }
                catch (Exception e) {
                    //TEST
                    System.out.println("Connector.sendMessageToAll - send to Client catch an exception");
                    System.out.println("Connector - calling onConnectionToAClientLost callback");
                    connectionCallback.onConnectionToAClientLost(client);
                }
            }
        }
    }


    //Xử lý sự kiện ở Các Listener -------------------------------------

    public void onConnectionReceived(Socket socketToClient) {
        //TEST
        System.out.println("Connector - onConnectionReceived called");
        try {
            clients.add(socketToClient);
            outputStreams.put(socketToClient, new ObjectOutputStream(socketToClient.getOutputStream()));
            //TEST
            System.out.println("Connector - onConnectionReceived, output to client opened");
            MessageReceiver msgReceiver = new MessageReceiver(this, socketToClient);
            messageReceiver.put(socketToClient, msgReceiver);
            messageReceiver.get(socketToClient).start();
            //TEST
            System.out.println("Connector - onConnectionReceived, inputListener from client started");

            //TEST
            System.out.println("Connector - onConnectionReceived, calling onConnectionReceived callback");
            connectionCallback.onConnectionReceived(socketToClient);
        } catch (Exception e) {
            //TEST
            System.out.println("Connector - onConnectionReceived catch an exception");
            clients.remove(socketToClient);
            messageReceiver.remove(socketToClient);
            outputStreams.remove(socketToClient);
        }
    }

    public void onMsgReceived(Object msg, Socket fromSocket) {
        //TEST
        System.out.println("Connector - onMsgReceived called");
        System.out.println("Connector - calling onMsgReceived callback");
        connectionCallback.onMsgReceived(msg, fromSocket);
    }

    public void onStreamClosed(Socket socket) {
        //TEST
        System.out.println("Connector - onStreamClosed called");
        if (socket != socketToServer) {
            //TEST
            System.out.println("Connector - calling lost client connection callback");
            connectionCallback.onConnectionToAClientLost(socket);
        } else {
            //TEST
            System.out.println("Connector - calling lost server connection callback");
            connectionCallback.onConnectionToServerLost(socket);
        }
    }

    //End Xử lý sự kiện ở Các Listener ---------------------------------
}

//Class này chỉ để Lắng nghe kết nối
class Listener extends Thread {
    private ServerSocket listener = null;
    private Thread thread;

    private ListenCallback listenCallback;

    public void run() {
        try {
            if (listener == null) {
                listener = new ServerSocket(0);
            }
            while (true) {
                //TEST
                System.out.println("Listener - waiting at listener.Accept()");
                Socket socket = listener.accept();
                //Gọi callback nhận được kết nối.
                //TEST
                System.out.println("Listener - connection received, calling callback");
                Thread callbackThread = new Thread(()->listenCallback.onConnectionReceived(socket));
                callbackThread.start();
            }
        } catch (Exception e) {
            //TEST
            System.out.println("Listener - catch an exception");
            System.out.println("Listener - thread ended");
        }
    }

    @Override
    public synchronized void start() {
        //TEST
        System.out.println("Listener - start thread");
        thread = new Thread(this, "Listener");
        thread.start();
    }

    public synchronized void stopListen() {
        //TEST
        System.out.println("Listener - close listener SocketServer");
        try {
            listener.close();
        }
        catch (Exception e) {

        }
    }

    public synchronized void resumeListen() {
        //TEST
        System.out.println("Listener - create new thread to resume listen");
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
        //TEST
        System.out.println("MessageReceiver - opening inputStream");
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
            //TEST
            System.out.println("MessageReceiver - inputStream opened");
        }
        catch (Exception e) {
            Thread callBackThread = new Thread(()->msgCallback.onStreamClosed(socket));
            //TEST
            System.out.println("MessageReceiver - open inputStream failed, calling onStreamClosedCallback");
            callBackThread.start();
            //TEST
            System.out.println("MessageReceiver Thread ended");
            return;
        }

        while (true) {
            try {
                //TEST
                System.out.println("MessageReceiver - waiting for message");
                Object msg = inputStream.readObject();
                //TEST
                System.out.println("MessageReceiver - message received, calling callback");
                Thread callBackThread = new Thread(()-> msgCallback.onMsgReceived(msg, socket));
                callBackThread.start();
            } catch (Exception e) {
                //TEST
                System.out.println("MessageReceiver - exception at inputStream.readObject, calling callback " + e.toString());
                Thread callBackThread = new Thread(()->msgCallback.onStreamClosed(socket));
                callBackThread.start();
                break;
            }
        }
        //TEST
        System.out.println("MessageReceiver Thread ended");
    }

    @Override
    public synchronized void start() {
        //TEST
        System.out.println("MessageReceiver.start");
        thread = new Thread(this, "message Receiver");
        thread.start();
    }
}
