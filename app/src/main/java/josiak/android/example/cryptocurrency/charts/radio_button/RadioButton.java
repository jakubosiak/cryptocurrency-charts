package josiak.android.example.cryptocurrency.charts.radio_button;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import josiak.android.example.cryptocurrency.charts.R;

/**
 * Created by Kuba on 2018-06-26.
 */

public class RadioButton extends RelativeLayout implements RadioCheckable {
    private String mValue;
    private int mTextColorNormal;
    private int mTextColorPressed;
    private TextView mTextViewValue;

    private Drawable mInitialBackgroundDrawable;
    private OnClickListener mOnClickListener;
    private OnTouchListener mOnTouchListener;
    private boolean mChecked;
    private ArrayList<OnCheckedChangeListener> mOnCheckedChangeListeners = new ArrayList<>();


    public RadioButton(Context context) {
        super(context);
        setupView();
    }

    public RadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
        setupView();
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(attrs);
        setupView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(attrs);
        setupView();
    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RadioButton, 0, 0);
        mValue = a.getString(R.styleable.RadioButton_radioButtonTextViewValue);
        mTextColorNormal = a.getColor(R.styleable.RadioButton_radioButtonTextColor,
                ContextCompat.getColor(getContext(), R.color.textColor));
        mTextColorPressed = a.getColor(R.styleable.RadioButton_radioButtonPressedTextColor,
                ContextCompat.getColor(getContext(), R.color.colorAccent));
        a.recycle();
    }

    private void setupView() {
        inflateView();
        bindView();
        setCustomOnTouchListener();
    }

    private void inflateView() {
        LayoutInflater.from(getContext()).inflate(R.layout.custom_radio_button, this, true);
        mTextViewValue = findViewById(R.id.rb_text_value);
        mInitialBackgroundDrawable = getBackground();
    }

    private void bindView() {
        mTextViewValue.setTextColor(mTextColorNormal);
        mTextViewValue.setText(mValue);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
    }

    protected void setCustomOnTouchListener() {
        super.setOnTouchListener(new TouchListener());
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }

    @Override
    public void addOnCheckChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListeners.add(onCheckedChangeListener);
    }

    @Override
    public void removeOnCheckChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListeners.remove(onCheckedChangeListener);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            if (!mOnCheckedChangeListeners.isEmpty()) {
                for (int i = 0; i < mOnCheckedChangeListeners.size(); i++) {
                    mOnCheckedChangeListeners.get(i).onCheckedChanged(this);
                }
            }
            if (mChecked)
                setCheckedState();
            else
                setNormalState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    public String getValue() {
        return mValue;
    }

    private void setNormalState() {
        setBackground(mInitialBackgroundDrawable);
        mTextViewValue.setTextColor(mTextColorNormal);
    }

    private void setCheckedState() {
        setBackgroundResource(R.drawable.background_state_pressed);
        mTextViewValue.setTextColor(mTextColorPressed);
    }

    private void onTouchUp() {
        if (mOnClickListener != null)
            mOnClickListener.onClick(this);
    }

    private void onTouchDown() {
        setChecked(true);
    }

    private final class TouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchDown();
                    break;
                case MotionEvent.ACTION_UP:
                    onTouchUp();
                    break;
            }
            if (mOnTouchListener != null) {
                mOnTouchListener.onTouch(v, event);
            }

            return true;
        }
    }
}
