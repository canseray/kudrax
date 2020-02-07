package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TouchImageView;
import tr.limonist.extras.TouchImageView.OnTouchImageViewListener;
import tr.limonist.kudra.R;

public class MyZoomImageDialog extends Dialog {
    Activity m_activity;
    TouchImageView img;
    MyTextView title;
    LinearLayout lay_close;

    public MyZoomImageDialog(Activity activity,String message,String url,Bitmap bitmap,boolean is_url) {
        super(activity, android.R.style.Theme_Black_NoTitleBar);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        m_activity = activity;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.a_my_zoom_image_dialog);

        lay_close = (LinearLayout) findViewById(R.id.lay_close);
        lay_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }

        });

        title = (MyTextView) findViewById(R.id.title);
        title.setText(message);

        img = (TouchImageView)findViewById(R.id.img);

        if(is_url)
           new Connection().execute(url);
        else
            img.setImageBitmap(bitmap);
        img.setOnTouchImageViewListener(new OnTouchImageViewListener() {

            @Override
            public void onMove() {
                PointF point = img.getScrollPosition();
                RectF rect = img.getZoomedRect();
                float currentZoom = img.getCurrentZoom();
                boolean isZoomed = img.isZoomed();

            }
        });

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    class Connection extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... args) {

            Bitmap bitmap =null;

            bitmap = getBitmapFromURL(args[0]);

            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
           if(result!=null)
               img.setImageBitmap(result);
        }
    }
}
