package ie.sharkie.cryptr.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ie.sharkie.cryptr.R;
import ie.sharkie.cryptr.controller.ConvoDbControl;
import ie.sharkie.cryptr.crypto.KeyHandler;
import ie.sharkie.cryptr.data.Conversation;
import ie.sharkie.cryptr.utility.Base64Util;

public class SetupActivity extends AppCompatActivity {

    private Button btnGen;
    private TextView lblKey;
    private ConstraintLayout disLay;
    private ProgressBar loading;
    private KeyHandler handler;
    private String mode, del = ":", lastMessage;
    private Base64Util base64;
    private TextView txtName;
    private boolean save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        handler = new KeyHandler();
        base64 = new Base64Util();
        save = false;

        btnGen = findViewById(R.id.btnGen);
        lblKey = findViewById(R.id.lblKey);
        disLay = findViewById(R.id.layDisplay);
        loading = findViewById(R.id.layLoading);
        txtName = findViewById(R.id.txtName);

        checkClipboard();
    }


    public void copyKey(View v) {

        lastMessage = mode + del + lblKey.getText().toString();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied text", lastMessage);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Key copied!", Toast.LENGTH_SHORT).show();
    }

    public void genKey(View v) {

        if (save) {
            ConvoDbControl db = new ConvoDbControl(this);
            Conversation c = new Conversation();

            String name = txtName.getText().toString();

            if (name.length() == 0) {
                Toast.makeText(this, "Please enter a conversation name", Toast.LENGTH_SHORT).show();
                return;
            }

            c.setName(name);
            c.setStage(3);
            c.setKey(handler.getSecret());

            db.insertConvo(c);

            onBackPressed();
        } else {
            Toast.makeText(this, "This may take up to a minute", Toast.LENGTH_LONG).show();
            generateKey(null);
        }

    }

    public void generateKey(byte[] key) {
        final HandlerThread t = new HandlerThread("key");

        Runnable run = () -> {
            byte[] newKey;

            if (key == null) {
                newKey = handler.genKeyAgreement();
                mode = "1";
            } else {
                newKey = handler.genKeyAgreement(key);
                mode = "2";
            }

            this.runOnUiThread(() -> showFinalUI(newKey));

        };

        setLoading();
        t.start();

        Handler h = new Handler(t.getLooper());
        h.post(run);

        t.quitSafely();
    }

    public void setLoading() {
        loading.setVisibility(View.VISIBLE);
        btnGen.setVisibility(View.INVISIBLE);
    }

    public void showFinalUI(byte[] key) {
        String res = new String(base64.toBase64(key));

        lblKey.setText(res);
        btnGen.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        disLay.setVisibility(View.VISIBLE);
    }

    public void showSaveButton() {
        btnGen.setVisibility(View.VISIBLE);
        btnGen.setText(R.string.save);
        save = true;
    }

    public void onResume() {
        super.onResume();
        checkClipboard();
    }


    public void checkClipboard() {
        String key = "";
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                key = item.getText().toString();
            }

            if (key.equals("") || key.equals(lastMessage)) {
                return;
            }

            String[] keyArr = key.split(":");
            String keyMode = keyArr[0];
            byte[] inputKey;

            switch (keyMode) {
                case "1":
                    inputKey = base64.fromBase64(keyArr[1]);

                    showFinalUI(handler.genKeyAgreement(inputKey));
                    handler.genSecret(inputKey);
                    mode = "2";
                    Toast.makeText(this, "After sending this you can click save", Toast.LENGTH_LONG).show();
                    showSaveButton();
                    break;
                case "2":
                    inputKey = base64.fromBase64(keyArr[1]);
                    handler.genSecret(inputKey);
                    Toast.makeText(this, "Click save and you're good to go", Toast.LENGTH_LONG).show();
                    showSaveButton();
                    break;
            }

            ClipData clip = ClipData.newPlainText("Copied text", "");
            clipboard.setPrimaryClip(clip);

        } catch (Exception e) {
            Toast.makeText(this, "Clipboard read incorrectly", Toast.LENGTH_LONG).show();
            Log.wtf("checkClipboard", e.toString());
        }

    }

}
