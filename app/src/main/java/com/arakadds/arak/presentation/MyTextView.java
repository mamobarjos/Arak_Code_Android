package com.arakadds.arak.presentation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.arakadds.arak.R;
import com.arakadds.arak.common.helper.FontHelper;


public class MyTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(attrs);
        }

    }

    public MyTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(null);
        }
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView);
            String fontID = a.getString(R.styleable.CustomView_typeface);
            if (fontID != null) {
                String fontName= FontHelper.getTypeFaceByPosition(getContext(), Integer.parseInt(fontID));
                Typeface myTypeface = FontHelper.getFont(getContext(), fontName);
                setTypeface(myTypeface);
            } else {
                Typeface myTypeface = FontHelper.getFont(getContext()
                        , FontHelper.getFontNameRegular(getContext()));
                setTypeface(myTypeface);
            }
            a.recycle();
        }
    }

}