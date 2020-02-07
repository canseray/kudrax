package tr.limonist.extras;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import tr.limonist.kudra.R;

public class TransparentProgressDialog extends Dialog {
	
	TextView tv;
	
	public TransparentProgressDialog(Context context,String text,boolean cancel) {
	super(context, R.style.TransparentProgressDialog);
    setContentView(R.layout.a_loading);	
    
    this.setCancelable(cancel);
    this.setCanceledOnTouchOutside(cancel);
    
//    tv=(TextView)findViewById(R.id.tv);
//    tv.setText(text);
    
	}
	
	@Override
	public void show() {
		super.show();
	}
	
}
