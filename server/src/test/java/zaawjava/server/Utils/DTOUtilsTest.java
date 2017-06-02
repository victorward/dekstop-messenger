package zaawjava.server.Utils;

import org.junit.Test;
import zaawjava.commons.DTO.CountryDTO;
import zaawjava.commons.DTO.LanguageDTO;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.server.model.Country;
import zaawjava.server.model.Language;
import zaawjava.server.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DTOUtilsTest {

    private boolean equalsCountryAndDto(Country country, CountryDTO dto) {
        if (country.getId() != dto.getId()) return false;
        if (!country.getCountryName().equals(dto.getCountryName())) return false;
        return true;
    }

    private boolean equalsLanguageAndDto(Language language, LanguageDTO dto) {
        if (language.getId() != dto.getId()) return false;
        if (!language.getLanguageName().equals(dto.getLanguageName())) return false;
        return true;
    }

    private boolean equalsUserAndDto(User user, UserDTO dto) {
        if (user.getId() != dto.getId()) return false;
        if (!user.getFirstName().equals(dto.getFirstName())) return false;
        if (!user.getAddress().equals(dto.getAddress())) return false;
        if (!user.getBirthDate().equals(dto.getBirthDate())) return false;
        if (!equalsCountryAndDto(user.getCountry(), dto.getCountry())) return false;
        if (!user.getEmail().equals(dto.getEmail())) return false;
        if (!user.getGender().equals(dto.getGender())) return false;
        if (!user.getLastName().equals(dto.getLastName())) return false;
        if (!user.getPassword().equals(dto.getPassword())) return false;
        if (user.getPhone() != dto.getPhone()) return false;
        if (!user.getPhoto().equals(dto.getPhoto())) return false;

        Set<Language> languages = user.getLanguages();
        Set<LanguageDTO> languageDtos = dto.getLanguages();

        if (languageDtos.size() != languages.size()) return false;

        for (Language language : languages) {
            boolean found = languageDtos
                    .stream()
                    .anyMatch(d -> equalsLanguageAndDto(language, d));

            if (!found) return false;
        }

        return true;

    }

    @Test
    public void convertDTOtoUser() throws Exception {
        //dto definition
        Set<LanguageDTO> languageDTOS = new HashSet<>();
        languageDTOS.add(new LanguageDTO(1, "Poland"));
        languageDTOS.add(new LanguageDTO(2, "England"));

        UserDTO dto = new UserDTO();
        dto.setId(1);
        dto.setFirstName("A");
        dto.setAddress("Address");
        dto.setBirthDate(LocalDate.now());
        dto.setCountry(new CountryDTO(1, "Country"));
        dto.setEmail("email");
        dto.setGender("male");
        dto.setLanguages(languageDTOS);
        dto.setLastName("Naz");
        dto.setPassword("asd");
        dto.setPhone(123123123);
        dto.setPhoto("/url/aaa");

        User converted = DTOUtils.convertDTOtoUser(dto);

        assertThat(equalsUserAndDto(converted, dto)).isTrue();

    }

//    @Test
//    public void convertUserToDTO() throws Exception {
//    }
//
//    @Test
//    public void convertUserToDTO1() throws Exception {
//    }
//
//    @Test
//    public void convertUserToDTOwithOnlyMainData() throws Exception {
//    }
//
//    @Test
//    public void convertDTOtoCountry() throws Exception {
//    }
//
//    @Test
//    public void convertCountryToDTO() throws Exception {
//    }
//
//    @Test
//    public void convertCountryToDTO1() throws Exception {
//    }
//
//    @Test
//    public void convertDTOtoLanguageSET() throws Exception {
//    }
//
//    @Test
//    public void convertLanguageSETtoDTO() throws Exception {
//    }
//
//    @Test
//    public void convertLanguageToDTO() throws Exception {
//    }
//
//    @Test
//    public void convertDTOtoLanguage() throws Exception {
//    }
//
//    @Test
//    public void convertLanguageToDTO1() throws Exception {
//    }
//
//    @Test
//    public void convertChatMessageToDTO() throws Exception {
//    }
//
//    @Test
//    public void convertDTOToChatMessage() throws Exception {
//    }
//
//    @Test
//    public void convertChatMessageToDTO1() throws Exception {
//    }

}