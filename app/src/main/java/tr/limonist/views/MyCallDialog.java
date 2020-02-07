package tr.limonist.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import tr.limonist.kudra.R;

public class MyCallDialog extends Dialog {

	TextView tv_cancel,tv_call,txt_no;
	Context m_context;

	public MyCallDialog(Context context, final String number) {
		super(context);
		m_context=context;
		setContentView(R.layout.a_my_call_dialog);

		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		tv_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		tv_call = (TextView) findViewById(R.id.tv_call);
		tv_call.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				m_context.startActivity(new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
				dismiss();

			}
		});
		txt_no = (TextView) findViewById(R.id.txt_no);
		txt_no.setText(number);

		show();
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
