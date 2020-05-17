package ie.sharkie.cryptr.crypto;

import android.util.Log;

import java.security.AlgorithmParameters;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ie.sharkie.cryptr.utility.Base64Util;

public class KeyHandler {

    private KeyGen keyGen;
    private KeyAgreement keyAgreement;
    private byte[] keySecret;
    private Base64Util base64;

    public KeyHandler() {
        keyGen = new KeyGen();
        base64 = new Base64Util();
    }

    public byte[] genKeyAgreement() {
        return genKeyAgreement(null);
    }

    public byte[] genKeyAgreement(byte[] key) {

        try {
            keyAgreement = keyGen.genKeyAgreement(key);
            return keyGen.getPubKeyBytes();
        } catch (Exception e) {
            Log.e("genKeyAgreement", e.toString());
            keyAgreement = null;
            return null;
        }

    }

    public void genSecret(byte[] key) {
        try {
            keyAgreement.doPhase(keyGen.getKeyFromBytes(key), true);
            keySecret = keyAgreement.generateSecret();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public byte[] getSecret() {
        if (keySecret == null) {
            keySecret = keyAgreement.generateSecret();
        }

        return keySecret;
    }

    public String encrypt(String msg, byte[] byteKey) {
        try {
            SecretKeySpec secKey = new SecretKeySpec(byteKey, 0, 16, "AES");

            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.ENCRYPT_MODE, secKey);

            byte[] encodedParams = aesCipher.getParameters().getEncoded();

            byte[] encodedMsg = aesCipher.doFinal(msg.getBytes());
            String params, msgStr;

            params = new String(base64.toBase64(encodedParams));
            msgStr = new String(base64.toBase64(encodedMsg));

            return msgStr + ":" + params;
        } catch (Exception e) {
            Log.wtf("encrypt", e.toString());
            return null;
        }
    }

    public String decrypt(String msg, byte[] byteKey) {
        try {
            byte[] params, msgBytes;
            msgBytes = base64.fromBase64(msg.split(":")[0]);
            params = base64.fromBase64(msg.split(":")[1]);

            AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
            aesParams.init(params);

            SecretKeySpec secKey = new SecretKeySpec(byteKey, 0, 16, "AES");

            Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCipher.init(Cipher.DECRYPT_MODE, secKey, aesParams);

            return new String(aesCipher.doFinal(msgBytes));
        } catch (Exception e) {
            Log.wtf("decrypt", e.toString());
            return null;
        }

    }

}
