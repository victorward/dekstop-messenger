package zaawjava.Utils;

import DTO.CountryDTO;
import DTO.LanguageDTO;
import DTO.UserDTO;
import org.springframework.beans.BeanUtils;
import zaawjava.model.Country;
import zaawjava.model.Language;
import zaawjava.model.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Yuriy
 */
public class UtilsDTO implements Serializable {
    public static User convertDTOtoUser(UserDTO userDTO) {
        User user = null;
        if (userDTO != null) {
            user = new User(userDTO.getId(), convertDTOtoLanguageSET(userDTO.getLanguages()), userDTO.getFirstName(), userDTO.getLastName(), userDTO.getPhone(), userDTO.getGender(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getAddress(), convertDTOtoCountry(userDTO.getCountry()), userDTO.getBirthDate(), userDTO.getPhoto());
        }
        return user;
    }

    public static UserDTO convertUserToDTO(User user) {
        UserDTO userDTO = null;
        if (user != null) {
            userDTO = new UserDTO(user.getId(), convertLanguageSETtoDTO(user.getLanguages()), user.getFirstName(), user.getLastName(), user.getPhone(), user.getGender(), user.getEmail(), user.getPassword(), user.getAddress(), convertCountryToDTO(user.getCountry()), user.getBirthDate(), user.getPhoto());
        }
        return userDTO;
    }

    public static UserDTO convertUserToDTOwithOnlyMainData(User user) {
        UserDTO userDTO = null;
        if (user != null) {
            userDTO = new UserDTO(user.getId(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getBirthDate(), user.getGender());
        }
        return userDTO;
    }


    public static Country convertDTOtoCountry(CountryDTO countryDTO) {
        Country country = null;
        if (countryDTO != null) {
            country = new Country();
            BeanUtils.copyProperties(countryDTO, country);
        }
        return country;
    }

    public static CountryDTO convertCountryToDTO(Country country) {
        CountryDTO countryDTO = null;
        if (country != null) {
            countryDTO = new CountryDTO();
            BeanUtils.copyProperties(country, countryDTO);
        }
        return countryDTO;
    }

    public static Set<Language> convertDTOtoLanguageSET(Set<LanguageDTO> languageDTO) {
        Set<Language> languageSet = null;
        if (languageDTO != null) {
            languageSet = new HashSet<>();
            for (LanguageDTO lang : languageDTO) {
                Language language = new Language();
                BeanUtils.copyProperties(lang, language);
                languageSet.add(language);
            }
        }
        return languageSet;
    }

    public static Set<LanguageDTO> convertLanguageSETtoDTO(Set<Language> language) {
        Set<LanguageDTO> languageSet = null;
        if (language != null) {
            languageSet = new HashSet<>();
            for (Language lang : language) {
                LanguageDTO languageDTO = new LanguageDTO();
                BeanUtils.copyProperties(lang, languageDTO);
                languageSet.add(languageDTO);
            }
        }
        return languageSet;
    }

    public static Language convertDTOtoLanguage(LanguageDTO languageDTO) {
        Language language = null;
        if (languageDTO != null) {
            language = new Language();
            BeanUtils.copyProperties(languageDTO, language);
        }
        return language;
    }

    public static LanguageDTO convertLanguageToDTO(Language language) {
        LanguageDTO languageDTO = null;
        if (language != null) {
            languageDTO = new LanguageDTO();
            BeanUtils.copyProperties(language, languageDTO);
        }
        return languageDTO;
    }

}
