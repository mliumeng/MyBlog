package com.liumeng.materiallibrary.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liumeng.materiallibrary.R;
import com.liumeng.materiallibrary.view.utils.DensityUtil;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * ****************************************************
 * 项目名：Blog
 * 创建时间：2017/4/16
 * 创建人：刘蒙
 * 功能：与FlatButton相比 有背景有高度（阴影）
 * *****************************************************
 */

public class RectangleButton extends RelativeLayout {
    int paddingTop = DensityUtil.dp2px(getContext(), 3);
    int paddingBottom = DensityUtil.dp2px(getContext(), 3);
    int paddingLeft = DensityUtil.dp2px(getContext(), 15);
    int paddingRight = DensityUtil.dp2px(getContext(), 15);
    private String ANDROID = "http://schemas.android.com/apk/res/android"; // android 属性值命名空间
    private TextView textView;// 用于展示button中的文字
    private String text;//button的文字
    private int textColor = Color.parseColor("#000080");//文字颜色
    private float textSize = DensityUtil.sp2px(getContext(), 16f);//默认字体大小;
    //    private Color backgroundColor;// button背景色
//    private Drawable background;// button 背景
    private int rippleColor = Color.parseColor("#17434343");// 水波纹的颜色
    private int rippleSize = 9;// 决定水波纹起始半径的大小 radius = getHeight() / rippleSize;
    private int rippleSpeed = 20;// 一定程度上控制了水波纹效果波动速度
    private float x = -1, y = -1;//圆形水波纹的中心点（圆点）
    private boolean clickAfterRipple = true; //是否动画完成后响应点击事件 默认是
    private boolean textAllCaps = true; // 是否文字都大写
    private OnClickListener onClickListener; //点击监听
    private int radius;
    private int OUTSIDE = -1;
    private int defWidth = 48;
    private int defHeight = 23;

    public RectangleButton(Context context) {
        this(context, null);
    }

