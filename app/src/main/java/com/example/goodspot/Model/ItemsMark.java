package com.example.goodspot.Model;

//Class pour le recyclerView - Layout
public class ItemsMark {
    private String name;
    private String desc;
    private String type;
    private String link;
    private int image;

    public ItemsMark(String name, String desc, String type, String link, int ima) {
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.link = link;
        this.image = ima;
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

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
