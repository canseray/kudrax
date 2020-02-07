package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.JazzyListView;

import java.util.ArrayList;

import tr.limonist.kudra.APP;
import tr.limonist.kudra.R;
import tr.limonist.classes.SlideMenuItem;
import tr.limonist.extras.MyTextView;

public class MySlideMenuDialog extends Dialog {
	boolean show_status = false;
	Activity m_activity;
	LinearLayout lay_status, lay_main;
	ArrayList<SlideMenuItem> results;
	private JazzyListView list;
	private lazy adapter;
	private MyTextView name,mail,point,status;
	private SimpleDraweeView img,img_status;

	public MySlideMenuDialog(Activity activity) {
		super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		m_activity = activity;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.a_my_slide_menu_dialog);

		lay_status = (LinearLayout) findViewById(R.id.lay_status);
		lay_status.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lay_status.isEnabled())
					close();
			}
		});

		lay_main = (LinearLayout) findViewById(R.id.lay_main);
		lay_main.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (lay_status.isEnabled())
					close();
			}
		});

		list = (JazzyListView) findViewById(R.id.list);
		list.setTransitionEffect(JazzyHelper.TILT);

		setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (lay_status.isEnabled())
						close();
				}
				return true;
			}
		});

		LayoutInflater layoutInflater = LayoutInflater.from(m_activity);
		View head = layoutInflater.inflate(R.layout.c_item_head_slide_menu_list, null);

		name = (MyTextView) head.findViewById(R.id.name);
		point = (MyTextView) head.findViewById(R.id.point);
		status = (MyTextView) head.findViewById(R.id.status);
		mail = (MyTextView) head.findViewById(R.id.mail);

		img_status = (SimpleDraweeView) head.findViewById(R.id.img_status);
		img = (SimpleDraweeView) head.findViewById(R.id.img);
		img.setImageURI(APP.main_user.image);
		mail.setText(APP.main_user.email);
		name.setText(APP.main_user.name + " " + APP.main_user.surname);

		head.setEnabled(false);
		head.setClickable(false);

		list.addHeaderView(head);

	}

	public void setItemClick(OnItemClickListener item_click) {
		this.list.setOnItemClickListener(item_click);

	}

	public class lazy extends BaseAdapter {
		private ArrayList<SlideMenuItem> data;
		private LayoutInflater inflater = null;

		public lazy(ArrayList<SlideMenuItem> d) {
			this.data = d;
			inflater = LayoutInflater.from(m_activity);

		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public SlideMenuItem getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View view, ViewGroup parent) {
			final SlideMenuItem item = data.get(position);

			ViewHolder holder;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) m_activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.c_item_slide_menu_list, parent, false);
				holder = new ViewHolder(view);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.title.setText(item.getTitle());
			holder.badge.setText(item.getBadge());
			if (Integer.parseInt(item.getBadge()) > 0)
				holder.badge.setVisibility(View.VISIBLE);
			else
				holder.badge.setVisibility(View.GONE);

			holder.img.setImageURI(item.getImage());

			return view;
		}

		private class ViewHolder {
			protected MyTextView title, badge;
			protected SimpleDraweeView img;

			public ViewHolder(View convertView) {
				title = (MyTextView) convertView.findViewById(R.id.title);
				badge = (MyTextView) convertView.findViewById(R.id.badge);
				img = (SimpleDraweeView) convertView.findViewById(R.id.img);
			}
		}

	}

	public void setData(ArrayList<SlideMenuItem> data) {
		results = data;
		adapter = new lazy(results);
		list.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	public void setStatus(String status_image,String mPoint,String mStatus) {
		img_status.setImageURI(status_image);
		point.setText(mPoint);
		status.setText(mStatus);
	}

	@Override
	public void show() {
		super.show();
		showSetting();
	}

	@Override
	public void dismiss() {
		super.dismiss();

	}

	public void close() {

		hideSetting();
	}

	protected void hideSetting() {

		if (show_status) {
			Animation logoMoveAnimation = AnimationUtils.loadAnimation(m_activity, R.anim.a_grow_from_right);
			logoMoveAnimation.setAnimationListener(new TranslateAnimation.AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					lay_status.setEnabled(false);
					lay_status.setClickable(false);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					show_status = false;
					lay_status.setVisibility(View.GONE);
					lay_status.setEnabled(true);
					lay_status.setClickable(true);
					dismiss();

				}
			});
			lay_status.setVisibility(View.VISIBLE);
			lay_status.startAnimation(logoMoveAnimation);
		}
	}

	protected void showSetting() {

		Animation logoMoveAnimation = AnimationUtils.loadAnimation(m_activity, R.anim.a_grow_from_left);
		logoMoveAnimation.setAnimationListener(new TranslateAnimation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				lay_status.setVisibility(View.VISIBLE);
				show_status = true;
				lay_status.setClickable(false);
				lay_status.setEnabled(false);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				lay_status.setEnabled(true);
				lay_status.setClickable(true);
			}
		});
		lay_status.setVisibility(View.INVISIBLE);
		lay_status.startAnimation(logoMoveAnimation);

	}

}
