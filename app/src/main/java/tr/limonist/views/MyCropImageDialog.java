package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import tr.limonist.extras.TransparentProgressDialog;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;

public class MyCropImageDialog extends Dialog {
    private final CropImageView mCropView;
    Activity m_activity;
    Uri source_uri;

    public Uri output_uri = null;

    private TransparentProgressDialog pd;

    public MyCropImageDialog(Activity activity, Uri url) {
        super(activity, android.R.style.Theme_Black_NoTitleBar);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        source_uri = url;
        m_activity = activity;
        pd = new TransparentProgressDialog(m_activity, "", true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.a_dialog_crop_image);

        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        mCropView.setCompressFormat(Bitmap.CompressFormat.JPEG);
        mCropView.setCompressQuality(90);
        mCropView.setOutputMaxSize(300, 300);
        mCropView.setInitialFrameScale(0.9f);
        mCropView.setFrameColor(m_activity.getResources().getColor(R.color.a_main11));
        mCropView.setHandleColor(m_activity.getResources().getColor(R.color.a_main11));
        mCropView.setGuideColor(m_activity.getResources().getColor(R.color.a_main11));
        mCropView.setCropMode(CropImageView.CropMode.SQUARE);
        findViewById(R.id.buttonDone).setOnClickListener(btnListener);
        findViewById(R.id.buttonClose).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);

        mCropView.load(source_uri).execute(mLoadCallback);

    }

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(m_activity, e.toString(), Toast.LENGTH_SHORT).show();
            dismiss();
        }
    };

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonDone:

                    pd.show();
                    mCropView.crop(source_uri)
                            .execute(new CropCallback() {
                                @Override
                                public void onSuccess(Bitmap cropped) {

                                    mCropView.save(cropped)
                                            .execute(createNewUri(m_activity,mCompressFormat), mSaveCallback);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }
                            });

                    break;
                case R.id.buttonRotateLeft:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    break;
                case R.id.buttonRotateRight:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    break;
                case R.id.buttonClose:
                    dismiss();
                    break;
            }
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {

        @Override
        public void onSuccess(Uri outputUri) {
            if (pd != null)
                pd.dismiss();
            output_uri = outputUri;
            dismiss();
        }

        @Override
        public void onError(Throwable e) {
            if (pd != null)
                pd.dismiss();
        }
    };

    public String output_path="";

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "scv" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        output_path = path;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Logger.i("SaveUri = " + uri);
        return uri;
    }
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    public String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/" + APP.file_prefix);
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }
    public String getMimeType(Bitmap.CompressFormat format) {
        Logger.i("getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }
}
