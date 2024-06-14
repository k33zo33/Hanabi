package hr.k33zo.hanabi;

import hr.k33zo.hanabi.controller.GameController;
import hr.k33zo.hanabi.model.GameState;
import hr.k33zo.hanabi.model.RoleName;
import hr.k33zo.hanabi.networking.Country;
import hr.k33zo.hanabi.networking.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static hr.k33zo.hanabi.model.NetworkConfiguration.CLIENT_PORT;
import static hr.k33zo.hanabi.model.NetworkConfiguration.SERVER_PORT;

public class GameApplication extends Application {

    public static RoleName loggedInRoleName;
    public static GameController gameController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 750);
        gameController = fxmlLoader.getController();
        stage.setTitle(loggedInRoleName.name());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        String inputRoleName = args[0];

        for(RoleName rn : RoleName.values()) {
            if(rn.name().equals(inputRoleName)) {
                loggedInRoleName = rn;
                break;
            }
        }

        new Thread(Application::launch).start();

        if (loggedInRoleName == RoleName.SERVER) {
            acceptRequestsAsServer();
        }
        else if(loggedInRoleName == RoleName.CLIENT){
            acceptRequestsAsClient();
        }

    }

    private static void acceptRequestsAsServer() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
            System.err.printf("Server listening on port: %d%n", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.printf("Client connected from port %s%n", clientSocket.getPort());;
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void acceptRequestsAsClient() {
        try (ServerSocket serverSocket = new ServerSocket(CLIENT_PORT)){
            System.err.printf("Server listening on port: %d%n", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.printf("Client connected from port %s%n", clientSocket.getPort());;
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){
            GameState gameState = (GameState) ois.readObject();


            Platform.runLater(()->gameController.updateGameState(gameState));

            System.out.printf("Game state received");
            oos.writeObject("Game state received confirmation!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
//
//    private static void sendRequest() {
//        try (Socket clientSocket = new Socket(Server.HOST, Server.PORT)){
//            System.err.printf("Client is connecting to %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());
//
//            //sendPrimitiveRequest(clientSocket);
//            sendSerializableRequest(clientSocket);
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void sendSerializableRequest(Socket client) throws IOException, ClassNotFoundException {
//        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
//        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
//        oos.writeObject(new Country("Croatia", 1));
//        System.out.printf("Moved to: %s%n", ois.readObject());
//    }





}