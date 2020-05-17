package ie.sharkie.cryptr.crypto;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class KeyGen {

    private KeyPair keyPair;

    public byte[] getPubKeyBytes() {
        return keyPair.getPublic().getEncoded();
    }

    public void genKeyPair(DHParameterSpec params) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyG = KeyPairGenerator.getInstance("DH");

        if (params == null) {
            keyG.initialize(512);
        } else {
            keyG.initialize(params);
        }

        keyPair = keyG.generateKeyPair();
    }

    public KeyAgreement genKeyAgreement(byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeySpecException {

        if (keyPair == null) {
            DHParameterSpec spec = null;
            if (key != null) {
                PublicKey pub = getKeyFromBytes(key);
                spec = ((DHPublicKey) pub).getParams();
            }
            genKeyPair(spec);
        }

        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(keyPair.getPrivate());

        return keyAgreement;
    }

    public PublicKey getKeyFromBytes(byte[] inKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(inKey);

        return keyFactory.generatePublic(x509KeySpec);
    }

}















