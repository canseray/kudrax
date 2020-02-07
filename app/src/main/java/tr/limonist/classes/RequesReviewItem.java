package tr.limonist.classes;

import java.io.Serializable;

public class RequesReviewItem implements Serializable {

    private String id;
    private String title;

    public RequesReviewItem(String id, String title) {

        this.id = id;
        this.title = title;

    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }


}