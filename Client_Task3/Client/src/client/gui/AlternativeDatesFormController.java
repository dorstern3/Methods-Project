package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AlternativeDatesFormController {

    @FXML
    private Button btnBook;

    @FXML
    private Button btnCancel;

    @FXML
    void clickBook(ActionEvent event) {
        System.out.println("User chose to book the alternative date");
        // פה נשים בהמשך את הקוד שלוקח את השורה שהמשתמש בחר בטבלה
    }

    @FXML
    void clickCancel(ActionEvent event) {
        ScreenSwitch.switchScreen("/client/gui/NewOrderForm.fxml", "Book a Visit");
    }

}