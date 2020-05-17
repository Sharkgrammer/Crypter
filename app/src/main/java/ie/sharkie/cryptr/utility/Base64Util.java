package ie.sharkie.cryptr.utility;

import android.os.Build;
import android.util.Base64;

public class Base64Util {

    public byte[] toBase64(String str) {
        return android.util.Base64.encode(str.getBytes(), android.util.Base64.NO_WRAP);
    }

    public byte[] toBase64(byte[] str) {
        return android.util.Base64.encode(str, android.util.Base64.NO_WRAP);
    }

    public byte[] fromBase64(String str) {
        return android.util.Base64.decode(str.getBytes(), android.util.Base64.NO_WRAP);
    }

    public byte[] fromBase64(byte[] str) {
        return android.util.Base64.decode(str, android.util.Base64.NO_WRAP);
    }

}
