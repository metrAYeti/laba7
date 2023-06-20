package client;

import client.utility.AuthHandler;
import client.utility.UserHandler;
import common.exceptions.ConnectionErrorException;
import common.exceptions.NotInDeclaredLimitsException;
import common.interaction.Request;
import common.interaction.Response;
import common.interaction.ResponseCode;
import common.interaction.User;
import common.utility.Outputer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Runs the client.
 */
public class Client {
    private String host;
    private int port;
    private int reconnectionTimeout;
    private int reconnectionAttempts;
    private int maxReconnectionAttempts;
    private UserHandler userHandler;
    private SocketChannel socketChannel;
    private ObjectOutputStream serverWriter;
    private ObjectInputStream serverReader;
    private AuthHandler authHandler;
    private User user;

    public Client(String host, int port, int reconnectionTimeout, int maxReconnectionAttempts, UserHandler userHandler,
                  AuthHandler authHandler) {
        this.host = host;
        this.port = port;
        this.reconnectionTimeout = reconnectionTimeout;
        this.maxReconnectionAttempts = maxReconnectionAttempts;
        this.userHandler = userHandler;
        this.authHandler = authHandler;
    }

    /**
     * Begins client operation.
     */
    public void run() {
        try {
            while (true) {
                try {
                    connectToServer();
                    processAuthentication();
                    processRequestToServer();
                    break;
                } catch (ConnectionErrorException exception) {
                    if (reconnectionAttempts >= maxReconnectionAttempts) {
                        Outputer.printerror("Maximum connection attempts exceeded!");
                        break;
                    }
                    try {
                        Thread.sleep(reconnectionTimeout);
                    } catch (IllegalArgumentException timeoutException) {
                        Outputer.printerror("Connection timeout '" + reconnectionTimeout +
                                "' is out of possible range!");
                        Outputer.println("Reconnecting immediately.");
                    } catch (Exception timeoutException) {
                        Outputer.printerror("An error occurred while waiting for connection!");
                        Outputer.println("Reconnecting immediately.");
                    }
                }
                reconnectionAttempts++;
            }
            if (socketChannel != null) socketChannel.close();
            Outputer.println("Client work completed.");
        } catch (NotInDeclaredLimitsException exception) {
            Outputer.printerror("The client cannot be started!");
        } catch (IOException exception) {
            Outputer.printerror("An error occurred while trying to terminate the connection to the server!");
        }
    }

    /**
     * Connecting to server.
     */
    private void connectToServer() throws ConnectionErrorException, NotInDeclaredLimitsException {
        try {
            if (reconnectionAttempts >= 1) Outputer.println("Reconnecting to server...");
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            Outputer.println("Connected to server.");
            Outputer.println("Waiting for permission to exchange data...");
            serverWriter = new ObjectOutputStream(socketChannel.socket().getOutputStream());
            serverReader = new ObjectInputStream(socketChannel.socket().getInputStream());
            Outputer.println("Permission to exchange data received.");
        } catch (IllegalArgumentException exception) {
            Outputer.printerror("Server address entered incorrectly!");
            throw new NotInDeclaredLimitsException();
        } catch (IOException exception) {
            Outputer.printerror("An error occurred while connecting to the server!");
            throw new ConnectionErrorException();
        }
    }

    /**
     * Server request process.
     */
    private void processRequestToServer() {
        Request requestToServer = null;
        Response serverResponse = null;
        do {
            try {
                requestToServer = serverResponse != null ? userHandler.handle(serverResponse.getResponseCode(), user) :
                        userHandler.handle(null, user);
                if (requestToServer.isEmpty()) continue;
                serverWriter.writeObject(requestToServer);
                serverResponse = (Response) serverReader.readObject();
                Outputer.print(serverResponse.getResponseBody());
            } catch (InvalidClassException | NotSerializableException exception) {
                Outputer.printerror("An error occurred while sending data to the server!");
            } catch (ClassNotFoundException exception) {
                Outputer.printerror("An error occurred while reading received data!");
            } catch (IOException exception) {
                Outputer.printerror("The connection to the server has been terminated!");
                try {
                    connectToServer();
                } catch (ConnectionErrorException | NotInDeclaredLimitsException reconnectionException) {
                    if (requestToServer.getCommandName().equals("exit"))
                        Outputer.println("The command will not be registered on the server.");
                    else Outputer.println("Try repeating the command later.");
                }
            }
        } while (!requestToServer.getCommandName().equals("exit"));
    }

    /**
     * Handle process authentication.
     */
    private void processAuthentication() {
        Request requestToServer = null;
        Response serverResponse = null;
        do {
            try {
                requestToServer = authHandler.handle();
                if (requestToServer.isEmpty()) continue;
                serverWriter.writeObject(requestToServer);
                serverResponse = (Response) serverReader.readObject();
                Outputer.print(serverResponse.getResponseBody());
            } catch (InvalidClassException | NotSerializableException exception) {
                Outputer.printerror("An error occurred while sending data to the server!");
            } catch (ClassNotFoundException exception) {
                Outputer.printerror("An error occurred while reading received data!");
            } catch (IOException exception) {
                Outputer.printerror("The connection to the server has been terminated!");
                try {
                    connectToServer();
                } catch (ConnectionErrorException | NotInDeclaredLimitsException reconnectionException) {
                    Outputer.println("Try repeating authentication later.");
                }
            }
        } while (serverResponse == null || !serverResponse.getResponseCode().equals(ResponseCode.OK));
        user = requestToServer.getUser();
    }
}
