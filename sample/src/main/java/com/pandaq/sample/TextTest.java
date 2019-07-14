package com.pandaq.sample;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by huxinyu on 2019/7/12.
 * Email : panda.h@foxmail.com
 * Description :
 */
public class TextTest extends TextView {
    public TextTest(Context context) {
        super(context);
    }

    public TextTest(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextTest(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TextTest(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void invalidate() {
        super.invalidate();
    }

}
