package ie.sharkie.cryptr.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ie.sharkie.cryptr.data.Conversation;
import ie.sharkie.cryptr.utility.ReadFile;

public class ConvoDbControl extends DbControl {

    public ConvoDbControl(Context c) {
        super(c);
    }

    public List<Conversation> selectAllConvo() {
        return selectConversation(null, null);
    }

    public Conversation selectConvoByID(int Convo_ID) {
        return selectConversation(null, Convo_ID).get(0);
    }

    private List<Conversation> selectConversation(Integer profile_ID, Integer convo_ID) {
        List<Conversation> result = new ArrayList<>();
        String name = "selectConvo", sqlFile;
        final int SELECT_ALL = 0, SELECT_CONVO = 1;

        try {
            ReadFile readFile = new ReadFile(context);

            String[] sqlFileAll = readFile.returnAssetAsString(name + ".sql").split(";");
            Cursor cursor;

            if (convo_ID != null) {

                sqlFile = sqlFileAll[SELECT_CONVO];
                cursor = db.rawQuery(sqlFile, new String[]{String.valueOf(convo_ID)});

            } else {

                sqlFile = sqlFileAll[SELECT_ALL];
                cursor = db.rawQuery(sqlFile, null);

            }

            System.out.println(sqlFile);

            cursor.moveToFirst();

            Log.wtf("CONVO CURSOR", String.valueOf(cursor.getCount()));

            Conversation c;
            do {
                c = new Conversation();

                c.setID(cursor.getInt(0));
                c.setName(cursor.getString(1));
                c.setStage(cursor.getInt(2));
                c.setKey(cursor.getBlob(3));

                result.add(c);

            } while (cursor.moveToNext());

            cursor.close();

        } catch (Exception e) {
            Log.wtf("Error in " + name, e.toString() + "  " + Arrays.toString(e.getStackTrace()));
        }

        return result;
    }

    public boolean deleteConvo(Conversation c) {
        return deleteConvo(c.getID());
    }

    public boolean deleteConvo(int ConvoID) {
        String name = "deleteConvo";

        try {
            ReadFile readFile = new ReadFile(context);

            String sqlFile = readFile.returnAssetAsString(name + ".sql");

            db.execSQL(sqlFile, new String[]{String.valueOf(ConvoID)});
        } catch (Exception e) {
            Log.wtf("Error in " + name, e.toString());
            return false;
        }

        return true;
    }

    public boolean insertConvo(Conversation c) {
        String name = "insertConvo";

        try {
            ReadFile readFile = new ReadFile(context);

            String sqlFile = readFile.returnAssetAsString(name + ".sql");

            insertUpdate(null, c, sqlFile);

        } catch (Exception e) {
            Log.wtf("Error in " + name, e.toString());
            return false;
        }

        return true;
    }


    public boolean updateConvo(int ConvoID, Conversation c) {
        String name = "updateConvo";

        try {
            ReadFile readFile = new ReadFile(context);

            String sqlFile = readFile.returnAssetAsString(name + ".sql");

            insertUpdate(ConvoID, c, sqlFile);

        } catch (Exception e) {
            Log.wtf("Error in " + name, e.toString());
            return false;
        }

        return true;
    }

    private void insertUpdate(Integer ConvoID, Conversation convo, String sqlFile) {
        SQLiteStatement queryState = db.compileStatement(sqlFile);
        int num = 1;

        queryState.bindString(num++, convo.getName());
        queryState.bindDouble(num++, convo.getStage());
        queryState.bindBlob(num++, convo.getKey());

        if (ConvoID != null) {
            queryState.bindDouble(num, ConvoID);
            queryState.executeUpdateDelete();
        } else {
            queryState.executeInsert();
        }
    }


}