    public RectangleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectangleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setDefWidthOrHeight();
        setAttribute(attrs);
        setButton();
    }

    /**
     * 设置默认宽高
     */
    private void setDefWidthOrHeight() {
        setMinimumWidth(DensityUtil.dp2px(getContext(), defWidth));
        setMinimumHeight(DensityUtil.dp2px(getContext(), defHeight));
    }
    private void setButton() {
        if (getText() != null) {
            textView.setText(getText());
        }
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        LayoutParams params = (LayoutParams) textView.getLayoutParams();
        params.addRule(CENTER_IN_PARENT);
        textView.setLayoutParams(params);
        textView.setTextSize(getTextSize());
        textView.setTextColor(getTextColor());
        textView.setAllCaps(textAllCaps);

    }


    private void initView() {
        setBackgroundResource(R.drawable.rectangle_button_bg);
        textView = new TextView(getContext());
        addView(textView);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return true;
    }

    /**
     * 获取属性值
     *
     * @param attrs
     */
    private void setAttribute(AttributeSet attrs) {
        // ======== - 获取系统属性值 - ================
        // 1. 获取text
        int textResource = attrs.getAttributeResourceValue(ANDROID, "text", -1);
        if (textResource == -1) {
            String text = attrs.getAttributeValue(ANDROID, "text");
            if (text != null) setText(text);
        } else {
            setText(getResources().getString(textResource));
        }
        // 2. 获取背景色 backgroundColor
        int backgroundColorResource = attrs.getAttributeResourceValue(ANDROID, "backgroundColor", -1);
        if (backgroundColorResource == -1) {
            int backgroundColor = attrs.getAttributeIntValue(ANDROID, "backgroundColor", -1);
            if (backgroundColor != -1) {
                mSetBackgroundColor(backgroundColor);
            } else {
                mSetBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        } else {
            mSetBackgroundColor(backgroundColorResource);
        }
        //3. 获取textColor
//        int mTextColor = attrs.getAttributeResourceValue(ANDROID, "textColor", -1);
//        if (mTextColor == -1) {
//            int textColor = attrs.getAttributeIntValue(ANDROID, "textColor", -1);
//            if (textColor != -1) setTextColor(textColor);
//        } else setTextColor(getResources().getColor(mTextColor));

        int mTextColor = attrs.getAttributeResourceValue(ANDROID, "textColor", -1);
        if (mTextColor == -1) {
            int textColor = attrs.getAttributeIntValue(ANDROID, "textColor", -1);
            if (textColor != -1) setTextColor(textColor);
        } else setTextColor(getResources().getColor(mTextColor));

        //4. 获取textSize
        int mTextSize = attrs.getAttributeResourceValue(ANDROID, "textSize", -1);
        if (mTextSize == -1) {
            String textSize = attrs.getAttributeValue(ANDROID, "textSize");
            if (!TextUtils.isEmpty(textSize)) {
                if (textSize.endsWith("sp")) {
                    String substring = textSize.substring(0, textSize.length() - 2);
                    float v = Float.parseFloat(substring);
                    setTextSize(DensityUtil.sp2px(getContext(), v));
                } else {

                }
            }

            if (mTextSize != -1) setTextSize(mTextSize);
        } else setTextSize(getResources().getDimension(mTextSize));
        //5. 设置是默认大写textAllCaps
        boolean textAllCaps = attrs.getAttributeBooleanValue(ANDROID, "textAllCaps", true);
        setTextAllCaps(textAllCaps);
        // ======== - 获取自定义属性值 - ================
        //1. 获取水波纹颜色
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonAttributes);
        rippleColor = typedArray.getColor(R.styleable.ButtonAttributes_mRippleColor, rippleColor);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        if (!gainFocus) {
            x = -1;
            y = -1;
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        if (isEnabled()) {
            int action = event.getAction();
            if (action == ACTION_DOWN) {
                radius = getHeight() / rippleSize;
                x = event.getX();
                y = event.getY();
            } else if (action == ACTION_MOVE) {
                radius = getHeight() / rippleSize;
                if ((event.getX() >= 0 && event.getX() <= getWidth()) && event.getY() >= 0 && event.getY() <= getHeight()) {
                    x = event.getX();
                    y = event.getY();
                } else {
                    x = OUTSIDE;
                    y = OUTSIDE;
                }
            } else if (action == ACTION_UP) {
                radius++;
                if ((event.getX() >= 0 && event.getX() <= getWidth()) && event.getY() >= 0 && event.getY() <= getHeight()) {
                    x = event.getX();
                    y = event.getY();
                    if (!clickAfterRipple && onClickListener != null)
                        onClickListener.onClick(this);
                } else {
                    x = OUTSIDE;
                    y = OUTSIDE;
                }
            } else if (action == ACTION_CANCEL) {
                x = OUTSIDE;
                y = OUTSIDE;
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != OUTSIDE) {
            Rect src = new Rect(0, 0, getWidth() - DensityUtil.dp2px(getContext(), 6),
                    getHeight() - DensityUtil.dp2px(getContext(), 7));
            Rect dst = new Rect(DensityUtil.dp2px(getContext(), 6), DensityUtil.dp2px(getContext(), 6),
                    getWidth() - DensityUtil.dp2px(getContext(), 6), getHeight() - DensityUtil.dp2px(getContext(), 7));
            canvas.drawBitmap(makeCircle(), src, dst, null);
//            canvas.drawCircle(x, y, radius, paint);
//            if (radius > getHeight() / rippleSize)
//                radius += rippleSpeed;
//            if (radius >= getWidth()) {
//                x = -1;
//                y = -1;
//                radius = getHeight() / rippleSize;
//                if (onClickListener != null && clickAfterRipple)
//                    onClickListener.onClick(this);
//            }
            invalidate();
        }
    }

    public Bitmap makeCircle() {
        Bitmap output = Bitmap.createBitmap(
                getWidth() - DensityUtil.dp2px(getContext(), 6), getHeight()
                        - DensityUtil.dp2px(getContext(), 7), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(rippleColor);
        canvas.drawCircle(x, y, radius, paint);
        if (radius > getHeight() / rippleSize)
            radius += rippleSpeed;
        if (radius >= getWidth()) {
            x = -1;
            y = -1;
            radius = getHeight() / rippleSize;
            if (onClickListener != null && clickAfterRipple)
                onClickListener.onClick(this);
        }
        return output;
    }

    public void mSetBackgroundColor(int color) {
        try {
            LayerDrawable layerDrawable = (LayerDrawable) getBackground();
            GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable
                    .findDrawableByLayerId(R.id.bg_sharp);
            gradientDrawable.setColor(color);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRippleColor() {
        return rippleColor;
    }

    public void setRippleColor(int rippleColor) {
        this.rippleColor = rippleColor;
    }

    public int getRippleSize() {
        return rippleSize;
    }

    public void setRippleSize(int rippleSize) {
        this.rippleSize = rippleSize;
    }

    public int getRippleSpeed() {
        return rippleSpeed;
    }

    public void setRippleSpeed(int rippleSpeed) {
        this.rippleSpeed = rippleSpeed;
    }

    public boolean isClickAfterRipple() {
        return clickAfterRipple;
    }

    public void setClickAfterRipple(boolean clickAfterRipple) {
        this.clickAfterRipple = clickAfterRipple;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return DensityUtil.px2sp(getContext(), textSize);
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

}
