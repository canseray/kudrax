package tr.limonist.kudra.app.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;
import tr.limonist.classes.RequesReviewItem;
import tr.limonist.extras.ActionEditText;
import tr.limonist.extras.MultipartRequest;
import tr.limonist.views.MyAlertDialog;
import tr.limonist.extras.MyTextView;
import tr.limonist.views.MyZoomImageDialog;
import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class Feedback extends ActivityManagePermission {

    ArrayList<RequesReviewItem> results;
    MyTextView tv_done;
    private TransparentProgressDialog pd;
    private LinearLayout lay_form;
    public String rating, message;
    public String[] formPart1;
    private ActionEditText et_message;
    ImageView img_photo;
    Activity m_activity;
    public boolean is_have_image = false;
    private Bitmap bitmap;
    String title;
    EZPhotoPickStorage ezPhotoPickStorage;
    Uri new_image_uri;
    String new_image_name="",new_image_path="", formPart2;
    private String sendPart1,sendPart2;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_activity = Feedback.this;
        APP.setWindowsProperties(m_activity, false);
        is_have_image = false;
        title = getIntent().getStringExtra("title");
        pd = new TransparentProgressDialog(m_activity, "", true);
        setContentView(R.layout.z_feedback);

        ViewStub stub = (ViewStub) findViewById(R.id.lay_stub);
        stub.setLayoutResource(R.layout.b_top_img_txt_img);
        stub.inflate();

        ezPhotoPickStorage = new EZPhotoPickStorage(m_activity);

        MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
        tv_baslik.setTextColor(getResources().getColor(R.color.a_brown11));
        tv_baslik.setText(title);
        rating = "";
        message = "";

        ImageView img_left = (ImageView) findViewById(R.id.img_left);
        img_left.setImageResource(R.drawable.left_k);
        img_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {



                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();


            }

        });

        tv_done = (MyTextView) findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                message = et_message.getText().toString();
                rating = "";
                for (int i = 0; i < results.size(); i++) {
                    float rate = ((MaterialRatingBar) lay_form.getChildAt(i).findViewById(R.id.rating_bar)).getRating();
                    if (i == 0)
                        rating = results.get(i).getId()+"[-]"+String.valueOf((int)rate);
                    else
                        rating += "[--]"+ results.get(i).getId()+"[-]"+String.valueOf((int)rate);
                }

                pd.show();
                new Connection2().execute();
            }

        });

        img_photo = (ImageView) findViewById(R.id.img_photo);
        img_photo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (bitmap == null) {
                    EZPhotoPickConfig config = new EZPhotoPickConfig();
                    config.photoSource = PhotoSource.CAMERA;
                    config.storageDir = APP.file_prefix;
                    config.needToAddToGallery = true;
                    config.exportingSize = 1000;
                    EZPhotoPick.startPhotoPickActivity(m_activity, config);
                } else {
                    final MyAlertDialog alert = new MyAlertDialog(m_activity, R.drawable.b_ic_add_photo_main,
                            getString(R.string.s_photo), getString(R.string.s_photo_desc),
                            getString(R.string.s_photo_change), getString(R.string.s_photo_view), true);

                    alert.setNegativeClicl(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            alert.dismiss();

                            MyZoomImageDialog dialog = new MyZoomImageDialog(m_activity, title, "", bitmap, false);
                            dialog.show();

                        }
                    });
                    alert.setPositiveClicl(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            alert.dismiss();
                            EZPhotoPickConfig config = new EZPhotoPickConfig();
                            config.photoSource = PhotoSource.CAMERA;
                            config.storageDir = APP.file_prefix;
                            config.needToAddToGallery = true;
                            config.exportingSize = 1000;
                            EZPhotoPick.startPhotoPickActivity(m_activity, config);
                        }
                    });
                    alert.show();
                }
            }
        });
        lay_form = (LinearLayout)findViewById(R.id.lay_form);
        lay_form.removeAllViews();

        et_message = (ActionEditText) findViewById(R.id.et_message);

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

        pd.show();
        new Connection().execute("");

    }

    private void componenets() {

        for (int i = 0; i < results.size(); i++) {

            LayoutInflater inflater = LayoutInflater.from(m_activity);
            View vi = inflater.inflate(R.layout.c_item_feedback, null, false);

            final TextView title = (TextView) vi.findViewById(R.id.title);
            title.setText(results.get(i).getTitle());

            final SimpleDraweeView img = (SimpleDraweeView) vi.findViewById(R.id.img);
            final TextView txt = (TextView) vi.findViewById(R.id.txt);

            final MaterialRatingBar rate = (MaterialRatingBar) vi.findViewById(R.id.rating_bar);
            rate.setNumStars(5);
            rate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {


                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                    if (rating == 1){
                        img.setImageResource(R.drawable.emoji_k);
                        txt.setText("Çok Kötü");

                    } else if(rating == 2) {
                        img.setImageResource(R.drawable.logo);
                        txt.setText("Kötü");

                    } else if(rating == 3) {
                        img.setImageResource(R.drawable.logo);
                        txt.setText("İyi");

                    } else if(rating == 4) {
                        img.setImageResource(R.drawable.logo);
                        txt.setText("Çok İyi");

                    } else {
                        img.setImageResource(R.drawable.logo);
                        txt.setText("Harika");

                    }

                    if (rating < 1.0f)
                        ratingBar.setRating(1.0f);
                    else {
                        int pos = (int) rating - 1;
                    }
                }
            });

            rate.setRating(1.0f);
            lay_form.addView(vi);
        }

    }

    private class Connection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            List nameValuePairs = new ArrayList<>();

            results = new ArrayList<>();

            nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.id)));
            nameValuePairs.add(new Pair<>("param2", APP.base64Encode(APP.language_id)));
            nameValuePairs.add(new Pair<>("param3", APP.base64Encode("A")));

            String xml = APP.post1(nameValuePairs, APP.path + "/get_feedback_data_list.php");

            if (xml != null && !xml.contentEquals("fail")) {

                try {

                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    List<String> dataList = new ArrayList<String>();

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        formPart1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
                        formPart2 = APP.base64Decode(APP.getElement(parse, "part2"));

                    }
                    if (!formPart1[0].contentEquals("")) {

                        for (int i = 0; i < formPart1.length; i++) {
                            String[] temp = formPart1[i].split("\\[#\\]");
                            RequesReviewItem ai = new RequesReviewItem(
                                    temp.length > 0 ? temp[0] : "", temp.length > 1 ? temp[1] : "");
                            results.add(ai);
                        }
                        return "true";
                    }
                    else
                        return "empty";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }

            } else {
                return "false";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (pd != null)
                pd.dismiss();
            if (result.contentEquals("true")) {
                componenets();
            } else if (result.contentEquals("empty")) {

                APP.show_status_with_dismiss(m_activity, 2, getString(R.string.s_no_active_form), new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });

            } else {
                APP.show_status(m_activity, 1, getResources().getString(R.string.s_unexpected_connection_error_has_occured));
            }

        }

    }

    private class Connection2 extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... args) {

            MultipartRequest multipartRequest = new MultipartRequest(m_activity);

            multipartRequest.addString("param1",APP.base64Encode(APP.main_user.id));
            multipartRequest.addString("param2",APP.base64Encode(rating));
            multipartRequest.addString("param3",APP.base64Encode(message));
            multipartRequest.addString("param4",APP.base64Encode(APP.language_id));
            multipartRequest.addString("param5",APP.base64Encode("A"));

            if(is_have_image)
                multipartRequest.addFile("file",new_image_path,new_image_name);

            String xml = multipartRequest.execute(APP.path + "/send_feedback_request.php");
            if (xml != null && !xml.contentEquals("fail")) {
                try {
                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
                    List<String> dataList = new ArrayList<String>();

                    for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

                        sendPart1 = APP.base64Decode(APP.getElement(parse,"part1"));
                        sendPart2 = APP.base64Decode(APP.getElement(parse,"part2"));
                    }

                    if (sendPart1.contentEquals("OK")) {
                        return "true";
                    }else if (sendPart1.contentEquals("FAIL")) {
                        return "error";
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
                APP.show_status(m_activity, 0, sendPart2);
            } else if (result.contentEquals("error")) {
                APP.show_status(m_activity, 2, sendPart2);
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
            is_have_image = false;
            return;
        }

        if (requestCode == EZPhotoPick.PHOTO_PICK_GALLERY_REQUEST_CODE || requestCode == EZPhotoPick.PHOTO_PICK_CAMERA_REQUEST_CODE) {
            try {
                bitmap = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
                is_have_image = true;

                new_image_name = data.getStringExtra(EZPhotoPick.PICKED_PHOTO_NAME_KEY);
                new_image_path = ezPhotoPickStorage.getAbsolutePathOfStoredPhoto(APP.file_prefix, new_image_name);
                new_image_uri= Uri.fromFile(new File(new_image_path));// For files on device
                img_photo.setImageURI(new_image_uri);

            } catch (IOException e) {
                e.printStackTrace();
                is_have_image = false;
            }
        }
    }
}
