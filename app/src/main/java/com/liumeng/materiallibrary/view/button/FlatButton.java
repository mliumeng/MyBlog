package com.liumeng.materiallibrary.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liumeng.materiallibrary.R;
import com.liumeng.materiallibrary.view.utils.DensityUtil;


/**
 * ****************************************************
 * 项目名：Blog
 * 创建时间：2017/4/7
 * 创建人：刘蒙
 * 功能：背景透明的平面Button
 * *****************************************************
 */

public class FlatButton extends RelativeLayout {
    private static final String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    /**
     * 自定义View注意事项：
     * <p>
     * 1. 设置默认大小mini
     * 2. 读取xml中设置的大小
     * 3. 读取xml中的其他属性值
     */
    TextView textView;
    boolean clickAfterRipple = true; //是否动画完成后响应点击事件
    int paddingTop = DensityUtil.dp2px(getContext(), 4);
    int paddingBottom = DensityUtil.dp2px(getContext(), 4);
    int paddingLeft = DensityUtil.dp2px(getContext(), 6);
    int paddingRight = DensityUtil.dp2px(getContext(), 6);
    private int defWidth = 48;
    private int defHeight = 23;
    private int textColor = Color.parseColor("#000080");//deflate color
    private float textSize = DensityUtil.sp2px(getContext(), 16f);//deflate color
    private int rippleColor = Color.parseColor("#17434343");
    private String text;
    private int background;//此按钮不需要
    private float x = -1, y = -1;
    private int rippleSize = 9;
    private int rippleSpeed = 10;
    private int radius = 15;

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    private boolean textAllCaps;
    private OnClickListener onClickListener;

    public FlatButton(Context context) {
        this(context, null);
    }

    public FlatButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textView = new TextView(context);
        this.addView(textView);
        setDefWidthOrHeight();// 设置默认大小
        initButtonAttr(context, attrs, defStyleAttr);// 设置xml属性
        setButton();
    }

    /**
     * 设置按钮
     */
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

    public int getRippleSize() {
        return rippleSize;
    }

    /**
     * 动画速度
     *
     * @param rippleSize
     */
    public void setRippleSize(int rippleSize) {
        this.rippleSize = rippleSize;
    }

    public boolean isClickAfterRipple() {
        return clickAfterRipple;
    }

    /**
     * 是否在水波纹动画结束后执行click事件
     *
     * @param clickAfterRipple
     */
    public void setClickAfterRipple(boolean clickAfterRipple) {
        this.clickAfterRipple = clickAfterRipple;
    }

    public int getTextColor() {
        return textColor;
    }

    /**
      * 设置button文本颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return DensityUtil.px2sp(getContext(), textSize);
    }

    /**
     * 设置字体大小，单位px
     *
     * @param textSize px
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * 获取属性值
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initButtonAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        // ======== - 获取系统属性值 - ================
        //1.获取text
        int textResource = attrs.getAttributeResourceValue(ANDROIDXML, "text", -1);
        if (textResource == -1) {
            setText(attrs.getAttributeValue(ANDROIDXML, "text"));
        } else {
            setText(getResources().getString(textResource));
        }

        //2.获取背景色(这个按钮背景色为透明)
        background = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        setBackgroundColor(Color.TRANSPARENT);

        //3.获取文字颜色
        int mTextColor = attrs.getAttributeResourceValue(ANDROIDXML, "textColor", -1);
        if (mTextColor == -1) {
            int textColor = attrs.getAttributeIntValue(ANDROIDXML, "textColor", -1);
            if (textColor != -1) setTextColor(textColor);
        } else setTextColor(getResources().getColor(mTextColor));

        //4.获取textSize
        int mTextSize = attrs.getAttributeResourceValue(ANDROIDXML, "textSize", -1);
        if (mTextSize == -1) {
            String textSize = attrs.getAttributeValue(ANDROIDXML, "textSize");
            if (!TextUtils.isEmpty(textSize)) {
                if (textSize.endsWith("sp")) {
                    String substring = textSize.substring(0, textSize.length() - 2);
                    float v = Float.parseFloat(substring);
                    setTextSize(DensityUtil.sp2px(context, v));
                } else {
                    //TODO 暂时仅支持SP单位
                }
            }

            if (mTextSize != -1) setTextSize(mTextSize);
        } else setTextSize(getResources().getDimension(mTextSize));
        //5.设置是默认大写
        textAllCaps = attrs.getAttributeBooleanValue(ANDROIDXML, "textAllCaps", true);
        //6.设置padding值

        // ======== - 获取自定义属性值 - ================
        //1. 获取水波纹颜色
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonAttributes);
        rippleColor = typedArray.getColor(R.styleable.ButtonAttributes_mRippleColor, rippleColor);

    }

    /**
     * 设置默认宽高
     */
    private void setDefWidthOrHeight() {
//        setMinimumWidth(DensityUtil.dp2px(getContext(), defWidth));
//        setMinimumHeight(DensityUtil.dp2px(getContext(), defHeight));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        if (isEnabled()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                radius = getHeight() / rippleSize;
                x = event.getX();
                y = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                radius = getHeight()  / rippleSize;
                x = event.getX();
                y = event.getY();
                if ((!(event.getX() > 0 && event.getX() <= getWidth()) &&
                        event.getY() > 0 && event.getY() <= getHeight())) {
                    x = -1;
                    y = -1;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if ((event.getX() <= getWidth() && event.getX() >= 0)
                        && (event.getY() <= getHeight() && event.getY() >= 0)) {
                    // todo Click Listener
                    radius++;
                    if (!clickAfterRipple && onClickListener != null)
                        onClickListener.onClick(this);
                } else {
                    x = -1;
                    y = -1;
                }
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                x = -1;
                y = -1;
            }
        }

        return true;//拦截onTouch事件
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
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (x != -1) {
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
            invalidate();
        }
    }

    private void sout(String s) {
        System.out.println("===>======>=======> " + s);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap cropCircle(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public Bitmap makeCircle() {
        Bitmap output = Bitmap.createBitmap(
                getWidth() - 12, getHeight()
                        - 14, Bitmap.Config.ARGB_8888);
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
//            if (onClickListener != null && clickAfterRipple)
//                onClickListener.onClick(this);
        }
        return output;
    }


}
