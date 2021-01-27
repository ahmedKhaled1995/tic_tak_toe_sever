package com.iti.project;

import com.iti.project.Database.PlayerResource;
import com.iti.project.Server.GameServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EntryPoint extends Application {

    private static Logger logger = LoggerFactory.getLogger(EntryPoint.class);

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private BorderPane pane;
    private TableView<PlayerResource> playersList;
    private Button startButton;
    private Button stopButton;
    private TextArea consoleOutputArea;

    private static GameServer gameServer;

    @Override
    public void init(){
        this.pane = new BorderPane();
        this.pane.setPadding(new Insets(16, 16, 16, 16));
        this.playersList = new TableView<>();
        this.playersList.setPadding(new Insets(0, 0, 10, 0));
        this.startButton = new Button("Start");
        this.stopButton = new Button("Stop");
        this.consoleOutputArea = new TextArea();
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

        this.startButton.setOnAction((e)->{
            startServer();
        });

        this.stopButton.setOnAction((e)->{
            stopServer();
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

    private void startServer(){
        gameServer.startServer();
    }

    private void stopServer(){
        gameServer.stopServer();
    }
}
