package tr.limonist.kudra.app.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baoyz.actionsheet.ActionSheet;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;
import tr.limonist.classes.ProfileItem;
import tr.limonist.classes.USER;
import tr.limonist.extras.MultipartRequest;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.views.MyAlertDialog;
import tr.limonist.views.MyCropImageDialog;
import tr.limonist.views.MyZoomImageDialog;

public class ProfileSettings extends ActivityManagePermission {
    String s_mail, s_name, s_surname, s_phone;
    int keyDel;
    private TransparentProgressDialog pd;
    private Activity m_activity;
    private ImageView img_camera;
    SimpleDraweeView img;
    EZPhotoPickStorage ezPhotoPickStorage;
    ArrayList<ProfileItem> results;
    Uri new_image_uri;
    String new_image_name = "", new_image_path = "";
    private String sendPart1, sendPart2, sendPart3;
    private LinearLayout lay_main;
    private lazy adapter;
    private String respPart1,respPart2;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = ProfileSettings.this;
        APP.setWindowsProperties(m_activity, true);
        pd = new TransparentProgressDialog(m_activity, "", true);
        ezPhotoPickStorage = new EZPhotoPickStorage(m_activity);

        setContentView(R.layout.z_profile_settings);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_oval_txt_but);
        stub.inflate();

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setText(getString(R.string.s_my_account));
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));

        LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }

        });

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);

        MyTextView tv_right = (MyTextView) findViewById(R.id.tv_right);
        tv_right.setTextSize((int)getResources().getDimension(R.dimen.dp15));
        tv_right.setAllCaps(true);
        tv_right.setText(getString(R.string.s_save));
        tv_right.setBackgroundResource(R.drawable.but_oval_str_brown1_tra);
        tv_right.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                String required_field = "";
                boolean all_field = true;

                for (int i = 0; i < results.size(); i++) {

                    if (results.get(i).getRequire().contentEquals("1")
                            && results.get(i).getValue().trim().contentEquals("")) {
                        required_field = results.get(i).getTitle();
                        all_field = false;
                        break;
                    }

                    if (results.get(i).getType().contentEquals("1")) {
                        if (i == 0)
                            s_name = results.get(i).getValue();
                        else
                            s_surname = results.get(i).getValue();
                    }

                    else if (results.get(i).getType().contentEquals("2")) {
                        s_mail = results.get(i).getValue();
                    } else if (results.get(i).getType().contentEquals("3")) {
                        s_phone = results.get(i).getValue();
                    }

                }

                if (all_field) {

                   // pd.show();
                    new Connection().execute("");

                } else {
                    APP.show_status(m_activity, 2, getResources().getString(R.string.s_please_enter_x, required_field));
                }

            }
        });

        img_camera = (ImageView) findViewById(R.id.img_camera);
        img_camera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                ActionSheet.createBuilder(m_activity, getSupportFragmentManager())
                        .setCancelButtonTitle(getString(R.string.s_cancel))
                        .setOtherButtonTitles(getString(R.string.s_camera), getString(R.string.s_photo_gallery))
                        .setCancelableOnTouchOutside(true)
                        .setListener(new ActionSheet.ActionSheetListener() {
                            @Override
                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                            }

                            @Override
                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {

                                if (index == 0) {

                                    EZPhotoPickConfig config = new EZPhotoPickConfig();
                                    config.photoSource = PhotoSource.CAMERA;
                                    config.storageDir = APP.file_prefix;
                                    config.needToAddToGallery = true;
                                    config.exportingSize = 1000;
                                    EZPhotoPick.startPhotoPickActivity(m_activity, config);

                                } else if (index == 1) {

                                    EZPhotoPickConfig config = new EZPhotoPickConfig();
                                    config.photoSource = PhotoSource.GALLERY;
                                    config.needToExportThumbnail = true;
                                    config.isAllowMultipleSelect = false;
                                    config.storageDir = APP.file_prefix;
                                    config.exportingThumbSize = 200;
                                    config.exportingSize = 1000;
                                    EZPhotoPick.startPhotoPickActivity(m_activity, config);

                                }
                            }
                        }).show();
            }
        });

        img = (SimpleDraweeView) findViewById(R.id.img);
        img.setImageURI(APP.main_user.image);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyZoomImageDialog alert = new MyZoomImageDialog(m_activity, APP.main_user.name + " " + APP.main_user.surname, APP.main_user.image, null, true);
                alert.show();
            }
        });

        lay_main = (LinearLayout) findViewById(R.id.lay_main);

        results = new ArrayList<>();
        String[] profile_titles = getResources().getStringArray(R.array.profile_titles);

        results.add(new ProfileItem(APP.main_user.name, profile_titles[0], "1", "1"));
        results.add(new ProfileItem(APP.main_user.surname, profile_titles[1], "1", "1"));
        results.add(new ProfileItem(APP.main_user.email, profile_titles[2], "2", "1"));
        results.add(new ProfileItem(APP.main_user.mobile, profile_titles[3], "3", "1"));
        results.add(new ProfileItem(profile_titles[4], profile_titles[4], "6", "0"));

        adapter = new lazy(results);

        fillList();

        askCompactPermission(PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, new PermissionResult() {
            @Override
            public void permissionGranted() {

            }

            @Override
            public void permissionDenied() {

            }

            @Override
            public void permissionForeverDenied() {

            }
        });
    }

    private void fillList() {

        lay_main.removeAllViews();
        adapter.notifyDataSetChanged();
        final int adapterCount = adapter.getCount();

        for (int i = 0; i < adapterCount; i++) {
            View item = adapter.getView(i, null, null);
            lay_main.addView(item);
        }

    }

    public class lazy extends BaseAdapter {
        private ArrayList<ProfileItem> data;
        private LayoutInflater inflater = null;

        public lazy(ArrayList<ProfileItem> d) {
            this.data = d;
            inflater = LayoutInflater.from(m_activity);

        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public ProfileItem getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ProfileItem item = data.get(position);

            final ViewHolder holder;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.c_item_profile_setting, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.title.setText(item.getTitle());
            if (Integer.parseInt(item.getType()) > 3) {
                holder.img.setVisibility(View.VISIBLE);
                holder.tv_value.setVisibility(View.VISIBLE);
                holder.et_value.setVisibility(View.GONE);
                holder.tv_value.setText(item.getValue());
                holder.tv_value.setHint(item.getTitle());

            } else {
                holder.img.setVisibility(View.GONE);
                holder.tv_value.setVisibility(View.GONE);
                holder.et_value.setVisibility(View.VISIBLE);
                holder.et_value.setText(item.getValue());
                holder.et_value.setHint(item.getTitle());

                if (item.getType().contentEquals("1")) {

                    holder.et_value.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                } else if (item.getType().contentEquals("2")) {

                    holder.et_value.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                } else if (item.getType().contentEquals("3")) {

                    holder.et_value.setInputType(InputType.TYPE_CLASS_NUMBER);
                    holder.et_value.setKeyListener(DigitsKeyListener.getInstance("+0123456789"));

                }
            }

            holder.et_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus)
                        holder.title.setSelected(false);
                    else
                        holder.title.setSelected(true);
                }
            });

            holder.et_value.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    item.setValue(s.toString());
                    results.set(position, item);
                }
            });

            holder.tv_value.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus)
                        holder.title.setSelected(false);
                    else
                        holder.title.setSelected(true);
                }
            });

            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (item.getType().contentEquals("6")) {
                        startActivity(new Intent(m_activity, ChangePassword.class));
                    }
                }
            });

            return view;
        }

        private class ViewHolder {
            protected MyTextView title, tv_value;
            protected ImageView img;
            EditText et_value;

            public ViewHolder(View convertView) {
                title = (MyTextView) convertView.findViewById(R.id.title);
                tv_value = (MyTextView) convertView.findViewById(R.id.tv_value);
                tv_value.setFocusable(true);
                tv_value.setFocusableInTouchMode(true);
                img = (ImageView) convertView.findViewById(R.id.img);
                et_value = (EditText) convertView.findViewById(R.id.et_value);
                et_value.setFocusable(true);
                et_value.setFocusableInTouchMode(true);
            }
        }

    }

    private class Connection extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            List<Pair<String, String>> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.id)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(s_name)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode(s_surname)));
            nameValuePairs.add(new Pair<>("param4", APP.base64Encode(s_mail)));
            nameValuePairs.add(new Pair<>("param5", APP.base64Encode(s_phone)));
            nameValuePairs.add(new Pair<>("param7", APP.base64Encode("A")));
            nameValuePairs.add(new Pair<>("param8", APP.base64Encode(APP.language_id)));

            String xml = APP.post1(nameValuePairs, APP.path + "/account_panel/set_account_settings.php");

            if (xml != null && !xml.contentEquals("fail")) {
                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    List<String> dataList = new ArrayList<String>();

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        respPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        respPart2 = APP.base64Decode(APP.getElement(parse, "part2"));

                    }

                    if (respPart1.contentEquals("OK")) {

                        USER user = new USER(APP.main_user.id, s_name, s_surname, s_mail, APP.main_user.image, s_phone,APP.main_user.pass);

                        APP.main_user = user;
                        Gson gson = new Gson();
                        String json = gson.toJson(user);
                        APP.e.putString("USER", json);
                        APP.e.commit();
                        return "true";
                    } else {
                        return "error";
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
                finish();
            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, respPart2);
            } else {
                APP.show_status(m_activity, 1,
                        getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            Bitmap pickedPhoto = null;
            try {
                pickedPhoto = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
                final String photoName = data.getStringExtra(EZPhotoPick.PICKED_PHOTO_NAME_KEY);
                String photoPath = ezPhotoPickStorage.getAbsolutePathOfStoredPhoto(APP.file_prefix, photoName);

                Uri imageUri = Uri.fromFile(new File(photoPath));// For files on device

                final MyCropImageDialog alert = new MyCropImageDialog(m_activity, imageUri);
                alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (alert.output_uri != null) {
                            img.setImageURI(alert.output_uri);
                            new_image_uri = alert.output_uri;
                            new_image_name = photoName;
                            new_image_path = alert.output_path;

                            final MyAlertDialog alert = new MyAlertDialog(m_activity, R.mipmap.ic_placeholder_profile_black2,
                                    getString(R.string.s_profile_photo), getString(R.string.s_profile_photo_desc),
                                    getString(R.string.s_add), getString(R.string.s_cancel), true);

                            alert.setNegativeClicl(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                    img.setImageURI(APP.main_user.image);


                                }
                            });
                            alert.setPositiveClicl(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                   // pd.show();
                                    new Connection2().execute("");

                                }
                            });
                            alert.show();
                        }

                    }
                });
                alert.show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            MultipartRequest multipartRequest = new MultipartRequest(m_activity);
            multipartRequest.addString("param1", APP.base64Encode(APP.main_user.id));
            multipartRequest.addString("param2", APP.base64Encode(APP.language_id));
            multipartRequest.addString("param3", APP.base64Encode("A"));
            multipartRequest.addFile("file", new_image_path, new_image_name);

            String xml = multipartRequest.execute(APP.path + "/account_panel/profile_photos/set_profile_image.php");
            if (xml != null && !xml.contentEquals("fail")) {
                try {
                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        sendPart1 = APP.base64Decode(APP.getElement(parse, "part1"));
                        sendPart2 = APP.base64Decode(APP.getElement(parse, "part2"));
                        sendPart3 = APP.base64Decode(APP.getElement(parse, "part3"));

                    }

                    if (sendPart1.contentEquals("OK")) {

                        USER user = APP.main_user;
                        user.setImage(sendPart3);
                        APP.main_user = user;
                        Gson gson = new Gson();
                        String json = gson.toJson(user);
                        APP.e.putString("USER", json);
                        APP.e.commit();
                        return "true";
                    } else {
                        return "error";
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
                img.setImageURI(APP.main_user.image);
            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, sendPart2);
            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }
        }
    }

}
