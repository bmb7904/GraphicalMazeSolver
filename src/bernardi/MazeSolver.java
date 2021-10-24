/**
 * A JavaFX application that will solve mazes.
 * Each maze is imported as a black and white .bpm or .png file ana
 * converted into a char[][] into memory.
 *
 *
 * The rules for the maze are :
 *  1.) Each wall or path must be exactly one pixel wide in the incoming image
 *  2.) # of pixel rows = # of pixel columns (square)
 *  3.) There is exactly one opening of the maze in the first row
 *  4.) There is exactly one ending of the maze in the last row
 *  5.) The image must be in black and white where white is a
 *      path and black is a wall.
 *  5.) All edges of the maze must be black (except the opening and closing)
 */
package bernardi;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MazeSolver extends Application{

    // static global maze
    private char[][] mazeArray;

    // global GridPane
    private GridPane gridPane = new GridPane();

    private boolean buttonPressedOnce = false;

    private int index = 0;

    private ComboBox<String> comboBox;

    // screen height depending on user's screen
    final double heightScreen = Screen.getPrimary().getBounds().getHeight();

    final double gridPaneHeight = (int) (heightScreen * .75) - 200;

    // create Maze object
    Maze maze;

    @Override
    public void start(Stage primaryStage) throws Exception {

        VBox root = new VBox();

        GridPane topPane = new GridPane();
        topPane.setStyle("-fx-background-color: powderblue;");

        Button button = new Button("Solve");
        button.setStyle("-fx-background-color: red; -fx-font-size: 40;");
        button.setMinWidth(200);
        button.setMinHeight(75);

        gridPane.setMinHeight(gridPaneHeight);
        gridPane.setPrefHeight(gridPaneHeight);

        root.getChildren().addAll(topPane, gridPane);

        Scene scene = new Scene(root);


        // create drop down combo box and add options
        comboBox = new ComboBox<>();
        String[] mazeURLs = {
                "10x10",
                "15x15",
                "41x41",
                "63x63",
                "100x100",
                "149x149",
                "189x189",
                "201x201",
                "251x251",
                "301x301",
                "349x349",
        };
        for(String s: mazeURLs) {
            comboBox.getItems().add(s);
        }

        // Sets the default value of the combobox to the first item in list
        comboBox.getSelectionModel().selectFirst();

        comboBox.setOnAction(event -> {
            String url = comboBox.getValue();
            try {
                setUpMazeImage("/" + url + ".bmp");
            }
            catch(Exception e)
            {
                e.getMessage();

            }
            if(button.isDisabled()) {
                button.setDisable(false);
            }
            buttonPressedOnce = false;
            addMazeToPane(mazeArray, gridPane);
            // create Maze object
            maze = new Maze(mazeArray);
            index = 0;

        });


        // enter path of image to be used as maze and
        // call the setUpMazeImage method
        setUpMazeImage("/15x15.bmp");

        // At this point, the global mazeArray is set up
        // So, add to GridPane
        addMazeToPane(mazeArray, gridPane);

        maze = new Maze(mazeArray);



        // add children of topPane
        Label label = new Label();
        label.setText("                      ");
        label.setStyle("-fx-font-size: 40");
        topPane.add(comboBox,2,0);
        topPane.add(button,4,0);
        topPane.add(label,9,0);
        topPane.setHgap(50);


        // boiler-plate JAVAFX code
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("Maze");
        primaryStage.setAlwaysOnTop(true);


        class ButtonHandler implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent event) {


                // if button has not been pressed yet,
                // then maze.solve() has not been called
                if(buttonPressedOnce == false) {

                    maze.solve();
                    System.out.println(maze.getListOfSols().size() + " is size of list");
                    if (maze.getListOfSols().size() != 0) {
                        addMazeToPane(maze.getListOfSols().get(index), gridPane);
                        if(maze.getListOfSols().size() > 1) {
                            button.setText("Next");
                            buttonPressedOnce = true;
                            label.setText(maze.getListOfSols().size() + " SOLUTIONS");
                            index++;
                            System.out.println(maze.getCounter() + " recursive calls!");
                        }
                        else {
                            label.setText("1 SOLUTION");
                            System.out.println(maze.getCounter() + " recursive calls!");
                            button.setDisable(true);
                        }


                    } else {

                        label.setText("NO SOLUTIONS!");
                        button.setDisable(true); // disable button if no solutions
                    }
                }
                else
                {
                    addMazeToPane(maze.getListOfSols().get(index++), gridPane);
                    System.out.println("Anal111");
                    if(index > maze.getListOfSols().size()-1) {
                        button.setDisable(true);
                    }
                }

            }
        }
        button.setOnAction(new ButtonHandler());

    }

    /**
     *  This takes an image stored in the resources folder, imports in as a
     *  JavaFX image, then processes the image to convert it to a 2D char array in memory.
     *  Each open space is represented with a '0' and a blocked space, or wall,
     *  is represented by a '1'. 'b' is used to represent where the beginning of the maze is,
     *  and 'e' is used to represent the ending.
      */
    private void setUpMazeImage(String url) throws Exception {
        BufferedImage im = ImageIO.read(getClass().getResource(url));
        Image imag = SwingFXUtils.toFXImage(im,null);
        ImageView image = new ImageView(imag);

        mazeArray = null;

        mazeArray = new char [im.getWidth()][im.getHeight()];


        // convert BufferedImage into 2D array maze
        for(int i =0; i < im.getHeight(); i++)
        {
            for(int j = 0; j < im.getWidth(); j++)
            {
                int x = im.getRGB(j,i);
                Color c = new Color(x, true);
                if(c.equals(Color.white))
                {
                    mazeArray[i][j] = '0';
                }
                else if(c.equals(Color.BLACK))
                {
                    mazeArray[i][j] = '1';
                }
                else
                {
                    System.out.println("Error Reading Image!");
                }
                // this checks for where the beginning of the maze is
                if(i==0 && mazeArray[i][j] == '0')
                {
                    mazeArray[i][j] = 'b';
                }

            }
        }
        // this checks for the ending or finish
        for(int k = 0; k < mazeArray[0].length; k++)
        {
            if(mazeArray[mazeArray[0].length-1][k] == '0')
            {
                mazeArray[mazeArray[0].length-1][k] = 'e';
            }
        }
    }

    /**
     * takes in and prints a 2D char array
     * @param arr
     */
    private void printArray(char[][] arr)
    {
        for(int i = 0; i < arr.length; i++)
        {
            for(int j = 0; j < arr[0].length; j++)
            {
                System.out.print(arr[i][j] + ", ");
            }
            System.out.println();
        }
    }


    /**
     * Will add the specified char array (maze) to the specified GridPane
     */
    private void addMazeToPane(char[][] mazeArr, GridPane gridPane)
    {
        gridPane.getChildren().clear(); // clears the list of children of gridpane
        int lengthOfSides = (int)gridPaneHeight / mazeArr.length;
        System.out.println("length of sides: " + lengthOfSides);

        for(int i = 0; i < mazeArr.length; i++)
        {
            for(int j = 0; j < mazeArr[0].length; j++)
            {
                Rectangle rect = new Rectangle();
                rect.setWidth(lengthOfSides);
                rect.setHeight(lengthOfSides);
                if(mazeArr[i][j] == '0')
                {
                    rect.setFill(javafx.scene.paint.Color.WHITE);
                }
                else if(mazeArr[i][j] == '1')
                {
                    rect.setFill(javafx.scene.paint.Color.BLACK);
                }
                else if(mazeArr[i][j] == 'b')
                {
                    rect.setFill(javafx.scene.paint.Color.GREEN);
                }
                else if(mazeArr[i][j] == 'e')
                {
                    rect.setFill(javafx.scene.paint.Color.RED);
                }
                else if(mazeArr[i][j] == 'p')
                {
                    rect.setFill(javafx.scene.paint.Color.BLUE);
                }
                gridPane.add(rect, j, i);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
