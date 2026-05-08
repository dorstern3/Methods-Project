package application;

import common.Order;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;

public class OrderLogic {
    
    @FXML private TextField NumOfVisitors;
    @FXML private TextField OrderNumber;
    @FXML private DatePicker NewDate;
    
    @FXML
    public void onUpdate() {
        try {
            // Extract the text inputs from the GUI fields
            int visitors = Integer.parseInt(NumOfVisitors.getText());
            int orderNum = Integer.parseInt(OrderNumber.getText());
            LocalDate selectedDate = NewDate.getValue();
            
            // Validate that the user actually picked a date
            if(selectedDate == null) {
                System.out.println("Please select a date!");
                return;
            }
            
            // Create a new Order entity and populate it with the updated GUI data
            Order updatedOrder = new Order();
            updatedOrder.setOrderNumber(orderNum);
            updatedOrder.setNumberOfVisitors(visitors);
            updatedOrder.setOrderDate(selectedDate.toString());
            
            // Print to console to verify. Next step: send this object via OCSF to the server.
            System.out.println("Ready to send to server: " + updatedOrder.toString());
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers for Order Number and Visitors.");
        }
    }
    
    @FXML
    public void onShowTable() {
        System.out.println("Shown");
    }
}