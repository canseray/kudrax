package tr.limonist.kudra.app.cart;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.devmarvel.creditcardentry.library.CardValidCallback;
import com.devmarvel.creditcardentry.library.CreditCard;
import com.devmarvel.creditcardentry.library.CreditCardForm;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.twotoasters.jazzylistview.JazzyListView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.kudra.app.main.Main;
import tr.limonist.classes.PaymentTypeItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.views.MyAlertDialog;
import tr.limonist.views.MyContractDialog;
import tr.limonist.views.MyInfoDialog;
import tr.limonist.views.MyPromotionUsageDialog;

public class CheckOut extends Activity {

    public String[] part1;
    private lazy adapter;
    ArrayList<PaymentTypeItem> results;
    ArrayList<String> results_ids, results_names;
    JazzyListView list;
    RefreshLayout refreshLayout;
    private Activity m_activity;
    private TransparentProgressDialog pd;
    private MyTextView tv_total;
    private int selected_address_pos = -1, selected_payment_pos = -1;
    private String[] part2;
    private String total, currency, item_count;
    private AnimCheckBox scb;
    private String conPart1;
    private String send1, send2, send3, send4, send5;
    MyTextView tv_done;
    String total_point_amount="0";
    private MyTextView tv_used_point;

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                pd.show();
                new Connection().execute();

            }
        }, 250);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = CheckOut.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);
        item_count = getIntent().getStringExtra("item_count");
        total = getIntent().getStringExtra("total");
        currency = getIntent().getStringExtra("currency");
        setContentView(R.layout.z_checkout);

        ViewStub stub = findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_emp);
        stub.inflate();

        MyTextView tv_baslik = findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_black11));
        tv_baslik.setText(getString(R.string.s_your_cart));

        ImageView img_left = findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.b_ic_prew_black);

        LinearLayout top_left = findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new WaterDropHeader(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                new Connection().execute("");
            }
        });

        tv_done = (MyTextView) findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final MyAlertDialog alert = new MyAlertDialog(m_activity, 0,
                        getString(R.string.s_new_order), getString(R.string.s_your_order_request_will_be_send),
                        getString(R.string.s_send), getString(R.string.s_cancel), true);

                alert.setNegativeClicl(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alert.dismiss();

                    }
                });
                alert.setPositiveClicl(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alert.dismiss();

                        c_number = c_number.replace(" ", "");
                        cc_info = c_number + "," + c_month + "," + "/20" + c_year + "," + c_security + "," + c_type + "," + c_last_for;
                        cc_info = APP.base64Encode(APP.base64Encode(APP.base64Encode(APP.base64Encode(APP.base64Encode(cc_info)))));

                        pd.show();
                        new Connection3().execute("");

                    }
                });
                alert.show();

            }
        });

        list = findViewById(R.id.list);
        list.setEmptyView(findViewById(android.R.id.empty));

        tv_total = (MyTextView) findViewById(R.id.tv_total);
        tv_used_point = (MyTextView) findViewById(R.id.tv_used_point);

    }

    View head, footer;

    class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            results = new ArrayList<>();
            results_ids = new ArrayList<>();
            results_names = new ArrayList<>();

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(total)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/get_checkout_data_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        part2 = APP.base64Decode(APP.getElement(parse, "part2")).split("\\[##\\]");
                    }

                    if (!part2[0].contentEquals("")) {

                        for (int i = 0; i < part2.length; i++) {
                            String[] temp = part2[i].split("\\[#\\]");
                            PaymentTypeItem ai = new PaymentTypeItem(temp.length > 0 ? temp[0] : "",
                                    temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "");
                            results.add(ai);
                        }

                    }

                    if (!part1[0].contentEquals("")) {

                        results_ids.add("0");
                        results_names.add(getString(R.string.s_eelect_address));

                        for (int i = 0; i < part1.length; i++) {
                            String[] temp = part1[i].split("\\[#\\]");

                            results_ids.add(temp.length > 0 ? temp[0] : "");
                            results_names.add(temp.length > 1 ? temp[1] : "");
                        }

                    }

                    return "true";

                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }
        }

        protected void onPostExecute(String result) {
            if (pd != null)
                pd.dismiss();
            if (refreshLayout != null)
                refreshLayout.finishRefresh();
            if (result.contentEquals("true")) {

                fillComponents();

            } else {
                APP.show_status(m_activity, 1, getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    private void fillComponents() {

        tv_total.setText(total + " " + currency);

        selected_address_pos = 0;
        selected_payment_pos = -1;

        if (head != null)
            list.removeHeaderView(head);

        LayoutInflater layoutInflater = LayoutInflater.from(m_activity);
        head = layoutInflater.inflate(R.layout.c_item_head_checkout, null);

        footer = layoutInflater.inflate(R.layout.c_item_footer_checkout, null);

        LinearLayout lay_address = (LinearLayout) head.findViewById(R.id.lay_address);
        lay_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(m_activity, MyAddresses.class).putExtra("title", getString(R.string.s_my_addresses)));
            }
        });

        MaterialSpinner spinner = (MaterialSpinner) head.findViewById(R.id.spinner);

        spinner.setItems(new ArrayList<String>());
        spinner.setItems(results_names);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selected_address_pos = position;
                checkDone();

            }
        });
        spinner.setOnNothingSelectedListener(new MaterialSpinner.OnNothingSelectedListener() {

            @Override
            public void onNothingSelected(MaterialSpinner spinner) {
                selected_address_pos = 0;
            }
        });

        spinner.invalidate();

        list.addHeaderView(head);

        adapter = new lazy();
        list.setAdapter(adapter);

        checkDone();

    }

    boolean is_check;
    public String c_type = "", c_number = "", c_year = "", c_month = "", c_security = "", c_last_for = "";
    protected String cc_info = "";
    CreditCardForm zipForm;
    CardValidCallback cardValidCallback = new CardValidCallback() {
        @Override
        public void cardValid(CreditCard card) {
            if (card.getCardNumber() != null && card.getCardType() != null && card.getExpDate() != null
                    && card.getSecurityCode() != null) {
                c_type = card.getCardType().toString();
                if (c_type.contentEquals("VISA"))
                    c_type = "Visa";
                if (c_type.contentEquals("MASTERCARD"))
                    c_type = "MasterCard";
                c_number = card.getCardNumber();
                c_number = c_number.replace(" ", "");
                c_year = card.getExpYear().toString();
                c_month = card.getExpMonth().toString();
                c_security = card.getSecurityCode();
                c_last_for = c_number.substring(c_number.length() - 4);

                checkDone();

            }

        }
    };

    private void checkDone() {

        if (selected_address_pos > 0 && selected_payment_pos > -1) {

            if (results.get(selected_payment_pos).getType().contentEquals("2")) {
                if (is_check && zipForm.isCreditCardValid()) {
                    tv_done.setEnabled(true);
                    tv_done.setAlpha(1.0f);
                } else {
                    tv_done.setEnabled(false);
                    tv_done.setAlpha(0.5f);
                }
            } else {
                tv_done.setEnabled(true);
                tv_done.setAlpha(1.0f);
            }
        } else {
            tv_done.setEnabled(false);
            tv_done.setAlpha(0.5f);
        }
    }

    public class lazy extends BaseAdapter {
        private LayoutInflater inflater = null;

        public lazy() {
            inflater = LayoutInflater.from(m_activity);
        }

        @Override
        public int getCount() {
            return results.size();
        }

        @Override
        public PaymentTypeItem getItem(int position) {
            return results.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            MyTextView title;
            AnimCheckBox scb;
            LinearLayout lay_main;

        }

        public View getView(final int position, View view, ViewGroup parent) {
            final PaymentTypeItem item = results.get(position);

            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.c_item_checkout, null);

                holder.title = (MyTextView) view.findViewById(R.id.title);
                holder.scb = (AnimCheckBox) view.findViewById(R.id.scb);
                holder.lay_main = (LinearLayout) view.findViewById(R.id.lay_main);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.title.setText(item.getName());

            holder.scb.setClickable(false);
            holder.scb.setEnabled(false);

            if (selected_payment_pos == position) {
                holder.scb.setChecked(true, true);
            } else {
                holder.scb.setChecked(false);
            }

            if (item.getActive().contentEquals("1")) {
                view.setEnabled(true);
                holder.lay_main.setAlpha(1.0f);
            } else {
                view.setEnabled(false);
                holder.lay_main.setAlpha(0.3f);
            }

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selected_payment_pos != position) {
                        selected_payment_pos = position;
                        adapter.notifyDataSetChanged();

                        if (item.getType().contentEquals("2")) {
                            addFooterView();
                        } else {
                            if (footer != null)
                                list.removeFooterView(footer);
                        }

                        if (item.getType().contentEquals("4")) {
                            final MyPromotionUsageDialog alert = new MyPromotionUsageDialog(m_activity, getString(R.string.s_app_point), total);
                            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    total_point_amount = alert.used_point;
                                    if(!total_point_amount.contentEquals("0"))
                                    {
                                        tv_used_point.setText(getString(R.string.s_used_point_amount_x,total_point_amount));
                                        tv_used_point.setVisibility(View.VISIBLE);
                                    }
                                    else
                                        tv_used_point.setVisibility(View.GONE);

                                }
                            });
                            alert.show();
                        }


                        checkDone();
                    }
                }
            });

            return view;
        }

    }

    private void addFooterView() {

        is_check = false;

        zipForm = (CreditCardForm) footer.findViewById(R.id.credit_card_form);
        zipForm.setOnCardValidCallback(cardValidCallback);

        scb = (AnimCheckBox) footer.findViewById(R.id.scb);

        scb.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(AnimCheckBox view, boolean checked) {
                is_check = checked;
                checkDone();
            }
        });

        scb.setChecked(is_check, true);

        MyTextView tv_read = (MyTextView) footer.findViewById(R.id.tv_read);
        tv_read.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                pd.show();
                new Connection2().execute("");
            }
        });

        list.addFooterView(footer);

    }

    class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode("2")));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_contract_data.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        conPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                    }

                    if (!conPart1.contentEquals("")) {
                        return "true";
                    } else {
                        return "false";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }
        }

        protected void onPostExecute(String result) {
            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {
                new MyContractDialog(m_activity, conPart1, getString(R.string.s_contract));
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    final int PAYMENT_CODE = 0;

    public void onActivityResult(int requestCode, int resultCode, Intent intent2) {

        if (requestCode == PAYMENT_CODE) {

            if (resultCode == RESULT_OK) {

                MyInfoDialog alert = new MyInfoDialog(m_activity, send2);
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        Intent intent = new Intent(m_activity, Main.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(intent);
                    }
                });
                alert.show();

            } else if (resultCode == RESULT_FIRST_USER) {
                if (intent2.hasExtra("msg"))
                    APP.show_status(m_activity, 1, intent2.getStringExtra("msg"));
                else
                    APP.show_status(m_activity, 1, getString(R.string.s_error_occured_when_pay));
            } else if (resultCode == RESULT_CANCELED) {
                APP.show_status(m_activity, 1, getString(R.string.s_error_occured_when_pay));
            }
        }
    }

    class Connection3 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(item_count)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(total)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(cc_info)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(results_ids.get(selected_address_pos))));
            nameValuePairs.add(new Pair<>("param6", APP.base64Encode(results.get(selected_payment_pos).getType())));
            nameValuePairs.add(new Pair<>("param7", APP.base64Encode(total_point_amount)));
            nameValuePairs.add(new Pair<>("param8", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param9", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/cart_controls/send_order_request.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        send1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        send2 = APP.base64Decode(APP.getElement(parse, "part2"));
                        send3 = APP.base64Decode(APP.getElement(parse, "part3"));
                        send4 = APP.base64Decode(APP.getElement(parse, "part4"));
                        send5 = APP.base64Decode(APP.getElement(parse, "part5"));

                    }

                    if (send1.contentEquals("OK"))
                        return "true";
                    else if (send1.contentEquals("FAIL"))
                        return "error";
                    else
                        return "hata";

                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }
        }

        protected void onPostExecute(String result) {
            if (pd != null)
                pd.dismiss();
            if (refreshLayout != null)
                refreshLayout.finishRefresh();
            if (result.contentEquals("true")) {

                if (!send5.contentEquals("")) {
                    startActivityForResult(new Intent(m_activity, PaymentWebPage.class).putExtra("params", send5), PAYMENT_CODE);
                } else {
                    APP.show_status_with_dismiss(m_activity, 0, send2, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Intent intent = new Intent(m_activity, Main.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            startActivity(intent);
                        }
                    });
                }

            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, send2);
            } else {
                APP.show_status(m_activity, 1,
                        getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

}
