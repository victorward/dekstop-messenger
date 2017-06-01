package zaawjava.server.Utils;

import zaawjava.commons.DTO.ChatMessageDTO;
import zaawjava.commons.DTO.CountryDTO;
import zaawjava.commons.DTO.LanguageDTO;
import zaawjava.commons.DTO.UserDTO;
import org.springframework.beans.BeanUtils;
import zaawjava.server.model.ChatMessage;
import zaawjava.server.model.Country;
import zaawjava.server.model.Language;
import zaawjava.server.model.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Yuriy
 */
public class DTOUtils implements Serializable {
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

    public static List<UserDTO> convertUserToDTO(List<User> users) {
        List<UserDTO> userDTOs = users
                .stream()
                .map((DTOUtils::convertUserToDTO))
                .collect(Collectors.toList());
        return userDTOs;
    }

    public static UserDTO convertUserToDTOwithOnlyMainData(User user) {
        UserDTO userDTO = null;
        if (user != null) {
            userDTO = new UserDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getBirthDate(),
                    user.getGender(),
                    user.getPhoto());
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

    public static List<CountryDTO> convertCountryToDTO(List<Country> countries) {
        List<CountryDTO> countryDTOS = countries.stream()
                .map(DTOUtils::convertCountryToDTO)
                .collect(Collectors.toList());

        return countryDTOS;
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

    public static List<LanguageDTO> convertLanguageToDTO(List<Language> languages) {
        List<LanguageDTO> languageDTOS = languages.stream()
                .map(DTOUtils::convertLanguageToDTO)
                .collect(Collectors.toList());
        return languageDTOS;
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

    public static ChatMessageDTO convertChatMessageToDTO(ChatMessage chatMessage) {
        ChatMessageDTO converted = new ChatMessageDTO();
        BeanUtils.copyProperties(chatMessage, converted);
        converted.setSender(DTOUtils.convertUserToDTO(chatMessage.getSender()));
        return converted;
    }

    public static ChatMessage convertDTOToChatMessage(ChatMessageDTO chatMessageDTO) {
        ChatMessage converted = new ChatMessage();
        BeanUtils.copyProperties(chatMessageDTO, converted);
        converted.setSender(DTOUtils.convertDTOtoUser(chatMessageDTO.getSender()));
        return converted;
    }

    public static List<ChatMessageDTO> convertChatMessageToDTO(List<ChatMessage> chatMessages) {
        return chatMessages.stream()
                .map(DTOUtils::convertChatMessageToDTO)
                .collect(Collectors.toList());
    }

}
