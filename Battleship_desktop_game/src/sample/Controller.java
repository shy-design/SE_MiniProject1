package sample;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sample.Model.Ship;

import java.net.URISyntaxException;
import java.util.*;

public class Controller {

    private boolean nameIsPresent = false;

    private int playerTriesN = 0, playerMissesN = 0, playerStrikesN = 0, player = 0, enemy = 0;
    private Random random = new Random();
    private static Map<Ship, HBox> shipList = new HashMap<>();

    @FXML
    private HBox ship1;
    @FXML
    private HBox ship2;
    @FXML
    private HBox ship3;
    @FXML
    private HBox ship4;
    @FXML
    private HBox ship5;
    @FXML
    private GridPane gameBoard;

    @FXML
    private GridPane statisticsBoard;

    @FXML
    private TextArea messageBoard;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label boardLabel;

    @FXML
    private Label playerNameLabel;
    @FXML
    private Label playerTries;
    @FXML
    private Label playerStrikes;
    @FXML
    private Label playerMisses;
    @FXML
    private Label playerTriesScore;
    @FXML
    private Label playerStrikesScore;
    @FXML
    private Label playerMissesScore;
    @FXML
    private Label shipsLabel;

    @FXML
    private Button startGameBtn;
    @FXML
    private Button resetBtn;


    @FXML
    public void initialize() {

        messageBoard.clear();

        enemy = 0;
        player = 0;
        playerMissesN = 0;
        playerStrikesN = 0;
        playerTriesN = 0;

        if (!nameIsPresent) createPlayerName();
        StringBuilder sb = new StringBuilder();
        sb.append("\nWelcome to Battleship Game! \n\n");
        sb.append("Instruction:\n");
        sb.append("* You should destroy 5 ships *\n");
        sb.append("* Each ship has specific size which is shown on the left *\n");
        sb.append("* You have 30 tries to find and destroy all ships *\n\n");
        sb.append("Please, press START GAME to begin\n");
        messageBoard.setText(sb.toString());
        messageBoard.setEditable(false);

        startGameBtn.setDisable(false);
        resetBtn.setDisable(false);
        gameBoard.setDisable(true);
        playerTriesScore.setText(Integer.toString(playerTriesN));
        playerStrikesScore.setText(Integer.toString(playerStrikesN));
        playerMissesScore.setText(Integer.toString(playerMissesN));


    }

    private void addShips() {

        Ship destroyer = Ship.createShip("ship1", 1);
        Ship submarine = Ship.createShip("ship2", 2);
        Ship cruiser = Ship.createShip("ship3", 3);
        Ship battleship = Ship.createShip("ship4", 4);
        Ship carrier = Ship.createShip("ship5", 5);

        shipList = new HashMap<>();

        shipList.put(destroyer, ship1);
        shipList.put(submarine, ship2);
        shipList.put(cruiser, ship3);
        shipList.put(battleship, ship4);
        shipList.put(carrier, ship5);

    }


    @FXML
    private void onButtonClick(ActionEvent event) {

        if (event.getSource().equals(startGameBtn)) {

            createBoardWithShips();
            startGameBtn.setDisable(true);
        }
        if (event.getSource().equals(resetBtn)) {
            reset();
        }

    }

    // the ships will be added randomly to the game board
    private void createBoardWithShips() {

        gameBoard.setDisable(false);
        int x = random.nextInt(10);
        int y = random.nextInt(10);

        addShips();

        for (Ship ship : shipList.keySet()) {
            ship.setStartX(x);
            ship.setStartY(y);

            while (!placingShipsOnBoard(ship,gameBoard)) {
                x = random.nextInt(10);
                y = random.nextInt(10);
                ship.setStartX(x);
                ship.setStartY(y);
            }

        }

    }

    private boolean placingShipsOnBoard(Ship ship, GridPane board) {

        int index = ship.getSize();
        boolean isHorizontal;

        if (board == gameBoard) {

            int x = ship.getStartX();
            int y = ship.getStartY();
            isHorizontal = random.nextBoolean();

            if (checkSpace(x, y, ship.getSize(), isHorizontal, gameBoard)) {
                ship.setAdded(true);

                for (Node node : gameBoard.getChildren())
                    if (index > 0)
                        if ((GridPane.getColumnIndex(node) == y) && (GridPane.getRowIndex(node) == x)) {
                            node.setId(ship.getName());
                            createBoundaries(x, y, gameBoard);
                            index--;
                            if (isHorizontal)
                                y++;
                            else
                                x++;
                        }

                return true;
            }
            else {
                return false;
            }
        }
        return true;
    }

