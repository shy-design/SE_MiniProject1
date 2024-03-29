package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import sample.Model.Ship;

import java.io.File;
import java.util.*;

public class Controller {

    private boolean nameIsPresent = false;
    //Setting the image of the cell to the ship/bomb
    Image map1 = new Image("sample/image/ship.jpg");
    ImagePattern pattern1 = new ImagePattern(map1);

    Image map2 = new Image("sample/image/bomb.jpg");
    ImagePattern pattern2 = new ImagePattern(map2);

    
    AudioClip audioClip = new AudioClip(this.getClass().getResource("./audio/audio1.mp3").toExternalForm());

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
    private Label playerTimer;
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
    private Rectangle r1c0,r2c0, r2c1,r3c0,r3c1,r3c2,r4c0, r4c1, r4c2, r4c3, r4c4, r5c0, r5c1, r5c2, r5c3, r5c4, r5c5;

    @FXML
    private int ship1Count = 0, ship2Count = 0, ship3Count = 0, ship4Count = 0, ship5Count = 0;



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
        sb.append("* Each ship has specific size which is shown to the left *\n");
        sb.append("* You have 50 tries to find and destroy all ships *\n\n");
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

        int index = ship.getSize();                                                                                     //ship size from ship class
        boolean isHorizontal;                                                                                           //boolean for vertical or horizontal

        if (board == gameBoard) {

            int x = ship.getStartX();                                                                                   //get horizontal position x for the ship
            int y = ship.getStartY();                                                                                   //get vertical position for the ship
            isHorizontal = random.nextBoolean();

            if (checkSpace(x, y, ship.getSize(), isHorizontal, gameBoard)) {
                ship.setAdded(true);                                                                                    //set variable to true - ship was added

                for (Node node : gameBoard.getChildren())                                                               //
                    if (index > 0)
                        if ((GridPane.getColumnIndex(node) == y) && (GridPane.getRowIndex(node) == x)) {
                            node.setId(ship.getName());                                                                 //setting node id to ship's name
                            //node.set
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
        if (audioClip.isPlaying()) audioClip.stop();

        int x = GridPane.getRowIndex(hitRectangle);
        int y = GridPane.getColumnIndex(hitRectangle);
        for (Node node : gameBoard.getChildren()) {
            if (GridPane.getRowIndex(node) == x & GridPane.getColumnIndex(node) == y) {
                String value = node.getId();
                System.out.println(((Rectangle) node).getFill());
                if ((value != null) && !node.getId().equals("notPermitted")) {

                    ((Rectangle) node).setFill(pattern1);

                    audioClip.play();

                    playerStrikesN++;
                    playerStrikesScore.setText(Integer.toString(playerStrikesN));
                    playerTriesN++;
                    playerTriesScore.setText(Integer.toString(playerTriesN));

                    //resetShips();
                    switch (node.getId()){
                        case "ship1":
                            r1c0.setFill(Color.RED);
                            ship1Count = 1;
                            break;

                        case "ship2":
                            r2c0.setFill(Color.RED);
                            ship2Count++;

                            if(ship2Count == 2){
                                r2c0.setFill(Color.RED);
                                r2c1.setFill(Color.RED);
                            }

                            break;
                        case "ship3":
                            r3c0.setFill(Color.RED);
                            ship3Count++;

                            if(ship3Count == 2){
                                r3c0.setFill(Color.RED);
                                r3c1.setFill(Color.RED);
                            } else if (ship3Count == 3) {
                                r3c0.setFill(Color.RED);
                                r3c1.setFill(Color.RED);
                                r3c2.setFill(Color.RED);
                            }

                            break;
                        case "ship4":
                            r4c0.setFill(Color.RED);
                            ship4Count++;

                            if (ship4Count ==2 ){
                                r4c0.setFill(Color.RED);
                                r4c1.setFill(Color.RED);

                            }else if(ship4Count == 3){
                                r4c0.setFill(Color.RED);
                                r4c1.setFill(Color.RED);
                                r4c2.setFill(Color.RED);

                            }else if (ship4Count == 4){
                                r4c0.setFill(Color.RED);
                                r4c1.setFill(Color.RED);
                                r4c2.setFill(Color.RED);
                                r4c3.setFill(Color.RED);
                            }
                            break;

                        case "ship5":
                            r5c0.setFill(Color.RED);
                            ship5Count++;

                            if (ship5Count == 2){
                                r5c0.setFill(Color.RED);
                                r5c1.setFill(Color.RED);

                            }else if (ship5Count == 3){
                                r5c0.setFill(Color.RED);
                                r5c1.setFill(Color.RED);
                                r5c2.setFill(Color.RED);

                            }else if (ship5Count == 4){
                                r5c0.setFill(Color.RED);
                                r5c1.setFill(Color.RED);
                                r5c2.setFill(Color.RED);
                                r5c3.setFill(Color.RED);

                            } else if (ship5Count == 5){
                                r5c0.setFill(Color.RED);
                                r5c1.setFill(Color.RED);
                                r5c2.setFill(Color.RED);
                                r5c3.setFill(Color.RED);
                                r5c4.setFill(Color.RED);

                            }

                            break;
                    }
                    //node.setId(null);
                    messageBoard.appendText("\nPlayer->Wow, It was a strike!");

                    node.setId("notPermitted"); // the already pointed cell can not be pointed again

                }
                else if (((Rectangle) node).getFill().equals(pattern1)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning. Cell is already clicked");
                    alert.setContentText("This cell was already clicked");
                    alert.show();


                }
                else if (((Rectangle) node).getFill().equals(pattern2)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning. Cell is already clicked");
                    alert.setContentText("This cell was already clicked");
                    alert.show();
                    node.setId(null);
                }

                else {

                    ((Rectangle) node).setFill(pattern2);

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
                else if(playerTriesN == 51) {
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
            alert.setContentText("The Game has been finished.Better Luck Next Time! Replay?");
            messageBoard.setText("Unfortunately you lost the game.");

        } else {
            alert.setHeaderText("Player Won");
            alert.setContentText("Congratulations! You Won the Game!!! Replay?");
            messageBoard.setText("Great! You won the game!");
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (audioClip.isPlaying())
                audioClip.stop();
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

        //Resetting the Ships
      resetShips();
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

    public void resetShips(){
        r1c0.setFill(Color.YELLOW);
        r2c0.setFill(Color.YELLOW);
        r2c1.setFill(Color.YELLOW);
        r3c0.setFill(Color.YELLOW);
        r3c1.setFill(Color.YELLOW);
        r3c2.setFill(Color.YELLOW);
        r4c0.setFill(Color.YELLOW);
        r4c1.setFill(Color.YELLOW);
        r4c2.setFill(Color.YELLOW);
        r4c3.setFill(Color.YELLOW);
        r5c0.setFill(Color.YELLOW);
        r5c1.setFill(Color.YELLOW);
        r5c2.setFill(Color.YELLOW);
        r5c3.setFill(Color.YELLOW);
        r5c4.setFill(Color.YELLOW);

        ship1Count = 0;
        ship2Count = 0;
        ship3Count = 0;
        ship4Count = 0;
        ship5Count = 0;
    }
}

