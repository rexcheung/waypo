package zxb.zweibo.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zxb.zweibo.Utils.EmotionUtil;
import zxb.zweibo.ui.PersonalDetailActivity;

/**
 * Created by rex on 16-1-8.
 */
public class WeiboText extends TextView {

    private Context mContext;
    private EmotionUtil mEmoUtil;
    private final String LINK = " 网页链接 ";

    public WeiboText(Context context) {
        this(context, null);
    }

    public WeiboText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeiboText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mEmoUtil = new EmotionUtil(context);
    }

    public void setSpanText(String text){
        List<String> links = new ArrayList<>();
        //先把链接的文本做替换
        String spanString = replaceHttp(text, links);

        //再根据替换后的文本生成SpannableString
        SpannableString spanText = new SpannableString(spanString);
        person(spanText);
        topic(spanText);
        http(spanText, links);
        emotion(spanText);
        super.setText(spanText);
    }

    /**
     * 把微博的表情文本替换成为表情图像.
     * 因为的SpannableString的特性，设置进去的图像需要为本地图像，
     * 如果为网络图像的话，第一次会载入不成功的，所以有部分表情是不能显示.
     *
     * @param spanText SpannableString
     */
    private void emotion(SpannableString spanText) {
        Pattern pattern = Pattern.compile("\\[\\S{1,2}\\]");
        Matcher matcher = pattern.matcher(spanText);

        while (matcher.find()){
            int start = matcher.start();
            String group = matcher.group();

//            byte[] emotion = mEmoUtil.getEmotion(group);
            byte[] emotion = EmotionUtil.getEmotion(group);
            if (emotion != null){
                ByteArrayInputStream is = new ByteArrayInputStream(emotion);
                Drawable d = Drawable.createFromStream(is, group);
                d.setBounds(0, 0, 50, 50);
                spanText.setSpan(new ImageSpan(d), start, start + group.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
    }

    private String replaceHttp(String text, List<String> links){
        String result = text;
        Pattern pattern = Pattern.compile("http://t.cn/[A-z0-9]{7}");
        Matcher matcher = pattern.matcher(result.toString());

        while (matcher.find()) {
            links.add(matcher.group());
            result = result.replace(matcher.group(), LINK);
        }
        return result;
    }

    private void http(SpannableString spanText, List<String> links) {
        Pattern pattern = Pattern.compile(LINK);
        Matcher matcher = pattern.matcher(spanText.toString());

        int index=0;
        while (matcher.find()) {
            setHttpStyle(spanText, matcher);
            setHttpClick(spanText, matcher, links.get(index));
            index++;
        }
    }

    private void setHttpStyle(SpannableString spanText, Matcher matcher) {
        spanText.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(),
                matcher.start() + matcher.group().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spanText.setSpan(new BackgroundColorSpan(Color.CYAN), matcher.start(),
                matcher.start() + matcher.group().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private void setHttpClick(SpannableString spanText, Matcher matcher, String s) {
        spanText.setSpan(new ClickHttp(), matcher.start(),
                matcher.start() + matcher.group().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    class ClickHttp extends ClickableSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            Toast.makeText(mContext, LINK, Toast.LENGTH_SHORT).show();
        }
    }

    private void topic(SpannableString spanText) {
        Pattern pattern = Pattern.compile("#\\S*#");
        Matcher matcher = pattern.matcher(spanText.toString());

        while (matcher.find()) {
            setTopicColor(spanText, matcher);
            setTopicOnClick(spanText, matcher);
        }
    }

    private void setTopicColor(SpannableString spanText, Matcher matcher) {
        spanText.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(),
                matcher.start() + matcher.group().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    private void setTopicOnClick(SpannableString spanText, Matcher matcher) {
        spanText.setSpan(new ClickTopic(), matcher.start(),
                matcher.start() + matcher.group().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    class ClickTopic extends ClickableSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            Toast.makeText(mContext, "Topic", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 处理SpannableString里面包含人名的字符串.
     *
     * @param spanText SpannableString
     */
    private void person(SpannableString spanText) {
        Pattern pattern = Pattern.compile("@[\\S]*");
        Matcher matcher = pattern.matcher(spanText.toString());

        while (matcher.find()) {
            setPersonColor(spanText, matcher);
            setPersonClick(spanText, matcher);
        }
    }

    /**
     * 改变颜色.
     *
     * @param span    SpannableString
     * @param matcher 匹配器
     */
    private void setPersonColor(SpannableString span, Matcher matcher) {
        String name = matcher.group();
        String[] split = name.split(":");
        span.setSpan(new ForegroundColorSpan(Color.BLUE), matcher.start(),
                matcher.start() + split[0].length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    /**
     * 为人名设置点击事件.
     *
     * @param span    SpannableString
     * @param matcher 匹配器
     */
    private void setPersonClick(SpannableString span, Matcher matcher) {
        String name = matcher.group();
        String[] split = name.split(":");
        span.setSpan(new ClickPerson(), matcher.start(),
                matcher.start() + split[0].length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

    class ClickPerson extends ClickableSpan {
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            Toast.makeText(mContext, "People", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, PersonalDetailActivity.class);
            mContext.startActivity(intent);
        }
    }
}
