package client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the Role Selection screen.
 * Allows the user to choose between Traveler or Park Employee roles.
 */
public class RoleSelectionController {

    @FXML
    private Button btnTraveler;

    @FXML
    private Button btnEmployee;

    /**
     * Initializes the controller.
     * Removes the default focus from the buttons when the screen loads.
     */
    @FXML
    public void initialize() {
        Platform.runLater(() -> btnTraveler.getParent().requestFocus());
    }

    /**
     * Handles the Traveler button click.
     * Navigates to the Traveler Entry menu.
     * 
     * @param event the action event
     */
    @FXML
    void clickTraveler(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/TravelerEntry.fxml", "Traveler Menu");
    }

    /**
     * Handles the Employee button click.
     * Navigates to the Employee Login screen.
     * 
     * @param event the action event
     */
    @FXML
    void clickEmployee(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/EmployeeLogin.fxml", "Employee Login");
    }
}