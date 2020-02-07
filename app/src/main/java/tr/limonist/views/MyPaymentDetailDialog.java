package tr.limonist.views;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;


import tr.limonist.kudra.R;
import tr.limonist.classes.PaymentDetailItem;
import tr.limonist.extras.MyTextView;

public class MyPaymentDetailDialog extends Dialog {

	Activity m_activity;
	ArrayList<PaymentDetailItem> results;
	
	public MyPaymentDetailDialog(Activity context, ArrayList<PaymentDetailItem> r) {
		super(context, android.R.style.Theme_Black_NoTitleBar);
		m_activity=context;
		results=r;
		getWindow().setBackgroundDrawableResource(R.color.a_black12);
		setContentView(R.layout.a_dialog_payment_detail);

		LinearLayout lay_main=(LinearLayout)findViewById(R.id.lay_main);
		lay_main.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		ListView list =(ListView)findViewById(R.id.list);
		lazy adapter = new lazy();
		list.setAdapter(adapter);
		
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
		public PaymentDetailItem getItem(int position) {
			return results.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public class ViewHolder {

			MyTextView title,desc;

		}

		public View getView(final int position, View view, ViewGroup parent) {
			final PaymentDetailItem item = results.get(position);

			final ViewHolder holder;
			if (view == null) {
				holder = new ViewHolder();
				view = inflater.inflate(R.layout.c_item_payment_detail, null);

				holder.title = (MyTextView) view.findViewById(R.id.title);
				holder.desc = (MyTextView) view.findViewById(R.id.desc);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.title.setText(item.getTitle());
			holder.desc.setText(item.getDesc());

			return view;
		}

	}
}
