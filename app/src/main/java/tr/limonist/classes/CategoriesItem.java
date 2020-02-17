package tr.limonist.classes;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoriesItem implements Serializable {
    private String id;
    private String title;
    private String image;

    public CategoriesItem(String id, String title, String image) {

        this.id = id;
        this.title = title;
        this.image = image;

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

}