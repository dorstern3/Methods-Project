package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class WaitListFormController {

    @FXML
    private Button btnAltDate;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnWaitList;

    @FXML
    void clickAltDate(ActionEvent event) {
        System.out.println("User chose: Alternative Date - Need to open Table Screen");
        // פה נשים בהמשך את הקוד שיפתח את המסך השלישי שלך
    }

    @FXML
    void clickCancel(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "Book a Visit");
    }

    @FXML
    void clickWaitList(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful"); 
        alert.setHeaderText(null); 
        alert.setContentText("You have been successfully added to the waiting list.\n We will notify you if a spot becomes available.");
        alert.showAndWait();
    }
}

