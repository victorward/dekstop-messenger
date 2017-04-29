/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import DTO.LanguageDTO;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.UserService;

@Component
public class ProfileController implements Initializable {

    private ScreensManager screensManager;
    private UserService userService;

    private String selectedLanguage;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    @FXML
    private TableView<LanguageDTO> Languages;
    @FXML
    private TableColumn<LanguageDTO, String> language;
    @FXML
    private ChoiceBox<String> languagesList;

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

    private ObservableList<LanguageDTO> languagess;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        languagess = FXCollections.observableArrayList();
        ObservableList<String> languagesObsList = FXCollections.observableArrayList();

        languagess.addAll(userService.getUser().getLanguages());
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale obj : locales) {
            languagesObsList.add(obj.getDisplayLanguage());
        }
        Languages.setItems(languagess);
        languagesList.setItems(languagesObsList);

        language.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LanguageDTO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LanguageDTO, String> p) {
                return new SimpleStringProperty(p.getValue().getLanguageName());
            }
        });
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

    @FXML
    void delLanguage() {
        if (Languages.getSelectionModel().getSelectedItem() != null) {
            userService.getUser().getLanguages().remove(Languages.getSelectionModel().getSelectedItem());
            updateLanguages();
        }
    }

    @FXML
    void addLanguage() {
        if (languagesList.getValue() != null) {
//            if (!userService.getUser().getLanguages().contains(languagesList.getValue())) {
            if (!checkAllLeng()) {
                userService.getUser().getLanguages().add(new LanguageDTO(languagesList.getValue()));
                updateLanguages();
            }
        }
    }

    private void updateLanguages() {
        languagess.removeAll(languagess);
        languagess.addAll(userService.getUser().getLanguages());
    }

    private boolean checkAllLeng() {
        for (LanguageDTO lang : languagess) {
            if (lang.getLanguageName().equals(languagesList.getValue())) {
                return true;
            }
        }
        return false;
    }

}
