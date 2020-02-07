package tr.limonist.classes;

import java.io.Serializable;

public class MainItem implements Serializable {

    private String id;
    private String title;
    private String image;
    private String prep;
    private String badge;
    private String accountable;
    private String message;
    private String promotion;

    public MainItem(String id, String title, String image, String prep, String badge, String message, String accountable, String promotion) {

        this.id = id;
        this.title = title;
        this.image = image;
        this.prep = prep;
        this.badge = badge;
        this.accountable = accountable;
        this.message = message;
        this.promotion = promotion;

    }

    public String getPromotion() {
        return promotion;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getPrep() {
        return prep;
    }

    public String getBadge() {
        return badge;
    }

    public String getAccountable() {
        return accountable;
    }

    public String getMessage() {
        return message;
    }
}