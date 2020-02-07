package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import tr.limonist.kudra.R;

public class MyStatusDialog extends Dialog {
	boolean show_status = false;
	TextView tv_status_baslik, tv_status_message;
	ImageView img_status;
	LinearLayout lay_status;
	Context m_context;
	Activity m_activity;

	public MyStatusDialog(Context context, Activity activity, final int type, String message) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		m_context = context;
		m_activity = activity;
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setContentView(R.layout.a_my_status_dialog);

		tv_status_baslik = (TextView) findViewById(R.id.tv_status_baslik);
		tv_status_message = (TextView) findViewById(R.id.tv_status_message);

		img_status = (ImageView) findViewById(R.id.img_status);
		lay_status = (LinearLayout) findViewById(R.id.lay_status);

		this.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (type == 0)
					m_activity.finish();
			}
		});

		lay_status.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hideSetting();
			}
		});

		if (type == 0) {
			img_status.setImageResource(R.drawable.status_success);
			lay_status.setBackgroundColor(m_context.getResources().getColor(R.color.a_success11));
			tv_status_baslik.setText(m_context.getString(R.string.s_successful));
		} else if (type == 1) {
			img_status.setImageResource(R.drawable.status_error);
			lay_status.setBackgroundColor(m_context.getResources().getColor(R.color.a_error11));
			tv_status_baslik.setText(m_context.getString(R.string.s_error));
		} else if (type == 2) {
			img_status.setImageResource(R.drawable.status_warning);
			lay_status.setBackgroundColor(m_context.getResources().getColor(R.color.a_warning11));
			tv_status_baslik.setText(m_context.getString(R.string.s_warning));
		} else {
			img_status.setImageResource(R.drawable.status_success);
			lay_status.setBackgroundColor(m_context.getResources().getColor(R.color.a_success11));
			tv_status_baslik.setText(m_context.getString(R.string.s_successful));
		}

		tv_status_message.setText(message);
		showSetting();

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				hideSetting();
			}
		}, 3 * 1000);

	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	protected void hideSetting() {

		if (show_status) {
			Animation logoMoveAnimation = AnimationUtils.loadAnimation(m_context, R.anim.a_grow_from_top);
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

		Animation logoMoveAnimation = AnimationUtils.loadAnimation(m_context, R.anim.a_grow_from_bottom);
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
	
	public void setCustomDismiss(OnDismissListener dismiss) {
		this.setOnDismissListener(dismiss);

	}

}
