package zxb.zweibo.Utils;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * 动画工具类。
 * Created by Rex.Zhang on 2016/2/26.
 */
public class AnimationUtil {

	private AnimationUtil(){}

	private static final String ALPHA = "alpha";
	private static final String ROTATION = "rotation";
	private static final String TRANSLATION_X = "translationX";
	private static final String TRANSLATION_Y = "translationY";
	private static final String SCALE_X = "scaleX";
	private static final String SCALE_Y = "scaleY";

	/**
	 * 透明度动画。
	 * @param view 作用于哪个View
	 * @param duration 持续时长
	 * @return
	 */
	public static ObjectAnimator alpha(View view, int duration){
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
		animator.setDuration(duration);
		return animator;
	}

	public static ObjectAnimator tranY(View view, int duration, float pos){
		float currentY = view.getTranslationY();
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, TRANSLATION_Y, pos, currentY);
		return animator.setDuration(duration);
	}

	public static ObjectAnimator tranX(View view, int duration, float pos){
		float currentY = view.getTranslationY();
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, TRANSLATION_Y, pos, currentY);
		return animator.setDuration(duration);
	}

	public static ObjectAnimator rotation(View view, int duration){
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
		animator.setDuration(duration);
		return animator;
	}
	public static ObjectAnimator scaleX(View view, int duration){
		ObjectAnimator animator = ObjectAnimator.ofFloat(view, SCALE_X, 1f, 2f, 1f);
		animator.setDuration(duration);
		return animator;
	}
}
