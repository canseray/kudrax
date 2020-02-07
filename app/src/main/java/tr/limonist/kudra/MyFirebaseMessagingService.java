package tr.limonist.kudra;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Base64;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import tr.limonist.classes.USER;
import tr.limonist.kudra.app.StartMain;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public int mNotificationId;
    private SharedPreferences myPrefs;
    private SharedPreferences.Editor e;
    private USER main_user;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String message = data.get("message");
        showNotification(message, data);
    }

    public String base64Encode(String text) {
        byte[] data = null;
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public String base64Decode(String text) {
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

    private void showNotification(String message, Map<String, String> data) {
        JSONObject jsnobject=null;
        String mes_title="";
        String mes_body="";
        String mes_sound="";
        String mes_click="";
        try {
            jsnobject = new JSONObject(message);
            mes_title = jsnobject.getString("title");
            mes_body = jsnobject.getString("body");
            mes_sound = jsnobject.getString("sound");
            mes_click = jsnobject.getString("click_action");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] infos = mes_body.split("\\[#\\]");
        if (infos[0].contentEquals("chat")) {

            myPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            myPrefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
            e = myPrefs.edit();

            Gson gson = new Gson();
            String json = myPrefs.getString("USER", null);
            if (json != null)
                main_user = gson.fromJson(json, USER.class);
            else
                main_user = null;

            /*if (main_user != null) {
                if (Messaging.visible && Main.selected_chat_id.contentEquals(base64Decode(infos[1]))) {
                    Messaging.update();
                } else
                    new SendImageNotification().execute(message);
            }*/

        } else if (infos[0].contentEquals("friend")) {
            generateNotification(infos[1]);
        } else
            generateNotification(message);

    }

    public class SendImageNotification extends AsyncTask<String, Void, Bitmap> {

        String message;

        public SendImageNotification() {
            super();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            message = params[0];
            String[] infos = message.split("\\[#\\]");
            try {

                URL url = new URL(infos[1]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            chatNotification(result, message);
        }
    }

    private void chatNotification(Bitmap result, String message) {

        /*String[] infos = message.split("\\[#\\]");

        String CHANNEL_ID = "my_channel_01";
        CharSequence NAME = "my_channel";

        Intent intent = new Intent(getApplicationContext(), Messaging.class);

        intent.putExtra("image", base64Decode(infos[3]));
        intent.putExtra("title", base64Decode(infos[2]));
        intent.putExtra("receiver_id", base64Decode(infos[1]));
        intent.putExtra("user_id", main_user.id);

        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        Bitmap largeIcon = result;

        String channelName = CHANNEL_ID;
        CharSequence name = NAME;

        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.right);

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this, channelName)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(base64Decode(infos[2]))
                .setContentText(infos[4])
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(infos[4]));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelName, name, importance);
            channel.setDescription(description);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(soundUri, attributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(101, noBuilder.build());*/

    }

    private void generateNotification(String message) {

        String CHANNEL_ID = "my_channel_01";
        CharSequence NAME = "my_channel";

        Intent intent = new Intent(getApplicationContext(), StartMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("message", message);
        intent.putExtra("call_type","notification");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        int requestCode = 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String channelName = CHANNEL_ID;
        CharSequence name = NAME;
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.right);

        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this, channelName).setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(largeIcon).setContentTitle(getString(R.string.app_name)).setContentText(message)
                .setAutoCancel(true).setSound(soundUri).setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelName, name, importance);
            channel.setDescription(description);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(soundUri, attributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(101, noBuilder.build());

    }

}