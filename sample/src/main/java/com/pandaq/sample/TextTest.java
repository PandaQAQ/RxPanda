package com.pandaq.sample;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;

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
