package hanium.toast.mytoast.customdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import hanium.toast.mytoast.R;

public class CustomDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "CustomDialog";

    // component
    private EditText newfilename_ed;
    private Button okButton;
    private Button cancelButton;

    // var
    private String new_file_name;
    private String before_file_name;

    // others
    private Context context;
    private MyDialogListener dialogListener;


    public void setDialogListener(MyDialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }

    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context = context;

    }

    public CustomDialog(@NonNull Context context, String before_file_name) {
        super(context);
        this.context = context;
        this.before_file_name = before_file_name;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        newfilename_ed = (EditText) findViewById(R.id.newfilename);
        okButton = (Button) findViewById(R.id.okButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);

        newfilename_ed.setText(before_file_name);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        if (new_file_name != null) {
            newfilename_ed.setText(new_file_name);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                Toast.makeText(context, "확인", Toast.LENGTH_SHORT).show();
                new_file_name = newfilename_ed.getText().toString();
                dialogListener.onOkCliked(new_file_name);
                dismiss();
                break;

            case R.id.cancelButton:
                Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                dismiss();
                break;

        }
    }

    public interface MyDialogListener {
        public void onOkCliked(String new_file_name);

        public void onCancelClicked();
    }
}


