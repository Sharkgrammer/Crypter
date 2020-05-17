package ie.sharkie.cryptr.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ie.sharkie.cryptr.R;
import ie.sharkie.cryptr.data.Conversation;
import ie.sharkie.cryptr.utility.Base64Util;

public class MainAdapter extends RecyclerView.Adapter<MainViewHolder> {

    private List<Conversation> listData;
    private Context context;

    public MainAdapter(List<Conversation> listData, Context c) {
        this.listData = listData;
        this.context = c;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View Child = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null, false);
        Child.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new MainViewHolder(Child, context, this);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int pos) {
        final Conversation data = listData.get(pos);
        Base64Util base64 = new Base64Util();
        holder.setPos(pos);
        holder.setName(data.getName());
        holder.setKey(new String(base64.toBase64(data.getKey())));
        holder.setOnClick(data);

        int dataPos = listData.indexOf(data);
        data.setHolder(holder);

        listData.set(dataPos, data);
    }

    @Override
    public int getItemCount() {
        try {
            return listData.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void ViewHolderUpdate(int pos) {
        listData.remove(pos);
        this.notifyDataSetChanged();
    }

    public void displayViaID(String msg, int ID) {

        for (Conversation c : listData) {
            if (c.getID() == ID) {
                c.getHolder().encryptDialog(c);
                c.getHolder().displayMessage(msg);
            }
        }
        
    }

}
