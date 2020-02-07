package tr.limonist.classes;

import java.io.Serializable;

public class OrderParams implements Serializable {

    private String total_payment_amount;
    private String cc_information_string;
    private String order_money_point;
    private String product_id;
    private String product_quantity;
    private String cart_type;

    public OrderParams() {

        total_payment_amount = "";
        cc_information_string = "";
        order_money_point = "";
        product_id = "";
        product_quantity = "";
        cart_type = "";
    }

    public String getCart_type() {
        return cart_type;
    }

    public void setCart_type(String cart_type) {
        this.cart_type = cart_type;
    }

    public String getTotal_payment_amount() {
        return total_payment_amount;
    }

    public void setTotal_payment_amount(String total_payment_amount) {
        this.total_payment_amount = total_payment_amount;
    }

    public String getCc_information_string() {
        return cc_information_string;
    }

    public void setCc_information_string(String cc_information_string) {
        this.cc_information_string = cc_information_string;
    }

    public String getOrder_money_point() {
        return order_money_point;
    }

    public void setOrder_money_point(String order_money_point) {
        this.order_money_point = order_money_point;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }
}