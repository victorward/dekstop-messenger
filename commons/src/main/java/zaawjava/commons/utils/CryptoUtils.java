package zaawjava.commons.utils;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Created by Yuriy
 */
public class CryptoUtils {
    public static String encryptPassword(String pass){
//        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//        encryptor.setPassword("zaawjava");
//        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword("zaawjava");
        return encryptor.encrypt(pass);
    }

    public static String decryptPassword(String pass){
//        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//        encryptor.setPassword("zaawjava");
//        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword("zaawjava");
        return encryptor.decrypt(pass);
    }


}
