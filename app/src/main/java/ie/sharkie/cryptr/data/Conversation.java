package ie.sharkie.cryptr.data;

import ie.sharkie.cryptr.recycler.MainViewHolder;

public class Conversation {

    private int ID;
    private String name;
    private int stage;
    private byte[] key;
    private MainViewHolder holder;

    public Conversation() {
    }

    public Conversation(int id, String name, int stage, byte[] key) {
        this.ID = id;
        this.name = name;
        this.stage = stage;
        this.key = key;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public MainViewHolder getHolder() {
        return holder;
    }

    public void setHolder(MainViewHolder holder) {
        this.holder = holder;
    }
}
