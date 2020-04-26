package com.example.goodspot.Model;

public class ItemsMark {
    private String name;
    private String desc;
    private String type;
    private int image;

    public ItemsMark(String name, String desc, String type, int image) {
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
