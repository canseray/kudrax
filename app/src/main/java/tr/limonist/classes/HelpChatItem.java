package tr.limonist.classes;

import java.io.Serializable;

public class HelpChatItem implements Serializable {
    private String sendertype;
    private String date;
    private String text;
    private String id;

    public HelpChatItem(String sendertype, String date, String text, String id) {
        this.sendertype = sendertype;
        this.date = date;
        this.text = text;
        this.id = id;
    }

    public String getSendertype() {
        return sendertype;
    }

    public void setSendertype(String sendertype) {
        this.sendertype = sendertype;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
