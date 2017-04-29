/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ToggleButton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.UserService;

@Component
public class ProfileController implements Initializable {

    private ScreensManager screensManager;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    @FXML
    private JFXTextField firstName;
    @FXML
    private JFXTextField lastName;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXPasswordField password;
    @FXML
    private ToggleButton sex;
    @FXML
    private DatePicker date;
    @FXML
    private JFXTextField country;
    @FXML
    private JFXTextField number;
    @FXML
    private JFXTextField languages;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        init();
    }

    void init() {
        fillUserData();
    }

    void fillUserData() {
        firstName.setText(userService.getUser().getFirstName());
        lastName.setText(userService.getUser().getLastName());
        email.setText(userService.getUser().getEmail());
        password.setText(userService.getUser().getPassword());
        sex.setSelected(userService.getUser().getGender().equals("Male"));
        date.setValue(userService.getUser().getBirthDate());
        country.setText(userService.getUser().getCountry().getCountryName());
        number.setText(Integer.toString(userService.getUser().getPhone()));
    }

    @FXML
    void onCancelClick(ActionEvent event) {
        screensManager.goToMainView();
    }

}
