package tr.limonist.classes;

import java.io.Serializable;

public class MainItem implements Serializable {

    private String id;
    private String title;
    private String image;
    private String prep;
    private String badge;
    private String accountStatus;

    public MainItem(String id, String title, String image, String prep, String badge, String accountStatus) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.prep = prep;
        this.badge = badge;
        this.accountStatus = accountStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrep() {
        return prep;
    }

    public void setPrep(String prep) {
        this.prep = prep;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}