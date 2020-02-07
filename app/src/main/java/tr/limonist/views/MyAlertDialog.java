package tr.limonist.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import tr.limonist.extras.MyTextView;
import tr.limonist.kudra.R;

public class MyAlertDialog extends Dialog {

	MyTextView title, content;
	MyTextView negative, positive;
	ImageView img;

	public MyAlertDialog(Context context, int img, String title, String content, String pos_text, String neg_text,
			boolean cancel) {
		super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.a_black12)));
		setContentView(R.layout.a_my_alert_dialog);

		this.setCancelable(cancel);
		this.setCanceledOnTouchOutside(cancel);

		this.img = (ImageView) findViewById(R.id.img);
		this.img.setImageResource(img);
		if(img==0)
			this.img.setVisibility(View.GONE);

		this.title = (MyTextView) findViewById(R.id.title);
		this.title.setText(title);

		this.content = (MyTextView) findViewById(R.id.content);
		this.content.setText(content);

		this.negative = (MyTextView) findViewById(R.id.negative);
		this.negative.setText(neg_text);

		this.positive = (MyTextView) findViewById(R.id.positive);
		this.positive.setText(pos_text);

	}

	public void setNegativeClicl(View.OnClickListener neg_listener) {
		this.negative.setOnClickListener(neg_listener);

	}

	public void setPositiveClicl(View.OnClickListener pos_listener) {
		this.positive.setOnClickListener(pos_listener);

	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

}
