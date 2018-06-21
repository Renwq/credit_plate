package com.renwq.creditplate;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;



/**
 * 类作用：
 * Created by rwq_Administrator on 2018/6/20.
 */
public class CreditCheckView extends View {
    private static final int BAD = 0;
    private static final int MEDIUM = 1;
    private static final int GOOD = 2;
    private static final int BEST = 3;
    private static final int TIPTOP = 4;

    private boolean isChangeColor;
    private int badColor;
    private int mediumColor;
    private int goodColor;
    private int bestColor;
    private int tiptopColor;
    private int normalColor;
    private int defaultCreditColor;
    private int maxCreditNum;
    private int pointColor;
    private int minWidth= 0;
    private Paint paint;
    private TextPaint textPaint;
    private Path textPath;

    private int currentCreditValue = 0;
    private float mean;
    private String evaluateTime = "评估时间:2018-6-21";
    private float pointMeanDegrees;

    public CreditCheckView(Context context) {
        super(context);
        init();
        initDrawInfo();
    }


    public CreditCheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CreditCheckView);
        isChangeColor = typedArray.getBoolean(R.styleable.CreditCheckView_is_change_color, false);
        badColor = typedArray.getColor(R.styleable.CreditCheckView_bad_color, Color.parseColor("#a60909"));
        mediumColor = typedArray.getColor(R.styleable.CreditCheckView_medium_color, Color.parseColor("#cccc00"));
        goodColor = typedArray.getColor(R.styleable.CreditCheckView_good_color, Color.parseColor("#72f872"));
        bestColor = typedArray.getColor(R.styleable.CreditCheckView_best_color, Color.parseColor("#09a609"));
        tiptopColor = typedArray.getColor(R.styleable.CreditCheckView_tiptop_color, Color.parseColor("#00ffff"));
        normalColor = typedArray.getColor(R.styleable.CreditCheckView_normal_color, Color.parseColor("#7d91f0"));
        pointColor = typedArray.getColor(R.styleable.CreditCheckView_pointer_color, Color.parseColor("#7d91f2"));
        maxCreditNum = typedArray.getInteger(R.styleable.CreditCheckView_max_credit_num, 1400);
        typedArray.recycle();
        initDrawInfo();
    }

    private void init() {
        isChangeColor = false;
        badColor = Color.parseColor("#a60909");
        mediumColor = Color.parseColor("#cccc00");
        goodColor = Color.parseColor("#72f872");
        bestColor = Color.parseColor("#09a609");
        tiptopColor = Color.parseColor("#00ffff");
        normalColor = Color.parseColor("#7d91f0");
        pointColor = Color.parseColor("#7d91f2");
        maxCreditNum = 1400;

    }

    private void initDrawInfo() {
        minWidth = dp2px(180);
        paint = new Paint();
        paint.setColor(normalColor);
        textPaint = new TextPaint();
        textPaint.setColor(normalColor);
        textPaint.setTextSize(18);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPath = new Path();
        if (currentCreditValue > maxCreditNum) {
            throw new IllegalArgumentException("当前信用值大于最大信用值");
        }
        mean = maxCreditNum / 5;
        pointMeanDegrees = 210f / maxCreditNum;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int resultWidth = 0;
        int resultHeight = 0;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                if (width < minWidth) {
                    resultWidth = minWidth;
                    resultHeight = resultWidth;
                } else {
                    resultWidth = width;
                    resultHeight = width;
                }
                break;
            case MeasureSpec.EXACTLY:
                if (width < minWidth) {
                    resultWidth = minWidth;
                    resultHeight = resultWidth;
                } else {
                    resultWidth = width;
                    resultHeight = resultWidth;
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                resultWidth = minWidth;
                resultHeight = resultWidth;
                break;
        }
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawPeriphery(canvas);
        drawText(canvas);
    }

    /**
     * 绘制内部文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        canvas.restore();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        textPaint.setTextSize(20);
        textPaint.setStyle(Paint.Style.FILL);
        if (currentCreditValue >= 0 && currentCreditValue < mean) {
            //bad
            drawCreditGrade(canvas, BAD);
        } else if (currentCreditValue >= mean && currentCreditValue < mean * 2) {
            drawCreditGrade(canvas, MEDIUM);
        } else if (currentCreditValue >= mean * 2 && currentCreditValue < mean * 3) {
            drawCreditGrade(canvas, GOOD);
        } else if (currentCreditValue >= mean * 3 && currentCreditValue < mean * 4) {
            drawCreditGrade(canvas, BEST);
        } else {
            drawCreditGrade(canvas, TIPTOP);
        }
        textPaint.setTextSize(120);
        textPaint.setStrokeWidth(6);
        int textWidth = (int) textPaint.measureText(currentCreditValue + "", 0, String.valueOf(currentCreditValue).length());
        canvas.drawText(currentCreditValue + "", (0 - textWidth / 2), -50, textPaint);
        textPaint.setTextSize(20);
        textPaint.setStrokeWidth(4);
        int dateWidth = (int) textPaint.measureText(evaluateTime, 0, evaluateTime.length());
        canvas.drawText(evaluateTime, (0 - dateWidth / 2), 70, textPaint);
    }


    /**
     * 绘制级别文件
     *
     * @param canvas
     * @param grade
     */
    private void drawCreditGrade(Canvas canvas, int grade) {
        int textWidth = 0;
        int contentTextWidth = 0;
        String en_text = "";
        String cn_text = "";
        switch (grade) {
            case BAD:
                en_text = "BAD";
                cn_text = "信用极差";
                break;
            case MEDIUM:
                en_text = "MEDIUM";
                cn_text = "信用中等";
                break;
            case GOOD:
                en_text = "GOOD";
                cn_text = "信用好";
                break;
            case BEST:
                en_text = "BEST";
                cn_text = "信用很好";
                break;
            case TIPTOP:
                en_text = "TIPTOP";
                cn_text = "信用极好";
                break;
        }
        textWidth = (int) textPaint.measureText(en_text, 0, en_text.length());
        canvas.drawText(en_text, (0 - textWidth / 2), -200, textPaint);
        textPaint.setTextSize(30);
        textPaint.setStrokeWidth(3);
        contentTextWidth = (int) textPaint.measureText(cn_text, 0, cn_text.length());
        canvas.drawText(cn_text, 0 - contentTextWidth / 2, 30, textPaint);
    }

    /**
     * 绘制外围
     *
     * @param canvas
     */
    private void drawPeriphery(Canvas canvas) {
        canvas.save();
        int width = getWidth();
        int height = getHeight();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(30, 30, width - 30, width - 30, 165, 210, false, paint);
        }
        paint.setStrokeWidth(7);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(105, 105, width - 105, width - 105, 165, 210, false, paint);
        }
        paint.setStrokeWidth(4);
        canvas.translate(width / 2, height / 2);
        //        canvas.rotate(165);
        canvas.rotate(-15);
        for (int i = 0; i < 29; i++) {
            if (i % 2 == 0) {
                paint.setStrokeWidth(6);
                canvas.drawLine(-(width / 2 - 105), 0, -(width / 2 - 85), 0, paint);
                if (i != 0 && i != 28) {
                    textPath.moveTo(-(width / 2 - 75), 20);
                    textPath.lineTo(-(width / 2 - 75), -20);
                    canvas.drawTextOnPath(maxCreditNum / 14 * (i / 2) + "", textPath, 0, 0, textPaint);
                    textPath.reset();
                    // canvas.drawText(maxCreditNum / 14 * (i / 2) + "", width / 2 - 65, 0, textPaint);
                }
            } else {
                paint.setStrokeWidth(4);
                canvas.drawLine(-(width / 2 - 105), 0, -(width / 2 - 90), 0, paint);
            }
            canvas.rotate(7.5f);
        }
        drawPointer(canvas, currentCreditValue);
    }

    private void drawPointer(Canvas canvas, int currentCreditValue) {
        canvas.restore();
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(currentCreditValue * pointMeanDegrees + (-195));
        canvas.drawCircle(getWidth() / 2 - 30, 0, 20, textPaint);
    }

    public boolean isChangeColor() {
        return isChangeColor;
    }

    public void setChangeColor(boolean changeColor) {
        isChangeColor = changeColor;
    }

    public int getBadColor() {
        return badColor;
    }

    public void setBadColor(int badColor) {
        this.badColor = badColor;
    }

    public int getMediumColor() {
        return mediumColor;
    }

    public void setMediumColor(int mediumColor) {
        this.mediumColor = mediumColor;
    }

    public int getGoodColor() {
        return goodColor;
    }

    public void setGoodColor(int goodColor) {
        this.goodColor = goodColor;
    }

    public int getBestColor() {
        return bestColor;
    }

    public void setBestColor(int bestColor) {
        this.bestColor = bestColor;
    }

    public int getTiptopColor() {
        return tiptopColor;
    }

    public void setTiptopColor(int tiptopColor) {
        this.tiptopColor = tiptopColor;
    }

    public int getNormalColor() {
        return normalColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public int getDefaultCreditColor() {
        return defaultCreditColor;
    }

    public void setDefaultCreditColor(int defaultCreditColor) {
        this.defaultCreditColor = defaultCreditColor;
    }

    public int getMaxCreditNum() {
        return maxCreditNum;
    }

    public void setMaxCreditNum(int maxCreditNum) {
        this.maxCreditNum = maxCreditNum;
    }

    public int getPointColor() {
        return pointColor;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public void setCurrentCreditValue(int currentCreditValue) {
        this.currentCreditValue = currentCreditValue;
        startAnimation();
    }

    private void startAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, currentCreditValue);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                currentCreditValue = value;
                invalidate();
            }
        });
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.start();
    }

    /**
     * dp 转 px
     *
     * @param dpValue dp 值
     * @return px 值
     */
    public  int dp2px(final float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
