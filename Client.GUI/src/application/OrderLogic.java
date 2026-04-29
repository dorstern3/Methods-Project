package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;

public class OrderLogic {
	
	@FXML private TextField NumOfVisitors;
	@FXML private TextField OrderNumber;
	@FXML private DatePicker NewDate;
	
	@FXML
	public void onUpdate() {
		System.out.println(NumOfVisitors.getText());
	}
	
	@FXML
	public void onShowTable() {
		System.out.println("Shown");
	}
}
