package com.iti.project.Server;

import com.iti.project.EntryPoint;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameServer {

    public static final int PORT = 5000;
    private final List<GameHandler> handlers = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(GameServer.class);
    private AtomicBoolean  running = new AtomicBoolean(false);

    private Thread listeningToClientsThread;

    private ServerSocket serverSocket;

    public void startServer(){
        try {
            this.serverSocket = new ServerSocket(PORT);
            // Running the thread that will accept clients
            this.listeningToClientsThread = new Thread(()->{
                this.running.set(true);
                logger.info("Server Started on PORT {}", PORT);
                logger.info("Waiting for clients...");
                EntryPoint.getViewUpdater().updateConsole(logger.getName() + ": Server Started on PORT " + PORT);
                EntryPoint.getViewUpdater().updateConsole(logger.getName() + ": Waiting for clients...");
                while(this.running.get()) {
                    Socket s = null;
                    try {
                        s = serverSocket.accept();
                        GameHandler handler = new GameHandler(s);
                        handlers.add(handler);
                        logger.info("Client accepted");
                        EntryPoint.getViewUpdater().updateConsole(logger.getName() + ": Client accepted");
                    } catch (IOException e) {
                        //e.printStackTrace();
                        logger.info("Closing server...");
                        logger.info("Server closed");
                        EntryPoint.getViewUpdater().updateConsole(logger.getName() + ": Closing server...");
                        EntryPoint.getViewUpdater().updateConsole(logger.getName() + ": Server closed");
                    }
                }
            });
            this.listeningToClientsThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void stopServer(){
        try {
            this.serverSocket.close();
            this.running.set(false);
            for(GameHandler handler : handlers){
                handler.closeConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getUsers(){
        return GameHandler.getUsers();
    }
}