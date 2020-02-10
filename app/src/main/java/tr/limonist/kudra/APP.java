package tr.limonist.kudra;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tr.limonist.classes.OrderParams;
import tr.limonist.classes.USER;
import tr.limonist.views.MyStatusDialog;

public class APP extends Application {
    public static final String SHARED_PREF = "BARLEY";
    public static String file_prefix = "BARLEY";

    public static String language_id = "1";
    public static String package_name = "tr.limonist.barley";
    public static String path = "https://www.kudraciltbesinleri.com/mobil/";
    public static SharedPreferences myPrefs;
    public static SharedPreferences.Editor e;
    public static USER main_user;
    public static String android_id = "NOT_PERMITED";
    public static String device_id = "";
    public static LocationManager mLocationManager;
    public static double lat=0;
    public static double lon=0;
    public static String version;
    public static OrderParams send_order_params;

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        myPrefs = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        e = myPrefs.edit();

        Gson gson = new Gson();
        String json = myPrefs.getString("USER", null);
        if (json != null)
            main_user = gson.fromJson(json, USER.class);
        else
            main_user = null;

        if (Locale.getDefault().getLanguage().equals(new Locale("en").getLanguage())) {
            language_id = "2";
        } else {
            language_id = "1";
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull final Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {

                } else {
                    e.putString("regId", task.getResult().getToken());
                    e.commit();
                }

            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (Locale.getDefault().getLanguage().equals(new Locale("en").getLanguage())) {
            language_id = "2";
        } else {
            language_id = "1";
        }
    }

    public static String post1(List<Pair<String, String>> nameValuePairs, String services_part) {

        try {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (Pair<String, String> pair : nameValuePairs) {
                formBodyBuilder.add(pair.first, pair.second);
            }
            FormBody formBody = formBodyBuilder.build();

            Request.Builder builder = new Request.Builder();
            builder = builder.url(services_part);
            builder = builder.post(formBody);
            Request request = builder.build();

            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = null;
            try {
                response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return "fail";
            }
        } catch (Exception ex) {
            return "fail";
        }
    }

    public static String formatFigureTwoPlaces(Double value) {
        DecimalFormat myFormatter = new DecimalFormat("##0.00");

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        myFormatter.setMaximumFractionDigits(2);
        myFormatter.setMinimumFractionDigits(2);
        myFormatter.setDecimalFormatSymbols(dfs);

        return myFormatter.format(value);
    }

    public static String formatFigureOnePlaces(Double value) {
        DecimalFormat myFormatter = new DecimalFormat("##0.0");

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        myFormatter.setMaximumFractionDigits(1);
        myFormatter.setMinimumFractionDigits(1);
        myFormatter.setDecimalFormatSymbols(dfs);

        return myFormatter.format(value);
    }

    public static String base64Encode(String text) {
        byte[] data = null;
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static String base64Decode(String text) {
        byte[] data1 = Base64.decode(text, Base64.DEFAULT);
        String text1 = null;
        try {
            text1 = new String(data1, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            text1 = "fail";
        }

        return text1;

    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String getDeviceId(Context context) {

        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        String expression = "^0\\(5(\\d{2})\\)[ ](\\d{3})[- ](\\d{4})$";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

	public static void show_status(Activity activity, int type, String message) {
		MyStatusDialog alert = new MyStatusDialog(activity, activity, type, message);
		alert.show();
	}
	
	public static void show_status_with_dismiss(Activity activity, int type, String message,
			DialogInterface.OnDismissListener dismiss) {
		MyStatusDialog alert = new MyStatusDialog(activity, activity, type, message);
		alert.setCustomDismiss(dismiss);
		alert.show();
	}

	public static String getElement(Document parse, String element)
    {
        return parse.getElementsByTagName(element).getLength()>0?parse.getElementsByTagName(element).item(0).getTextContent():"";
    }
    public static void setWindowsProperties(Activity a,boolean lightStatusBar,boolean... keyboard)
    {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(a, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(a, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            a.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if(lightStatusBar)
                a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (keyboard.length > 0) {

        }
        else
            a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE|WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    static void setWindowFlag(Activity a, final int bits, boolean on) {
        Window win = a.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}