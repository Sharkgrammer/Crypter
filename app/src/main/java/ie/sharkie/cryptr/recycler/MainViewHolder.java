package ie.sharkie.cryptr.recycler;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ie.sharkie.cryptr.R;
import ie.sharkie.cryptr.controller.ConvoDbControl;
import ie.sharkie.cryptr.crypto.KeyHandler;
import ie.sharkie.cryptr.data.Conversation;
import ie.sharkie.cryptr.utility.Base64Util;

public class MainViewHolder extends RecyclerView.ViewHolder {

    private TextView txtName, txtKey, lblMessage;
    private ConstraintLayout layMain;
    private int pos;
    private Context context;
    private MainAdapter adapter;
    private KeyHandler handler;
    private Base64Util base64;
    private Button copy;

    public MainViewHolder(View itemView, Context context, MainAdapter adapter) {
        super(itemView);

        this.adapter = adapter;
        this.context = context;
        this.layMain = itemView.findViewById(R.id.layMain);
        this.txtKey = itemView.findViewById(R.id.txtKey);
        this.txtName = itemView.findViewById(R.id.txtName);
        this.handler = new KeyHandler();
        this.base64 = new Base64Util();
    }

    public void setName(String name) {
        txtName.setText(name);
    }

    public void setKey(String key) {
        txtKey.setText(key);
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public void setOnClick(final Conversation c) {

        layMain.setOnClickListener(view -> {
            encryptDialog(c);
        });

        layMain.setOnLongClickListener(view -> {
            deleteDialog(c);
            return true;
        });

    }

    public void encryptDialog(final Conversation c) {

        LayoutInflater li = LayoutInflater.from(context);
        View dialog = li.inflate(R.layout.encrypt_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(dialog);

        final EditText txtMessage = dialog.findViewById(R.id.txtMessage);
        lblMessage = dialog.findViewById(R.id.lblMessage);

        copy = dialog.findViewById(R.id.btnCopy);
        Button encrypt = dialog.findViewById(R.id.btnEncrypt);
        Button decrypt = dialog.findViewById(R.id.btnDecrypt);

        final AlertDialog alert = alertDialogBuilder.show();

        copy.setOnClickListener(view -> {
            copyToClipboard(lblMessage.getText().toString(), alert);
        });

        encrypt.setOnClickListener(view -> {

            String msg = txtMessage.getText().toString();

            if (msg.length() == 0) {
                Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                String resStr = handler.encrypt(msg, c.getKey());

                copyToClipboard(resStr, alert);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        decrypt.setOnClickListener(view -> {
            String msg = "";

            String pattern = "(\\w+)(=*)\\:(\\w+)(=*)";
            Pattern r = Pattern.compile(pattern);
            boolean found = false;

            try {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {

                    int count = clipboard.getPrimaryClip().getItemCount();

                    for (int x = 0; x < count; x++) {
                        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(x);
                        msg = item.getText().toString();

                        Matcher m = r.matcher(msg);

                        if (m.find()) {
                            found = true;
                            break;
                        }

                    }

                }

                if (msg.equals("") || !found) {
                    return;
                }

                msg = handler.decrypt(msg, c.getKey());

                displayMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void deleteDialog(final Conversation c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Conversation");
        builder.setMessage("'" + c.getName() + "' will be deleted. Are you sure?\nDeleted keys cannot be recovered");

        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {

            ConvoDbControl dbControl = new ConvoDbControl(context);
            boolean done = dbControl.deleteConvo(c);
            dbControl.destroy();

            Toast.makeText(context, "Delete " + (done ? "Success" : "Failure"), Toast.LENGTH_LONG).show();
            if (done) adapter.ViewHolderUpdate(pos);
        });

        builder.setNegativeButton(android.R.string.no, null);
        builder.show();
    }

    public void copyToClipboard(String msg, AlertDialog alert) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied text", msg);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "Message copied!", Toast.LENGTH_LONG).show();

        alert.dismiss();
    }

    public void displayMessage(String msg) {
        lblMessage.setText(msg);
        copy.setVisibility(View.VISIBLE);
        copy.setText(R.string.copyMsg);
    }

}