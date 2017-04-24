package zaawjava.Utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Yuriy
 */
public class Utils {
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
