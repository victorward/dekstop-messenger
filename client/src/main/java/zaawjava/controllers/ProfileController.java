/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zaawjava.controllers;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import DTO.CountryDTO;
import DTO.LanguageDTO;
import DTO.UserDTO;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
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
import zaawjava.services.SocketService;
import zaawjava.services.UserService;
import zaawjava.utils.Utils;

@Component
public class ProfileController implements Initializable {

    private ScreensManager screensManager;
    private UserService userService;
    private final SocketService socketService;

    private String selectedLanguage;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    @Autowired
    ProfileController(SocketService socketService) {
        this.socketService = socketService;
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
    private ChoiceBox<String> country;
    @FXML
    private JFXTextField number;
    @FXML
    private JFXTextField street;

    private ObservableList<LanguageDTO> languagess;

    List<CountryDTO> allCountries;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        languagess = FXCollections.observableArrayList();
        language.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LanguageDTO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LanguageDTO, String> p) {
                return new SimpleStringProperty(p.getValue().getLanguageName());
            }
        });
        init();
    }

    void init() {
        addLenguagesToSystem();
        addCountryToSystem();
        fillUserData();
    }

    void addLenguagesToSystem() {
        ObservableList<String> languagesObsList = FXCollections.observableArrayList();
        languagess.addAll(userService.getUser().getLanguages());
        Languages.setItems(languagess);
        socketService.emit("getLanguagesList", "").whenComplete((msg, ex) -> {
            if (ex == null) {
                List<LanguageDTO> allLanguages = (List<LanguageDTO>) msg;
                List<String> languagesss = new ArrayList<>();
                for (LanguageDTO l : allLanguages) {
                    languagesss.add(l.getLanguageName());
                }
                languagesObsList.setAll(languagesss);
                Platform.runLater(() -> languagesList.setItems(languagesObsList));
            } else {
                Platform.runLater(() -> showError("Failed during getting languages list"));
            }
        });
    }

    void addCountryToSystem() {
        ObservableList<String> countryObsList = FXCollections.observableArrayList();
        socketService.emit("getCountryList", "").whenComplete((msg, ex) -> {
            if (ex == null) {
                List<CountryDTO> allCountriesss = (List<CountryDTO>) msg;
                Platform.runLater(() -> setAllCountries(allCountriesss));
                List<String> countries = new ArrayList<>();
                for (CountryDTO c : allCountriesss) {
                    countries.add(c.getCountryName());
                }
                countryObsList.setAll(countries);
                Platform.runLater(() -> country.setItems(countryObsList));
            } else {
                Platform.runLater(() -> showError("Failed during getting country list"));
            }
        });

    }

    void fillUserData() {
        firstName.setText(userService.getUser().getFirstName());
        lastName.setText(userService.getUser().getLastName());
        email.setText(userService.getUser().getEmail());
        password.setText(userService.getUser().getPassword());
        sex.setSelected(userService.getUser().getGender().equals("female"));
        date.setValue(userService.getUser().getBirthDate());
        number.setText(Integer.toString(userService.getUser().getPhone()));
        if (userService.getUser().getAddress() != null)
            street.setText(userService.getUser().getAddress());
        if (userService.getUser().getCountry() != null)
            country.setValue(userService.getUser().getCountry().getCountryName());
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

    @FXML
    public void okClicked() {
        if (isInputValid()) {
            UserDTO user = fillNewData();
            socketService.emit("updateUser", user).whenComplete((msg, ex) -> {
                if (ex == null) {
                    if ("updated".equals(msg)) {
                        Platform.runLater(() -> showSuccess());
                        screensManager.goToMainView();
                    } else {
                        Platform.runLater(() -> showError("Read server output. " + msg));
                    }
                } else {
                    Platform.runLater(() -> showError("Failed during updating actual user"));
                }
            });
        }
    }

    private UserDTO fillNewData() {
        System.out.println("|W profilu " + userService.getUser());
        UserDTO user = userService.getUser();
        user.setAddress(street.getText());
        user.setBirthDate(date.getValue());
        user.setEmail(email.getText());
        if (sex.isSelected()) {
            user.setGender("female");
        } else {
            user.setGender("male");
        }
        user.setFirstName(firstName.getText());
        user.setLastName(lastName.getText());
        user.setPassword(password.getText());
        user.setPhone(Integer.parseInt(number.getText()));
        user.setCountry(new CountryDTO(getCountryID(country.getValue()), country.getValue()));
        return user;
    }

    private int getCountryID(String name) {
        int id = 0;
        for (CountryDTO country : allCountries) {
            if (country.getCountryName().equals(name)) id = country.getId();
        }
        return id;
    }

    private boolean isInputValid() {
        String errorMessage = "";
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
        if (date.getValue() == null || date.getValue() == null) {
            errorMessage += "No valid date!\n";
        } else {
            if (!Utils.validDate(date.getValue())) {
                errorMessage += "No valid date. Use the format yyyy-dd-mm!\n";
            }
            if (!Utils.validDate(date.getValue())) {
                errorMessage += "No valid date. Use the format yyyy-dd-mm!\n";
            }
        }
        if (sex == null) {
            errorMessage += "Please select your gender!\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(screensManager.getStage());
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    private void showSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Congratulations your data have successfully updated");
        alert.setContentText("Now you check new data in your profile");
        alert.showAndWait();
    }

    private void showError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something gone wrong");
        alert.setContentText(text);
        alert.showAndWait();
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

    public void setAllCountries(List<CountryDTO> allCountries) {
        this.allCountries = allCountries;
    }

}
