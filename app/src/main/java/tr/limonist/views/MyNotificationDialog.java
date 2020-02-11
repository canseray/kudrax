package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.marlonlom.utilities.timeago.TimeAgoMessages;
import com.twotoasters.jazzylistview.JazzyListView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tr.limonist.extras.MyTextView;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.NotificationsItem;
import tr.limonist.extras.TransparentProgressDialog;

public class MyNotificationDialog extends Dialog {

	public String[] part1;
	private lazy adapter;
	ArrayList<NotificationsItem> results;
	SwipeRefreshLayout refresh;
	boolean editing = false;
	private Activity m_activity;
	private TransparentProgressDialog pd;
	JazzyListView list;
	private String respPart1,respPart2;

	public MyNotificationDialog(Activity activity) {
		super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		setCancelable(true);
		setCanceledOnTouchOutside(true);
		m_activity = activity;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.a_my_notification_dialog);


		pd = new TransparentProgressDialog(m_activity, "", true);

		MyTextView tv_edit = (MyTextView) findViewById(R.id.tv_edit);
		tv_edit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				editing = !editing;
				adapter.notifyDataSetChanged();
			}
		});

		list = (JazzyListView) findViewById(R.id.list);

		TextView empty = (TextView) findViewById(android.R.id.empty);
		list.setEmptyView(empty);

		refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
		refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {

				refresh.setRefreshing(true);
				new Connection().execute("");
			}
		});

		show();
		pd.show();
		new Connection().execute("");

	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	class Connection extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... args) {

			List<Pair<String, String>> nameValuePairs = new ArrayList<>();

			results = new ArrayList<>();

			nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.id)));
			nameValuePairs.add(new Pair<>("param2", APP.base64Encode("A")));
			nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.android_id)));

			String xml = APP.post1(nameValuePairs, APP.path + "/notifications/get_notifications.php");

			if (xml != null && !xml.contentEquals("fail")) {

				try {

					DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
					List<String> dataList = new ArrayList<String>();

					for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
						part1 = APP.base64Decode(APP.getElement(parse,"part1")).split("\\[##\\]");
					}

					if (!part1[0].contentEquals("")) {

						for (int i = 0; i < part1.length; i++) {
							String[] temp = part1[i].split("\\[#\\]");
							NotificationsItem ai = new NotificationsItem(temp.length > 0 ? temp[0] : "",
									temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
									temp.length > 3 ? temp[3] : "", temp.length > 4 ? temp[4] : "",
									temp.length > 5 ? temp[5] : "", temp.length > 6 ? temp[6] : "");
							results.add(ai);
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
			if (refresh != null)
				refresh.setRefreshing(false);
			if (result.contentEquals("true")) {

				editing = false;

				adapter = new lazy();
				list.setAdapter(adapter);
				adapter.notifyDataSetChanged();

			} else {
				APP.show_status(m_activity, 1,
						m_activity.getResources().getString(R.string.s_unexpected_connection_error_has_occured));
			}
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
		public NotificationsItem getItem(int position) {
			return results.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public class ViewHolder {

			TextView title, text, date;
			ImageView img_remove;
			SimpleDraweeView img;

		}

		public View getView(final int position, View view, ViewGroup parent) {
			final NotificationsItem item = results.get(position);

			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.c_item_notificaiton, null);

				holder.title = (TextView) view.findViewById(R.id.title);
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.date = (TextView) view.findViewById(R.id.date);

				holder.img_remove = (ImageView) view.findViewById(R.id.img_remove);
				holder.img = (SimpleDraweeView) view.findViewById(R.id.img);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.title.setText(item.getTitle());
			holder.text.setText(item.getText());
			String text="";
			Locale LocaleBylanguageTag = Locale.forLanguageTag("tr");
			TimeAgoMessages messages = new TimeAgoMessages.Builder().withLocale(LocaleBylanguageTag).build();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date mDate = sdf.parse(item.getDate());
				long timeInMilliseconds = mDate.getTime();
				text = TimeAgo.using(timeInMilliseconds, messages);
				holder.date.setText(text);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (editing)
				holder.img_remove.setVisibility(View.VISIBLE);
			else
				holder.img_remove.setVisibility(View.GONE);

			holder.img_remove.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					selected_item_id = item.getId();
					pd.show();
					new Connection2().execute("");

				}
			});

			holder.img.setImageURI(item.getImage());

			return view;
		}

	}

	protected String selected_item_id = "";

	class Connection2 extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... args) {

			List<Pair<String, String>> nameValuePairs = new ArrayList<>();

			nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user.id)));
			nameValuePairs.add(new Pair<>("param2", APP.base64Encode(selected_item_id)));
			nameValuePairs.add(new Pair<>("param3", APP.base64Encode(APP.android_id)));
			nameValuePairs.add(new Pair<>("param4", APP.base64Encode("A")));

			String xml = APP.post1(nameValuePairs, APP.path + "/notifications/send_remove_notification_request.php");

			if (xml != null && !xml.contentEquals("fail")) {

				try {

					DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
					List<String> dataList = new ArrayList<String>();

					for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {
						respPart1 = APP.base64Decode(APP.getElement(parse,"part1"));
						respPart2 = APP.base64Decode(APP.getElement(parse,"part2"));
					}

					if (respPart1.contentEquals("OK")) {
						return "true";
					} else if (respPart1.contentEquals("FAIL")) {
						return "error";
					} else {
						return "hata";
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
			if (result.contentEquals("true")) {
				new Connection2().execute("");
			} else if (result.contentEquals("error")) {
				if (pd != null)
					pd.dismiss();
				APP.show_status(m_activity, 2, respPart2);
			} else {
				if (pd != null)
					pd.dismiss();
				APP.show_status(m_activity, 1,
						m_activity.getResources().getString(R.string.s_unexpected_connection_error_has_occured));
			}
		}
	}

}
