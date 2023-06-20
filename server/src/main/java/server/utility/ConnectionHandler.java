package server.utility;

import common.interaction.Request;
import common.interaction.Response;
import common.interaction.ResponseCode;
import common.utility.Outputer;
import server.App;
import server.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Handles user connection.
 */
public class ConnectionHandler implements Runnable {
    private Server server;
    private Socket clientSocket;
    private CommandManager commandManager;
    private ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();


    public ConnectionHandler(Server server, Socket clientSocket, CommandManager commandManager) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.commandManager = commandManager;
    }

    /**
     * Main handling cycle.
     */
    @Override
    public void run() {
        Request userRequest;
        Response responseToUser;
        boolean stopFlag = false;
        try (ObjectInputStream clientReader = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream clientWriter = new ObjectOutputStream(clientSocket.getOutputStream())) {
            do {
                userRequest = (Request) clientReader.readObject();
                responseToUser = forkJoinPool.invoke(new HandleRequestTask(userRequest, commandManager));
                App.logger.info("Запрос '" + userRequest.getCommandName() + "' обработан.");
                Response finalResponseToUser = responseToUser;
                if (!cachedThreadPool.submit(() -> {
                    try {
                        clientWriter.writeObject(finalResponseToUser);
                        clientWriter.flush();
                        return true;
                    } catch (IOException exception) {
                        Outputer.printerror("An error occurred while sending data to the client!");
                        App.logger.error("An error occurred while sending data to the client!");
                    }
                    return false;
                }).get()) break;
            } while (responseToUser.getResponseCode() != ResponseCode.SERVER_EXIT &&
                    responseToUser.getResponseCode() != ResponseCode.CLIENT_EXIT);
            if (responseToUser.getResponseCode() == ResponseCode.SERVER_EXIT)
                stopFlag = true;
        } catch (ClassNotFoundException exception) {
            Outputer.printerror("An error occurred while reading received data!");
            App.logger.error("An error occurred while reading received data!");
        } catch (CancellationException | ExecutionException | InterruptedException exception) {
            Outputer.println("A multi-threading error occurred while processing the request!");
            App.logger.warn("A multi-threading error occurred while processing the request!");
        } catch (IOException exception) {
            Outputer.printerror("Unexpected connection termination with the client!");
            App.logger.warn("Unexpected connection termination with the client!");
        } finally {
            try {
                cachedThreadPool.shutdown();
                clientSocket.close();
                Outputer.println("Client disconnected from the server.");
                App.logger.info("Client disconnected from the server.");
            } catch (IOException exception) {
                Outputer.printerror("An error occurred while attempting to terminate the connection with the client!");
                App.logger.error("An error occurred while attempting to terminate the connection with the client!");
            }
            if (stopFlag) server.stop();
            server.releaseConnection();
        }
    }
}
