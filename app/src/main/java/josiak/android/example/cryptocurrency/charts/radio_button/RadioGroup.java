package josiak.android.example.cryptocurrency.charts.radio_button;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;

import josiak.android.example.cryptocurrency.charts.R;

/**
 * Created by Kuba on 2018-06-27.
 */

public class RadioGroup extends LinearLayout {
    private int mCheckedId = View.NO_ID;
    private boolean mProtectFromCheckedChange = false;
    private HashMap<Integer, View> mChildViewsMap = new HashMap<>();


    private OnCheckedChangeListener mOnCheckedChangeListener;
    private RadioCheckable.OnCheckedChangeListener mChildOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    public RadioGroup(Context context) {
        super(context);
        setupView();
    }

    public RadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributes(attrs);
        setupView();
    }

    public RadioGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(attrs);
        setupView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RadioGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(attrs);
        setupView();
    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext()
                .obtainStyledAttributes(attrs, R.styleable.RadioGroup, 0, 0);
        mCheckedId = a.getResourceId(R.styleable.RadioGroup_checkedId, View.NO_ID);
        a.recycle();
    }

    private void setupView() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

/*    @Override
    public void addView(View child) {
        if(child instanceof RadioCheckable){
            final RadioCheckable button = (RadioCheckable) child;
            if(button.isChecked()){
                mProtectFromCheckedChange = true;
                if(mCheckedId != View.NO_ID){
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
            }
        }
        super.addView(child);
    }*/

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if(mCheckedId != View.NO_ID){
            mProtectFromCheckedChange = true;
            setCheckedStateForView(mCheckedId, true);
            mProtectFromCheckedChange = false;
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId);
    }

    private void setCheckedId(@IdRes int id, boolean isChecked) {
        mCheckedId = id;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mChildViewsMap.get(id), isChecked, mCheckedId);
        }

    }

    private void setCheckedStateForView(int viewId, boolean isChecked) {
        View checkedView = mChildViewsMap.get(viewId);
        if (checkedView == null) {
            checkedView = findViewById(viewId);
            if (checkedView != null) {
                mChildViewsMap.put(viewId, checkedView);
            }
        }
        if (checkedView != null && checkedView instanceof RadioCheckable) {
            ((RadioCheckable) checkedView).setChecked(isChecked);
        }
    }

    private class CheckedStateTracker implements RadioCheckable.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(View buttonView) {
            if (mProtectFromCheckedChange)
                return;

            mProtectFromCheckedChange = true;
            if (mCheckedId != View.NO_ID) {
                setCheckedStateForView(mCheckedId, false);
            }
            mProtectFromCheckedChange = false;

            int id = buttonView.getId();
            setCheckedId(id, true);
        }
    }

    private class PassThroughHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {
        private ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;

        @Override
        public void onChildViewAdded(View parent, View child) {
            if (parent == RadioGroup.this && child instanceof RadioCheckable) {
                int id = child.getId();
                if (id == View.NO_ID) {
                    id = View.generateViewId();
                    child.setId(id);
                }
                ((RadioCheckable) child).addOnCheckChangeListener(mChildOnCheckedChangeListener);
                mChildViewsMap.put(id, child);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (parent == RadioGroup.this && child instanceof RadioCheckable) {
                ((RadioCheckable) child).removeOnCheckChangeListener(mChildOnCheckedChangeListener);
            }
            mChildViewsMap.remove(child.getId());
            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

}
