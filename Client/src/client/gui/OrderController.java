package client.gui;

import java.util.ArrayList;
import java.util.Date;

import client.logic.OrderLogic;
import common.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class OrderController {

	// Fields of the Update form
	@FXML private TextField NumOfVisitors;
	@FXML private TextField OrderNumber;
	@FXML private DatePicker NewDate;
	@FXML private TextField status;
	
	// Fields of the Table
	@FXML private TableView<Order> orderTable;
	@FXML private TableColumn<Order,Integer> colOrderNumber;
	@FXML private TableColumn<Order,Date> colOrderDate;
	@FXML private TableColumn<Order,Integer> colNumberOfVisitors;
	@FXML private TableColumn<Order,Integer> colConfirmationCode;
	@FXML private TableColumn<Order,Integer> colSubscriberId;
	@FXML private TableColumn<Order,Date> colDateOfPlacingOrder;
	
	
	private ObservableList<Order> orders = FXCollections.observableArrayList();				// Helper list for the UI
	private OrderLogic orderLogic = OrderLogic.getInstance();								// Connection to the logic of the order
	
	@FXML
	public void initialize() {
		orderTable.setItems(orders);
		// Defining the columns to the fields of Order class
		colOrderNumber.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
		colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
		colNumberOfVisitors.setCellValueFactory(new PropertyValueFactory<>("numberOfVisitors"));
		colConfirmationCode.setCellValueFactory(new PropertyValueFactory<>("confirmationCode"));
		colSubscriberId.setCellValueFactory(new PropertyValueFactory<>("subscriberId"));
		colDateOfPlacingOrder.setCellValueFactory(new PropertyValueFactory<>("dateOfPlacingOrder"));
	}
	
	
	public void onUpdate() {
		try {
			// Change the values of the fields as required
			status.setText("");
			int visitors = Integer.parseInt(NumOfVisitors.getText());
			int orderNum = Integer.parseInt(OrderNumber.getText());
			java.sql.Date date = java.sql.Date.valueOf(NewDate.getValue());
			
			if(NewDate.getValue() == null) {
				status.setText("Please select a date!");
				return;
			}
			
			status.setText("Updating...");
			Order orderToUpdate = new Order(orderNum ,date ,visitors ,0 , 0 , null);	// Send the order to the logic level
			String response = orderLogic.sendOrderUpdate(orderToUpdate);
			
			// Reset the fields && set a message that the order has updated
			NumOfVisitors.setText("");
			OrderNumber.setText("");
			status.setText(response);
			
		} catch (NumberFormatException e) {
			status.setText("Please enter valid numbers!");
	    } catch (Exception e) {
	        status.setText("An unexpected error occurred.");
	        e.printStackTrace();
	    }
	}
	
	
	public void onShowTable() {
		ArrayList<Order> orderFromDB = orderLogic.getAllOrders();				// Get data from logic level
		orders.setAll(orderFromDB);												// Set the observable list for UI
	}
	
	// Call for disconnecting the client from server
	public void disconnect() {
		orderLogic.disconnect();
	}
}
