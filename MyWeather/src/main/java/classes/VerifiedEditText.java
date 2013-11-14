package classes;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;

/**
 * Created by Macklin Underdown on 11/11/13.
 * GitHub: @macklinu
 */
public class VerifiedEditText extends EditText {

    Integer mZipCode;
    static final int ZIP_CODE_MIN = 1;
    static final int ZIP_CODE_MAX = 99999;

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
        this.setFilters(new InputFilter[] { lengthFilter });
        this.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.setMaxWidth(60);
    }

    public boolean isValid() {
        Integer zip = getText().toString().length() == 0 ?
                null : Integer.parseInt(getText().toString().trim());
        if (zip != null &&
                zip >= ZIP_CODE_MIN &&
                zip <= ZIP_CODE_MAX &&
                zip.toString().length() == 5) {
            mZipCode = zip;
            return true;
        } else {
            mZipCode = null;
            return false;
        }
    }

    public Integer getZipCode() {
        return mZipCode;
    }

}

