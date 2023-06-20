package server;

import common.exceptions.ClosingSocketException;
import common.exceptions.ConnectionErrorException;
import common.exceptions.OpeningServerSocketException;
import common.utility.Outputer;
import server.utility.CommandManager;
import server.utility.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Runs the server.
 */
public class Server {
    private int port;
    private ServerSocket serverSocket;
    private CommandManager commandManager;
    private boolean isStopped;
    private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    private Semaphore semaphore;

    public Server(int port, int maxClients, CommandManager commandManager) {
        this.port = port;
        this.commandManager = commandManager;
        this.semaphore = new Semaphore(maxClients);
    }

    /**
     * Begins server operation.
     */
    public void run() {
        try {
            openServerSocket();
            while (!isStopped()) {
                try {
                    acquireConnection();
                    if (isStopped()) throw new ConnectionErrorException();
                    Socket clientSocket = connectToClient();
                    cachedThreadPool.submit(new ConnectionHandler(this, clientSocket, commandManager));
                } catch (ConnectionErrorException exception) {
                    if (!isStopped()) {
                        Outputer.printerror("Error occurred while connecting to the client!");
                        App.logger.error("Error occurred while connecting to the client!");
                    } else break;
                }
            }
            cachedThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            Outputer.println("Server work is complete.");
        } catch (OpeningServerSocketException exception) {
            Outputer.printerror("Server cannot be started!");
            App.logger.fatal("Server cannot be started!");
        } catch (InterruptedException e) {
            Outputer.printerror("An error occurred while ending the work with already connected clients!");
        }
    }

    /**
     * Acquire connection.
     */
    public void acquireConnection() {
        try {
            semaphore.acquire();
            App.logger.info("Permission for a new connection received.");
        } catch (InterruptedException exception) {
            Outputer.printerror("An error occurred while acquiring permission for a new connection!");
            App.logger.error("An error occurred while acquiring permission for a new connection!");
        }
    }

    /**
     * Release connection.
     */
    public void releaseConnection() {
        semaphore.release();
        App.logger.info("Connection break detected.");
    }

    /**
     * Finishes server operation.
     */
    public synchronized void stop() {
        try {
            App.logger.info("Stopping the server...");
            if (serverSocket == null) throw new ClosingSocketException();
            isStopped = true;
            cachedThreadPool.shutdown();
            serverSocket.close();
            Outputer.println("Ending work with already connected clients...");
            App.logger.info("Server work is complete.");
        } catch (ClosingSocketException exception) {
            Outputer.printerror("Cannot stop a server that hasn't been started yet!");
            App.logger.error("Cannot stop a server that hasn't been started yet!");
        } catch (IOException exception) {
            Outputer.printerror("An error occurred while ending server operation!");
            Outputer.println("Ending work with already connected clients...");
            App.logger.error("An error occurred while ending server operation!");
        }
    }

    /**
     * Checked stops of server.
     *
     * @return Status of server stop.
     */
    private synchronized boolean isStopped() {
        return isStopped;
    }

    /**
     * Open server socket.
     */
    private void openServerSocket() throws OpeningServerSocketException {
        try {
            App.logger.info("Starting the server...");
            serverSocket = new ServerSocket(port);
            App.logger.info("Server is running.");
        } catch (IllegalArgumentException exception) {
            Outputer.printerror("The port '" + port + "' is out of possible range!");
            App.logger.fatal("The port '" + port + "' is out of possible range!");
            throw new OpeningServerSocketException();
        } catch (IOException exception) {
            Outputer.printerror("An error occurred while trying to use the port '" + port + "'!");
            App.logger.fatal("An error occurred while trying to use the port '" + port + "'!");
            throw new OpeningServerSocketException();
        }
    }

    /**
     * Connecting to client.
     */
    private Socket connectToClient() throws ConnectionErrorException {
        try {
            Outputer.println("Listening on port '" + port + "'...");
            App.logger.info("Listening on port '" + port + "'...");
            Socket clientSocket = serverSocket.accept();
            Outputer.println("Connection with the client established.");
            App.logger.info("Connection with the client established.");
            return clientSocket;
        } catch (IOException exception) {
            throw new ConnectionErrorException();
        }
    }
}
