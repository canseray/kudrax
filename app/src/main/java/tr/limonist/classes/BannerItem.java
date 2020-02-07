package tr.limonist.classes;

import java.io.Serializable;

public class BannerItem implements Serializable {
    private String id;
    private String title;
    private String image;
    private String content;

    public BannerItem(String id, String title, String image, String content) {
        this.title = title;
        this.image = image;
        this.id = id;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

}
