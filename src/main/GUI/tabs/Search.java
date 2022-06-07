package main.GUI.tabs;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.GUI.ItemDisplay;
import main.GUI.Tab;
import main.data.Database;
import main.data.Item;
import main.data.ItemFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A class to make the Search GUI and search the database for
 * the item or file the user is searching for.
 *
 *
 * @author Wei Wei Chien
 * @version Spring 2022
 */

public class Search extends Tab {

    /**
     * Default size of buttons
     */
    private static final int BUTTON_SIZE = 250;
    private final double FILE_BUTTON_WIDTH = 500;

    /**
     * Pane for GUI elements
     */
    private Pane flowPane;
    private GridPane gridPane;
    private ScrollPane fileDisplay;

    /**
     * Arrays
     */
    private ArrayList<Item> itemsList;
    private ArrayList<ItemFile> itemsFiles;
    private Item[] items;



    private Button[] fileButtons;

    /**
     * Text to show Search
     */
    private final Text selectText;
    private final Text searchingText;
    private final Text errormsg;
    /**
     * Drop down box
     */
    private ChoiceBox<String> choiceBox;

    /**
     * Text field that allows user to search
     */
    private final TextField searchingBox;

    /**
     * Button
     */
    private final Button magGlass;

    /**
     * Boolean if user choose item or file
     */
    private boolean itemOrFile;

    private Stage myStage;

    /**
     * Constructor that class the super method from the tabs class.
     * Builds the GUI for Search. Includes Text, user input field, and
     * Search button.
     *
     * @param buttonName name of the button - Search
     * @param icon       image of the button
     */
    public Search(String buttonName, Image icon) {

        super(buttonName, icon);

        //first line : Select [choiceBox]
        //Text to prompt which type of search
        selectText = new Text("Select");
        selectText.getStyleClass().add("white-text");
        selectText.setStyle("-fx-font: 30 arial;");

        //Dropdown box for user to choose type
        choiceBox = new ChoiceBox<>();
        choiceBox.getItems().add("Items");
        choiceBox.getItems().add("Files");

        //default dropdown box setting
        choiceBox.setValue("Files");



        //second line : Search [Search field] button
        //Text to prompt which type of search
        searchingText = new Text("Searching");
        searchingText.getStyleClass().add("white-text");
        searchingText.setStyle("-fx-font: 30 arial;");

        //Text field for the user to type what they're searching
        searchingBox = new TextField();
        searchingBox.setMinSize(400, 50);
        searchingBox.setFont(Font.font("arial", 24));

        //Sets up the Search button
        magGlass = new Button();
        magGlass.getStyleClass().add("transparent-square-button");
        ImageView Arrowimage = new ImageView(new Image("/searchIcon.png"));
        Arrowimage.setFitHeight(BUTTON_SIZE * .20);
        Arrowimage.setFitWidth(BUTTON_SIZE * .20);
        magGlass.setGraphic(Arrowimage);



        //get what user is searching for - action for magGlass button
        magGlass.setOnAction(e -> {

            String userSearch = searchingBox.getText().toString();

            if (itemOrFile) {
                itemsList = searchItem(userSearch);
                fileDisplay.setContent(buildItemViewer());
            } else { //if (choice.equals("Files"))
                itemsFiles = searchFile(userSearch);
                fileDisplay.setContent(buildFileViewer());
            }

        });



        //sets up error message
        javafx.scene.text.Font font = Font.font("Verdana", FontWeight.BOLD, 25);
        errormsg = new Text("This does not exist!");
        errormsg.setFill(Color.RED);
        errormsg.setVisible(false);
        errormsg.setFont(font);
    }

    /**
     * Builds the GUI for Search. Aligns and size everything in pane.
     *
     * @param stage to add in GUI
     */
    @Override
    public Pane buildView(Stage stage) {
        //create pane
        //borderPane = new BorderPane();
        flowPane = new FlowPane(Orientation.VERTICAL);
        ((FlowPane)flowPane).setAlignment(Pos.TOP_CENTER);
        gridPane = new GridPane();
        myStage = stage;

        //set size and align elements
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setAlignment(Pos.TOP_CENTER);
        gridPane.add(selectText, 0, 0);
        gridPane.add(choiceBox, 1, 0);

        gridPane.add(searchingText, 0, 2);
        gridPane.add(searchingBox, 1, 2);
        gridPane.add(magGlass, 2, 2);
        gridPane.add(errormsg, 1,3);
        flowPane.getChildren().add(gridPane);

        //action for user choosing type
        choiceBox.setOnAction( e-> {
            String choice = choiceBox.getValue();

            if (choice.equals("Items")) {
                itemOrFile = true;
                itemsList = searchItem("");
                for (Item x : itemsList) {
                    System.out.println(x);
                }

                fileDisplay.setContent(buildItemViewer());
            } else { //if (choice.equals("Files"))
                itemOrFile = false;
                itemsFiles = searchFile("");
                for (ItemFile x : itemsFiles) {
                    System.out.println(x);
                }
                fileDisplay.setContent(buildFileViewer());
            }
        });


        fileDisplay = new ScrollPane();
        fileDisplay.getStyleClass().add("scroll-pane");
        fileDisplay.setStyle("-fx-font-size: 16");

        //fileDisplay.setContent(buildItemViewer());
        itemsList = searchItem("");
        itemsFiles = searchFile("");
        fileDisplay.setContent(buildFileViewer());
        fileDisplay.setMaxHeight(stage.getHeight() - 300);
        fileDisplay.setMinWidth(FILE_BUTTON_WIDTH);
        fileDisplay.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        flowPane.getChildren().add(fileDisplay);
        return flowPane;
    }


