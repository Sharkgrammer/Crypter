package ie.sharkie.cryptr.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ie.sharkie.cryptr.R;
import ie.sharkie.cryptr.controller.ConvoDbControl;
import ie.sharkie.cryptr.crypto.KeyHandler;
import ie.sharkie.cryptr.data.Conversation;
import ie.sharkie.cryptr.recycler.MainAdapter;
import ie.sharkie.cryptr.service.HeadService;

public class MainActivity extends AppCompatActivity {

    private MainAdapter adapter;
    private ConvoDbControl db;
    private List<Conversation> list;
    private Intent service;
    private int START_SERVICE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        service = new Intent(MainActivity.this, HeadService.class);

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please accept the overlay permission", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, START_SERVICE);
            } else {
                startService(service);
            }
        } else {
            startService(service);
        }

        db = new ConvoDbControl(this);

        if (!db.databaseExists()) {
            db.createTables();
        }

        list = db.selectAllConvo();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(list, this);
        recyclerView.setAdapter(adapter);
    }

    public void goToSetup(View v) {
        startActivity(new Intent(MainActivity.this, SetupActivity.class));
    }

    public void onResume() {
        super.onResume();
        resetUI();
    }

    public void resetUI() {
        checkClipboard();

        int pastInt = list.size();
        list = db.selectAllConvo();

        if (pastInt != list.size()) adapter.notifyDataSetChanged();
    }

    public void checkClipboard() {
        String msg = "";

        String pattern = "(\\w+)(=*)\\:(\\w+)(=*)";
        Pattern r = Pattern.compile(pattern);
        boolean found = false;

        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
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


            if (found) {
                KeyHandler handler = new KeyHandler();

                for (Conversation c : list) {
                    String res = handler.decrypt(msg, c.getKey());

                    if (res != null) {
                        adapter.displayViaID(res, c.getID());
                        return;
                    }
                }

            }

        } catch (Exception e) {
            Log.wtf("checkClipboard", e.toString());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_SERVICE) {
            startService(service);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(service);
    }

}














