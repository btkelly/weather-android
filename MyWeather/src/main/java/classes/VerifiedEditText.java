package classes;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;

/**
 * Created by macklinu on 11/11/13.
 */
public class VerifiedEditText extends EditText {

    String value;

    public VerifiedEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        // set your input filter here
        int maxLength = 5;
        // define filters
        InputFilter numFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    // System.out.println("Type : "+Character.getType(source.charAt(i)));
                    if (Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        InputFilter lengthFilter = new InputFilter.LengthFilter(maxLength);
        // create the filters
        // do I need numFilter if my input type is TYPE_CLASS_NUMBER?
        this.setFilters(new InputFilter[] {lengthFilter});
        this.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.setMaxWidth(60);
    }

    private boolean isError() {
        value = this.getText().toString().trim();
        return value.length() == 5;
    }

}

