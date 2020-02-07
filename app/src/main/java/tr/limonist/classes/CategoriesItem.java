package tr.limonist.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoriesItem implements Serializable {
    private String id;
    private String title;
    private String sub;
    private String image;

    public CategoriesItem(String id, String title, String sub, String image) {

        this.id = id;
        this.title = title;
        this.image = image;
        this.sub = sub;

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

    public String getSub() {
        return sub;
    }

}