    @FXML
    private void hitShip(MouseEvent event) {

        Rectangle hitRectangle = (Rectangle) event.getPickResult().getIntersectedNode();
        int x = GridPane.getRowIndex(hitRectangle);
        int y = GridPane.getColumnIndex(hitRectangle);
        for (Node node : gameBoard.getChildren()) {
            if (GridPane.getRowIndex(node) == x & GridPane.getColumnIndex(node) == y) {
                String value = node.getId();
                if ((value != null) && !node.getId().equals("notPermitted")) {

                    ((Rectangle) node).setFill(Color.RED);

                    playerStrikesN++;
                    playerStrikesScore.setText(Integer.toString(playerStrikesN));
                    playerTriesN++;
                    playerTriesScore.setText(Integer.toString(playerTriesN));
                    messageBoard.appendText("\nPlayer->Wow, It was a strike!");
                    node.setId("notPermitted"); // the already pointed cell can not be pointed again

                }
                else if (((Rectangle) node).getFill().equals(Color.BLACK)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning. Cell is already clicked");
                    alert.setContentText("This cell was already clicked");

                }
                else if (((Rectangle) node).getFill().equals(Color.RED)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning. Cell is already clicked");
                    alert.setContentText("This cell was already clicked");
                }

                else {
                    ((Rectangle) node).setFill(Color.BLACK);

                    playerTriesN++;
                    playerTriesScore.setText(Integer.toString(playerTriesN));
                    playerMissesN++;
                    playerMissesScore.setText(Integer.toString(playerMissesN));
                    node.setId("notPermitted"); // the already pointed cell can not be pointed again
                    messageBoard.appendText("\nPlayer->Unfortunately, It was a miss...");

                }
                if (playerStrikesN == 15) {
                    gameBoard.setDisable(true);
                    player++;
                    showWinner(player);
                    return;
                }
                else if(playerTriesN == 31) {
                    gameBoard.setDisable(true);
                    enemy++;
                    showWinner(enemy);
                    return;
                }

            }
        }

    }

    private void showWinner(int point) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        if (point == enemy) {
            alert.setHeaderText("Enemy Won");
            alert.setContentText("The Game Has been finished.Better Luck Next Time.Replay?");
            messageBoard.setText("Unfortunately you lost the game.");

        } else {
            alert.setHeaderText("Player Won");
            alert.setContentText("Congratulation.You Won the Game!!! Replay?");
            messageBoard.setText("Great! You won the game.");
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            reset();
        } else {
            System.exit(-1);
        }
    }

    //check the board and the forbidden cells for a cell to be positioned

    private boolean checkSpace(int x, int y, int shipSize, boolean isHorizontal, GridPane board) {

        if (isHorizontal) {
            if ((y + shipSize) > 10)
                return false;
        } else {
            if ((x + shipSize) > 10)
                return false;
        }

        for (Node node : board.getChildren())
            if (isHorizontal) {
                for (int i = 0; i < shipSize; i++) {
                    if (GridPane.getRowIndex(node) == x && GridPane.getColumnIndex(node) == y + i) {
                        String value = node.getId();
                        if (value != null) {
                            return false;
                        }
                    }
                }
            } else {
                for (int i = 0; i < shipSize; i++) {
                    if (GridPane.getRowIndex(node) == x + i && GridPane.getColumnIndex(node) == y) {
                        String value = node.getId();
                        if (value != null) {
                            return false;
                        }
                    }
                }
            }
        return true;
    }

    // Creating the limit around each ship so that the new ship can not be added to its boundaries.

    private void createBoundaries(int x, int y, GridPane board) {

        createLimitation(x + 1, y, board);
        createLimitation(x - 1, y, board);
        createLimitation(x, y - 1, board);
        createLimitation(x, y + 1, board);
        createLimitation(x - 1, y - 1, board);
        createLimitation(x + 1, y + 1, board);
        createLimitation(x + 1, y - 1, board);
        createLimitation(x - 1, y + 1, board);

    }

    private void createLimitation(int x, int y, GridPane board) {

        for (Node node : board.getChildren()) {
            if ((GridPane.getRowIndex(node) == x && GridPane.getColumnIndex(node) == y)) {
                String value = node.getId();
                if ((value == null)) {
                    node.setId("notPermitted");
                }
            }
        }

    }

    private void reset() {

        initialize();

        for (Node node : gameBoard.getChildren()) {
            ((Rectangle) node).setFill(Color.WHITE);
        }
        for (Node node : gameBoard.getChildren()) {
            node.setId(null);
        }

    }

    private void createPlayerName() {
        TextInputDialog dialog = new TextInputDialog("Player Name");
        dialog.setTitle("Player Name");
        dialog.setHeaderText("Welcome to the Battleship game!!!");
        dialog.setContentText("Please enter your name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> playerNameLabel.setText(name.toUpperCase()));
        nameIsPresent = true;

    }

}

