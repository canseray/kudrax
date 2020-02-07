package tr.limonist.kudra.app.shop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.twotoasters.jazzylistview.JazzyListView;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.piruin.quickaction.ActionItem;
import me.piruin.quickaction.QuickAction;
import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.FavoriteItem;
import tr.limonist.extras.MyTextView;
import tr.limonist.extras.TransparentProgressDialog;

public class Search extends Activity {

	private Activity m_activity;
	private TransparentProgressDialog pd;
	private EditText et_search;
	private MyTextView cancel;
	private LinearLayout lay_search;
	boolean search = false;
	private JazzyListView list;
	private QuickAction quickIntent;

	ArrayList<FavoriteItem> results;
	private String filter_type;
	public lazy adapter;
	String[] part1;
	private static final int ID_LOC = 1;
	private static final int ID_RATE = 2;
	String s_search ="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_activity = Search.this;
		APP.setWindowsProperties(m_activity, true);
		pd = new TransparentProgressDialog(m_activity, "", true);
		setContentView(R.layout.z_layout_listview_search);

		ViewStub stub = findViewById(R.id.lay_stub);
		stub.setLayoutResource(R.layout.b_top_img_txt_img);
		stub.inflate();

		MyTextView tv_baslik = (MyTextView) findViewById(R.id.tv_baslik);
		tv_baslik.setText(getString(R.string.s_search));

		ImageView img_left = (ImageView) findViewById(R.id.img_left);
		img_left.setImageResource(R.drawable.b_ic_prew_black);

		LinearLayout top_left = (LinearLayout) findViewById(R.id.top_left);
		top_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}

		});

		ImageView img_right = (ImageView) findViewById(R.id.img_right);
		img_right.setImageResource(R.drawable.b_ic_filter_black);

		LinearLayout top_right = (LinearLayout) findViewById(R.id.top_right);
		top_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				quickIntent.show(arg0);

			}

		});

		list = (JazzyListView) findViewById(R.id.list);

		et_search = (EditText) findViewById(R.id.et_search);
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				s_search =arg0.toString();
				new Connection().execute();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		cancel = (MyTextView) findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				closeSearch();

			}

		});

		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!search)
					openSearch();

			}

		};

		lay_search = (LinearLayout) findViewById(R.id.lay_search);
		lay_search.setOnClickListener(click);

		QuickAction.setDefaultColor(ResourcesCompat.getColor(getResources(), R.color.a_white11, null));
		QuickAction.setDefaultTextColor(Color.BLACK);

		ActionItem locItem = new ActionItem(ID_LOC, getString(R.string.s_filter_by_price_asc),
				R.drawable.b_ic_sort_asc);
		ActionItem rateItem = new ActionItem(ID_RATE, getString(R.string.s_filter_by_price_desc),
				R.drawable.b_ic_sort_decs);
		locItem.setSticky(false);
		rateItem.setSticky(false);
		quickIntent = new QuickAction(m_activity, QuickAction.VERTICAL);
		quickIntent.setDividerColor(ContextCompat.getColor(m_activity, R.color.a_black13));
		quickIntent.setEnabledDivider(true);
		quickIntent.addActionItem(locItem);
		quickIntent.addActionItem(rateItem);

		quickIntent.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			@Override
			public void onItemClick(ActionItem item) {
				if (item.getActionId() == ID_LOC) {
					filter_type = "1";
				} else if (item.getActionId() == ID_RATE) {
					filter_type = "2";
				}
				s_search = et_search.getText().toString();
				pd.show();
				new Connection().execute("");

			}
		});
		quickIntent.setAnimStyle(QuickAction.Animation.REFLECT);

		filter_type = "2";

		results = new ArrayList<>();

		adapter= new lazy();
		list.setAdapter(adapter);

	}

	private void openSearch() {
		search = true;
		cancel.setVisibility(View.VISIBLE);
		et_search.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(et_search, InputMethodManager.SHOW_IMPLICIT);
		lay_search.setVisibility(View.GONE);

	}

	private void closeSearch() {
		search = false;
		cancel.setVisibility(View.GONE);
		hideKeyboard(et_search);
		lay_search.setVisibility(View.VISIBLE);
	}

	private void hideKeyboard(EditText et) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
	}

	private class Connection extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... args) {

			results = new ArrayList<>();

			List<Pair<String, String>> nameValuePairs = new ArrayList<>();

			nameValuePairs.add(new Pair<>("param1", APP.base64Encode(APP.main_user != null ? APP.main_user.id : "0")));
			nameValuePairs.add(new Pair<>("param2", APP.base64Encode(filter_type)));
			nameValuePairs.add(new Pair<>("param3", APP.base64Encode(s_search)));
			nameValuePairs.add(new Pair<>("param4", APP.base64Encode(APP.language_id)));
			nameValuePairs.add(new Pair<>("param5", APP.base64Encode("A")));

			String xml = APP.post1(nameValuePairs, APP.path + "/get_searching_result.php");

			if (xml != null && !xml.contentEquals("fail")) {

				try {

					DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

					for (int i = 0; i < parse.getElementsByTagName("row").getLength(); i++) {

						part1 = APP.base64Decode(APP.getElement(parse, "part1")).split("\\[##\\]");
					}

					if (!part1[0].contentEquals("")) {

						for (int i = 0; i < part1.length; i++) {
							String[] temp = part1[i].split("\\[#\\]");
							FavoriteItem ai = new FavoriteItem(temp.length > 0 ? temp[0] : "",
									temp.length > 1 ? temp[1] : "", temp.length > 2 ? temp[2] : "",
									temp.length > 3 ? temp[3] : "",temp.length > 4 ? temp[4] : "");
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
			if (result.contentEquals("true")) {
				adapter.notifyDataSetChanged();
			} else {
				APP.show_status(m_activity, 1,getResources().getString(R.string.s_unexpected_connection_error_has_occured));
			}
		}
	}

	public class lazy extends BaseAdapter {
		private LayoutInflater mInflater;

		public lazy() {
			mInflater = (LayoutInflater) m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return results.size();
		}

		@Override
		public FavoriteItem getItem(int position) {
			return results.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public class ViewHolder {
			LinearLayout lay_price;
			SimpleDraweeView img;
			MyTextView title,price,old_price,new_price;

		}

		public View getView(final int position, View view, ViewGroup parent) {

			final FavoriteItem item = results.get(position);

			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = mInflater.inflate(R.layout.c_item_search_list, null);

				holder.title = (MyTextView) view.findViewById(R.id.title);
				holder.price = (MyTextView) view.findViewById(R.id.price);
				holder.old_price = (MyTextView) view.findViewById(R.id.old_price);
				holder.new_price = (MyTextView) view.findViewById(R.id.new_price);

				holder.img = (SimpleDraweeView) view.findViewById(R.id.img);

				holder.lay_price = (LinearLayout) view.findViewById(R.id.lay_price);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.img.setImageURI(item.getImage());

			holder.title.setText(item.getName());

			holder.price.setText(item.getPrice());

			holder.new_price.setText(item.getPrice());

			holder.old_price.setText(item.getOld());
			holder.old_price.setPaintFlags(holder.old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

			if(!item.getOld().contentEquals(""))
			{
				holder.price.setVisibility(View.GONE);
				holder.lay_price.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.price.setVisibility(View.VISIBLE);
				holder.lay_price.setVisibility(View.GONE);
			}

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					startActivity(new Intent(m_activity,ProductDetail.class)
							.putExtra("product_id",item.getProduct())
							.putExtra("product_title",item.getName())
					);

				}
			});

			return view;
		}

	}

}
