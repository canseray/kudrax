package tr.limonist.classes;

import java.io.Serializable;

public class OrderHistoryProductItem implements Serializable {
    private String name;
    private String image;
    private String quantity;
    private String total;

    public OrderHistoryProductItem(String name, String image, String quantity, String total) {

        this.name = name;
        this.image = image;
        this.quantity = quantity;
        this.total = total;
    }


    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getTotal() {
        return total;
    }


    //$request_id."[#]"
    // .$request_date."[#]".
    // $request_order_id."[#]"
    // .$request_total."[#]".
    // $request_status_name."[#]"
    //            .$request_order_details."[#]"
    //            .$request_status_id."[#]".
    //            $request_order_payment_method;
}