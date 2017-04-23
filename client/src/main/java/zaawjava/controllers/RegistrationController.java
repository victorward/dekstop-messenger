/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.utils.Utils;

@Component
public class RegistrationController implements Initializable {

    private ScreensManager screensManager;
    private final SocketService socketService;

    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private DatePicker dataPicker;
    @FXML
    private ProgressBar progressBar;
    @FXML
    ToggleGroup group;
    ToggleButton checkToogle = null;
    @FXML
    private Label errorLabel;

    @Autowired
    public RegistrationController(SocketService socketService) {
        this.socketService = socketService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        init();
    }

    public void init() {
        screensManager.getStage().setTitle("Registration");

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                checkToogle = (ToggleButton) t1.getToggleGroup().getSelectedToggle();
            }
        });

        dataPicker.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                dataPicker.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    private boolean isInputValid() {
        String errorMessage = "";
        progressBar.setProgress(0.15);
        if (email.getText() == null || email.getText().length() == 0) {
            errorMessage += "Empty email!\n";
        } else {
            if (!Utils.validateEmail(email.getText())) {
                errorMessage += "Not correct email!\n";
            }
        }
        if (password.getText() == null || password.getText().length() == 0) {
            errorMessage += "Empty password!\n";
        }
        if (firstName.getText() == null || firstName.getText().length() == 0) {
            errorMessage += "Empty first name!\n";
        } else {
            if (firstName.getText().length() > 20) {
                errorMessage += "Too large first name!\n";
            }
        }
        if (lastName.getText() == null || lastName.getText().length() == 0) {
            errorMessage += "Empty last name!\n";
        } else {
            if (lastName.getText().length() > 20) {
                errorMessage += "Too large last name!\n";
            }
        }
        if (dataPicker.getValue() == null || dataPicker.getValue() == null) {
            errorMessage += "No valid date!\n";
        } else {
            if (!Utils.validDate(dataPicker.getValue())) {
                errorMessage += "No valid date. Use the format yyyy-dd-mm!\n";
            }
            if (!Utils.validDate(dataPicker.getValue())) {
                errorMessage += "No valid date. Use the format yyyy-dd-mm!\n";
            }
        }
        if (checkToogle == null) {
            errorMessage += "Please select your gender!\n";
        }

        if (errorMessage.length() == 0) {
            progressBar.setProgress(0.25);
            return true;
        } else {
            progressBar.setProgress(0.01);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(screensManager.getStage());
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    @FXML
    void onBackClick(ActionEvent event) throws IOException {
        screensManager.goToLoginView();
    }

    @FXML
    void onSign() throws IOException {
        if (isInputValid()) {
            User user = new User(email.getText(), password.getText(), firstName.getText(), lastName.getText(), dataPicker.getValue(), checkToogle.getText());
            progressBar.setProgress(0.45);
            insertNewUser(user);
        }
    }

    private void insertNewUser(User user) {
        socketService.emit("onRegistration", user).whenComplete((msg, ex) -> {
            if (ex == null) {
                progressBar.setProgress(0.7);
                try {
                    if ("registered".equals(msg)) {
                        progressBar.setProgress(0.90);
                        showSuccess();
                        screensManager.goToLoginView();
                    } else {
                        Platform.runLater(() -> errorLabel.setText("Registration failed during writing data to database"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> errorLabel.setText("Cannot load login view"));
                }
            } else {
                Platform.runLater(() -> errorLabel.setText("Registration failed during connection to database"));
            }
        });
    }

    private void showSuccess(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(screensManager.getStage());
        alert.setTitle("Success");
        alert.setHeaderText("Congratulations you've successfully registered");
        alert.setContentText("Now you can log in your account");
        alert.showAndWait();
        progressBar.setProgress(0.99);
    }
}
