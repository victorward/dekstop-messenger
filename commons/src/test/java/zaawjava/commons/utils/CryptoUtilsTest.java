package zaawjava.commons.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class CryptoUtilsTest {
    @Test
    public void encryptPasswordTest() throws Exception {
        String encrypted = CryptoUtils.encryptPassword("passwordToEncrypt");

        String decrypted = CryptoUtils.decryptPassword(encrypted);

        assertThat(decrypted).isEqualTo("passwordToEncrypt");
    }

}