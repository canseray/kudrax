package tr.limonist.classes;

import java.io.Serializable;

public class ProductTempList implements Serializable {

    private String product_id;
    private String product_name;
    private String product_desc;
    private String product_content;
    private String product_image;
    private String product_quantities;
    private String product_max_quantities;
    private String product_min_quantities;

    public ProductTempList(String product_id,
                           String product_name,
                           String product_desc,
                           String product_content,
                           String product_image,
                           String product_quantities,
                           String product_max_quantities,
                           String product_min_quantities) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product_content = product_content;
        this.product_image = product_image;
        this.product_quantities = product_quantities;
        this.product_max_quantities = product_max_quantities;
        this.product_min_quantities = product_min_quantities;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getProduct_content() {
        return product_content;
    }

    public void setProduct_content(String product_content) {
        this.product_content = product_content;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_quantities() {
        return product_quantities;
    }

    public void setProduct_quantities(String product_quantities) {
        this.product_quantities = product_quantities;
    }

    public String getProduct_max_quantities() {
        return product_max_quantities;
    }

    public void setProduct_max_quantities(String product_max_quantities) {
        this.product_max_quantities = product_max_quantities;
    }

    public String getProduct_min_quantities() {
        return product_min_quantities;
    }

    public void setProduct_min_quantities(String product_min_quantities) {
        this.product_min_quantities = product_min_quantities;
    }
}
