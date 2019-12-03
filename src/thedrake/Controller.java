package thedrake;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

	@FXML
	private Button ButtonEnd;

	public void exit(){
		Platform.exit();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources){


	}
}