    /**
     * Searching Items(database)
     *
     * @param itemSearch - item user is searching for
     * @return list of items that contains the keywords
     */
    public ArrayList searchItem(String itemSearch) {
        itemsList = new ArrayList<>();

        if (itemSearch == null) {
            itemSearch = "";
            //error message if nothing matched
            errormsg.setVisible(true);
        }

        //add all items into arraylist
        items = Database.db.getItems();

        itemsList.addAll(Arrays.asList(Database.db.getItems()));

        //Remove anything that does not have keyword
        String finalItem = itemSearch;
        itemsList.removeIf(s -> !s.getName().toLowerCase().contains(finalItem));

        return itemsList;
    }


    /**
     * Searching Files
     *
     * @param fileSearch - file user is searching for
     * @return list of files that contains the keywords
     */
    public ArrayList searchFile(String fileSearch) {
        itemsFiles = new ArrayList<>();

        if (fileSearch == null) {
            fileSearch = "";
            //error message if nothing matched
            errormsg.setVisible(true);
        }

        //add all files unto ArrayList
        items = Database.db.getItems();
        for (int i = 0; i < items.length; i++) {
            itemsFiles.addAll(Arrays.asList(items[i].getFiles()));
        }

        //Remove anything that does not have keyword
        String finalFile = fileSearch;
        itemsFiles.removeIf(s -> !s.getName().toLowerCase().contains(finalFile));

        return itemsFiles;
    }

    private VBox buildItemViewer() {
        fileButtons = new Button[itemsList.size()];

        //sort in alphabetical order
        itemsList.sort(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return item1.getName().compareTo(item2.getName());
            }
        });


        //Desktop is used for opening files
        Desktop desktop = Desktop.getDesktop();
        Font fileButtonFont = new Font(24);

        for (int i = 0; i < itemsList.size(); i++) {
            //Initializes the item button
            Button currFileButton = new Button("Error...");
            currFileButton.getStyleClass().add("transparent-square-button");
            currFileButton.setFont(fileButtonFont);
            currFileButton.setAlignment(Pos.CENTER_LEFT);

            currFileButton.setText(itemsList.get(i).getName());
            currFileButton.setMinWidth(FILE_BUTTON_WIDTH);

            int finalI = i;
            currFileButton.setOnAction(clickEvent -> {
                ItemDisplay itemDisplay = new ItemDisplay(myStage);
                flowPane.getChildren().clear();
                flowPane.getChildren().add(itemDisplay.buildView(itemsList.get(finalI)));
            });

            fileButtons[i] = currFileButton;
        }


        VBox buttonGroup = new VBox(fileButtons);
        return buttonGroup;
    }

    /**
     * Build list to display files  - user can click on file
     *
     * @return VBox with list of files
     */
    private VBox buildFileViewer() {


        fileButtons = new Button[itemsFiles.size()];

        //sort in alphabetical order
        itemsFiles.sort(new Comparator<ItemFile>() {
            @Override
            public int compare(ItemFile file1, ItemFile file2) {
                return file1.getName().compareTo(file2.getName());
            }
        });


        //Desktop is used for opening files
        Desktop desktop = Desktop.getDesktop();
        Font fileButtonFont = new Font(24);
        for (int i = 0; i < itemsFiles.size(); i++) {
            //Initializes the item button
            Button currFileButton = new Button("Error...");
            currFileButton.getStyleClass().add("transparent-square-button");
            currFileButton.setFont(fileButtonFont);
            currFileButton.setAlignment(Pos.CENTER_LEFT);

            File selectedFile = new File(itemsFiles.get(i).getPath());
            currFileButton.setText(itemsFiles.get(i).getName() + "\t : \t" + selectedFile.getName());
            currFileButton.setMinWidth(FILE_BUTTON_WIDTH);
            int index = i;//action listeners can't take in i
            currFileButton.setOnAction(clickEvent -> {
                //Gets the file from the path
                //Makes sure the file exists
                if (selectedFile.exists()) {
                    try {
                        desktop.open(selectedFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Error: file does not exist");
                }
            });
            fileButtons[i] = currFileButton;
        }

        VBox buttonGroup = new VBox(fileButtons);
        return buttonGroup;
    }
}
