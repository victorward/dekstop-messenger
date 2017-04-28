package DTO;

import model.Country;
import model.Language;
import model.User;
import org.springframework.beans.BeanUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Yuriy
 */
public class UtilsDTO {
    public static User convertDTOtoUser(UserDTO userDTO) {
        User user = null;
        if (userDTO != null) {
            user = new User(convertDTOtoLanguageSET(userDTO.getLanguages()), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getPhone(), userDTO.getGender(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getAddress(), convertDTOtoCountry(userDTO.getCountry()), userDTO.getBirthDate(), userDTO.getPhoto());
        }
        return user;
    }

    public static UserDTO convertUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        if (user != null) {
            userDTO = new UserDTO(convertLanguageSETtoDTO(user.getLanguages()), user.getFirstName(), user.getLastName(), user.getPhone(), user.getGender(), user.getEmail(), user.getPassword(), user.getAddress(), convertCountryToDTO(user.getCountry()), user.getBirthDate(), user.getPhoto());
        }
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public static Country convertDTOtoCountry(CountryDTO countryDTO) {
        Country country = new Country();
        BeanUtils.copyProperties(countryDTO, country);
        return country;
    }

    public static CountryDTO convertCountryToDTO(Country country) {
        CountryDTO countryDTO = new CountryDTO();
        BeanUtils.copyProperties(country, countryDTO);
        return countryDTO;
    }

    public static Set<Language> convertDTOtoLanguageSET(Set<LanguageDTO> languageDTO) {
        Set<Language> languageSet = new HashSet<>();
        for (LanguageDTO lang : languageDTO) {
            Language language = new Language();
            BeanUtils.copyProperties(lang, language);
            languageSet.add(language);
        }
        return languageSet;
    }

    public static Set<LanguageDTO> convertLanguageSETtoDTO(Set<Language> language) {
        Set<LanguageDTO> languageSet = new HashSet<>();
        for (Language lang : language) {
            LanguageDTO languageDTO = new LanguageDTO();
            BeanUtils.copyProperties(lang, languageDTO);
            languageSet.add(languageDTO);
        }
        return languageSet;
    }

}
