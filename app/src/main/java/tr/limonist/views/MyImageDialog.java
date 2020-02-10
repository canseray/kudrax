package tr.limonist.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import tr.limonist.extras.MyTextView;
import tr.limonist.kudra.R;

public class MyImageDialog extends Dialog {

    MyTextView title, content;
    MyTextView okButton;

    @Override
    public void onBackPressed() {

    }

    public MyImageDialog(Context context, String title, String content, String ok_btn,
                         boolean cancel) {
        super(context, android.R.style.Theme_Black_NoTitleBar);
        getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.a_black12)));
        setContentView(R.layout.a_my_image_dialog);

        this.setCancelable(cancel);
        this.setCanceledOnTouchOutside(cancel);



        this.title = (MyTextView) findViewById(R.id.title_my);
        this.title.setText(title);

        this.content = (MyTextView) findViewById(R.id.content_my);
        this.content.setText(content);

        this.okButton = (MyTextView) findViewById(R.id.positive_my);
        this.okButton.setText(ok_btn);


    }

    public void setOkClick(View.OnClickListener okListeer) {
        this.okButton.setOnClickListener(okListeer);

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
