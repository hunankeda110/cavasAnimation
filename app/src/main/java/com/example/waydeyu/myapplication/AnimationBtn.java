package com.example.waydeyu.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;


/**
 * Created by waydeyu on 2018/4/16.
 */

@SuppressLint("AppCompatCustomView")
public class AnimationBtn extends Button{

    private Bitmap mAnimationBitmap;

    private GdtAnimationTimmer gdtAnimationTimmer;
    public AnimationBtn(Context context) {
        super(context);
    }

    public AnimationBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public AnimationBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }
    private void init(Context context, AttributeSet attrs){
        mAnimationBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.gdt_animation_cicle)).getBitmap();
        startAnimation();
    }

    public void startAnimation(){
        gdtAnimationTimmer = new RectAnimation(1500, 10, getResources().getColor(R.color.colorPrimary));
        TextAnimation textAnimation  = new TextAnimation(1000, 10);
        WaveAnimation waveAnimation1 = new WaveAnimation(1000, 10, mAnimationBitmap);
        WaveAnimation waveAnimation2 = new WaveAnimation(1000, 10, mAnimationBitmap);
        textAnimation.setVarietyVelocity(0.5f);
        waveAnimation1.setVarietyVelocity(0.5f);
        waveAnimation2.setVarietyVelocity(0.5f);
        gdtAnimationTimmer.setNextAnimation(textAnimation, 320);
        textAnimation.setNextAnimation(waveAnimation1,100);
        waveAnimation1.setNextAnimation(waveAnimation2,100);
        gdtAnimationTimmer.startAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(gdtAnimationTimmer != null &&
                gdtAnimationTimmer.currentAnimation != null) {
            gdtAnimationTimmer.currentAnimation.drawAnimation(canvas);
        }
        super.onDraw(canvas);
    }


   private abstract class  GdtAnimationTimmer extends CountDownTimer{
      protected long animationTotalTime;
      protected long animationIntervalTime;
      protected GdtAnimationTimmer nextAnimation;
      protected GdtAnimationTimmer currentAnimation;
      private  long delayTime;
      float varietyPercent;
      //速度因子默认为1
      protected float varietyVelocity = 1;


      public GdtAnimationTimmer(long animationTotalTime, long animationIntervalTime) {
          super(animationTotalTime, animationIntervalTime);
          this.animationTotalTime = animationTotalTime;
          this.animationIntervalTime = animationIntervalTime;

      }
      @Override
      public void onTick(long remaindTime) {
           varietyPercent = varietyVelocity * (animationTotalTime - remaindTime)/animationTotalTime;
           onAnimationInterval(varietyPercent);
           postInvalidate();
      }

      public void setVarietyVelocity(float varietyVelocity) {
          this.varietyVelocity = varietyVelocity;
      }

      @Override
      public void onFinish() {
          onAnimationEnd();
            if(nextAnimation != null){
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentAnimation = nextAnimation;
                        nextAnimation.startAnimation();
                        onNextAnimation();
                    }
                }, delayTime);
            }else{
               onAnimationSetEnd();
            }
      }
      public void setNextAnimation(GdtAnimationTimmer gdtAnimationTimmer, long delayTime){
          this.nextAnimation = gdtAnimationTimmer;
          this.delayTime = delayTime;

      }
      public void startAnimation(){
          start();
          currentAnimation = this;
          onAnimationStart();
      }
      protected  void onNextAnimation(){}
      protected  void onAnimationEnd(){}
      protected abstract void onAnimationInterval(float varietyPercent);
      public  abstract void  drawAnimation(Canvas canvas);
      protected  void onAnimationSetEnd(){}
      protected void onAnimationStart(){}
  }

  class AlphaAnimation extends GdtAnimationTimmer{
      Paint paint;
      Rect rect;
      public AlphaAnimation(long animationTotalTime, long animationIntervalTime, int color) {
          super(animationTotalTime, animationIntervalTime);
          this.paint = new Paint();
          paint.setColor(color);
          rect = new Rect(0,0, getWidth(), getHeight());

      }

      @Override
      protected void onNextAnimation() {
          nextAnimation.start();
      }

      @Override
      protected void onAnimationInterval(float varietyPercent) {
         paint.setAlpha((int)(256*varietyPercent));
      }

      @Override
      public void drawAnimation(Canvas canvas) {

         canvas.drawPaint(paint);
          Log.d("AnimationBtn", "currentAnimation = " + this.getClass().getSimpleName() +"percent =" +varietyPercent);
      }

  }
  class RectAnimation extends GdtAnimationTimmer{
      Paint paint;
      Rect rect;
      int rectColor;
      public RectAnimation(long animationTotalTime, long animationIntervalTime, int color){
          super(animationTotalTime, animationIntervalTime);
          paint = new Paint();
          rectColor = color;
          paint.setColor(rectColor);
      }

      @Override
      protected void onAnimationStart() {
          super.onAnimationStart();
          setBackgroundColor(getResources().getColor(R.color.colorAccent));
      }

      @Override
      protected void onAnimationInterval(float varietyPercent) {
            rect = new Rect(0,0, (int)(getWidth()*varietyPercent), getHeight());
      }

      @Override
      public void drawAnimation(Canvas canvas) {
           canvas.drawRect(rect, paint);
           Log.d("AnimationBtn", "currentAnimation = " + this.getClass().getSimpleName() +"percent =" +varietyPercent);


      }

      @Override
      protected void onAnimationEnd() {
          super.onAnimationEnd();
          setBackgroundColor(getResources().getColor(R.color.colorPrimary));
          gdtAnimationTimmer = gdtAnimationTimmer.nextAnimation;
      }
  }
  class WaveAnimation extends GdtAnimationTimmer{
      Bitmap waveBitmap;
      Matrix matrix;
      Paint paint;
      public WaveAnimation(long animationTotalTime, long animationIntervalTime, Bitmap bitmap){
          super(animationTotalTime, animationIntervalTime);
          this.waveBitmap = bitmap;
          matrix = new Matrix();
          paint = new Paint();
      }
      @Override
      protected void onAnimationInterval(float varietyPercent) {
          matrix.setTranslate(getWidth()/2f - waveBitmap.getWidth()/2f * varietyPercent , getHeight()/2f - waveBitmap.getHeight()/2f * varietyPercent);
          matrix.preScale(varietyPercent, varietyPercent);
          paint.setAlpha((int)((1-varietyPercent/varietyVelocity)*256));
      }
      @Override
      public void drawAnimation(Canvas canvas) {

          canvas.drawBitmap(waveBitmap, matrix, paint);
          Log.d("AnimationBtn", "currentAnimation = " + this.getClass().getSimpleName() +"percent =" +varietyPercent);
      }
      @Override
      protected void onAnimationEnd() {
          super.onAnimationEnd();
          gdtAnimationTimmer = gdtAnimationTimmer.nextAnimation;
      }
      @Override
      protected void onAnimationSetEnd() {

      }
  }
  class TextAnimation extends GdtAnimationTimmer{
        Paint paint;
        private float textSize;
      public TextAnimation(long animationTotalTime, long animationIntervalTime){
          super(animationTotalTime, animationIntervalTime);
          paint = new Paint();
          paint.setColor(getResources().getColor(R.color.colorAccent));
          textSize = getTextSize();
      }

      @Override
      protected void onAnimationInterval(float varietyPercent) {
          setTextSize(textSize * varietyPercent);
          Log.d("AnimationBtn", "currentAnimation = " + this.getClass().getSimpleName() +"percent =" +varietyPercent);

      }

      @Override
      public void drawAnimation(Canvas canvas) {

      }
      @Override
      protected void onAnimationEnd() {
          super.onAnimationEnd();
          gdtAnimationTimmer = gdtAnimationTimmer.nextAnimation;
      }
  }
}
