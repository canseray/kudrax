package tr.limonist.classes;

import java.io.Serializable;

public class HelpChatItem implements Serializable {
    private String sendertype;
    private String date;
    private String message;
    private String messageid;

    public HelpChatItem(String sendertype, String date, String message, String messageid) {
        this.sendertype = sendertype;
        this.date = date;
        this.message = message;
        this.messageid = messageid;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }
}
