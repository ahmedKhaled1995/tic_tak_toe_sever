package com.iti.project;

import com.iti.project.Database.PlayerResource;
import com.iti.project.Server.GameServer;
import com.iti.project.UI.PlayerRow;
import com.iti.project.UI.ViewUpdater;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EntryPoint extends Application {

    private static Logger logger = LoggerFactory.getLogger(EntryPoint.class);

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private BorderPane pane;
    private TableView<PlayerRow> playersList;
    private Button startButton;
    private Button stopButton;
    private TextArea consoleOutputArea;

    private static GameServer gameServer;
    private static ViewUpdater viewUpdater;

    @Override
    public void init(){
        this.pane = new BorderPane();
        this.pane.setPadding(new Insets(16, 16, 16, 16));
        this.playersList = new TableView<>();
        this.playersList.setPadding(new Insets(0, 0, 10, 0));
        this.startButton = new Button("Start");
        this.stopButton = new Button("Stop");
        this.stopButton.setDisable(true);
        this.consoleOutputArea = new TextArea();
        this.consoleOutputArea.setEditable(false);
        this.consoleOutputArea.setEditable(false);
        this.consoleOutputArea.setPadding(new Insets(16, 0, 0 , 0));

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().add(startButton);
        vBox.getChildren().add(stopButton);
        vBox.setPadding(new Insets(20, 0, 0, 20));

        this.pane.setRight(vBox);
        this.pane.setCenter(playersList);
        this.pane.setBottom(consoleOutputArea);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // To ensure program terminates when exit icon (x) is pressed
        primaryStage.setOnCloseRequest((e)-> {
            System.exit(0);
        });

        viewUpdater = new ViewUpdater(playersList, consoleOutputArea);

        this.startButton.setOnAction((e)->{
            viewUpdater.resetConsole();
            startServer();
            startButton.setDisable(true);
            stopButton.setDisable(false);
            viewUpdater.fillTableWithData();
        });

        this.stopButton.setOnAction((e)->{
            stopServer();
            startButton.setDisable(false);
            stopButton.setDisable(true);
            viewUpdater.resetTable();
            viewUpdater.resetConsole();
        });

        Scene scene = new Scene(pane, WIDTH, HEIGHT);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // To configure the logger
        BasicConfigurator.configure();
        gameServer = new GameServer();
        launch(args);
    }

    public static ViewUpdater getViewUpdater(){
        return viewUpdater;
    }

    public static GameServer getGameServer(){
        return gameServer;
    }

    private void startServer(){
        gameServer.startServer();
    }

    private void stopServer(){
        gameServer.stopServer();
    }
}
