package com.allen.indexablelist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import com.allen.indexablelist.adapter.ContactAdapter;
import com.allen.indexablelist.domain.ContactDomain;
import com.allen.indexablelist.utils.Pinyin4jUtil;

/**
 * @package：com.allen.indexablelist
 * @author：Allen
 * @email：jaylong1302@163.com
 * @data：2013-4-13 下午1:51:17
 * @description：带实体的数据源
 */
public class ContactActivity extends Activity {
    private LinearLayout mLlRightIndex;//右边a-z索引的布局
    private ListView mLvContent;//联系人容器
    private String[] str = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "U", "V", "W", "X", "Y", "Z"};//字母索引值
    private int indexItemHeight;// a-z布局中条目的高度，即A所占的高度
    private int rightCurrentIndex = -1, rightPreviousIndex =-1;
    private List<ContactDomain> mContacts;//联系人数据
    private ContactAdapter mContactAdapter;
    private TextView mTvCenterLetter;// 中间显示的文本,如屏幕中间显示的“A”

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contact);
        initViews();
    }

    private void initViews() {
        mLlRightIndex = (LinearLayout) this.findViewById(R.id.ll_right_letters);
        mLvContent = (ListView) findViewById(R.id.lv_content);
        mTvCenterLetter = (TextView) findViewById(R.id.tv_show_letter);
        initData();
    }

    private void initData() {
        mLlRightIndex.setBackgroundColor(Color.parseColor("#00ffffff"));
        mTvCenterLetter.setVisibility(View.INVISIBLE);

        //创建数据
        createData();
        //按首字母对数据排序
        Collections.sort(mContacts, new Comparator<ContactDomain>() {
            @Override
            public int compare(ContactDomain t0, ContactDomain t1) {
                return t0.index.charAt(0) < t1.index.charAt(0) ? -1 : 1;
            }
        });
        mContactAdapter = new ContactAdapter(this, mContacts, this.str);
        mLvContent.setAdapter(mContactAdapter);
    }

    /**
     * 控件加载完毕后，取得他们的状态
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // 在onCreate里面执行下面的代码没反应，因为onCreate里面得到的getHeight=0
        System.out.println("mLlRightIndex.getHeight()=" + mLlRightIndex.getHeight());
        indexItemHeight = mLlRightIndex.getHeight() / str.length;
        initIndexView();
        //必须放在initIndexView();之后，否则没有初始化索引就没法选中默人字符
        mLvContent.setOnScrollListener(new AbsListView.OnScrollListener() {
            String previousIndex= null,firstIndex= null;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstIndex = mContacts.get(firstVisibleItem).index;
                if(TextUtils.isEmpty(previousIndex)&&TextUtils.isEmpty(firstIndex)){//之前的所以和现在的索引
                    previousIndex = firstIndex;
                }else if(!TextUtils.equals(previousIndex,firstIndex)){
                    if(-1 != mContactAdapter.getPosition(previousIndex)) {//之前右边选中的索引字母
                        rightPreviousIndex = mContactAdapter.getPosition(previousIndex);
                        mLlRightIndex.getChildAt(rightPreviousIndex).setSelected(false);
                    }
                    if(-1 != mContactAdapter.getPosition(firstIndex)) {//当前右边选中的索引字母
                        rightCurrentIndex = mContactAdapter.getPosition(firstIndex);
                        mLlRightIndex.getChildAt(rightCurrentIndex).setSelected(true);
                    }
                    previousIndex = firstIndex;
                }
            }
        });
    }

    /**
     * 绘制索引列表
     */
    private void initIndexView() {
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, indexItemHeight);
        ColorStateList colorStateList = getResources().getColorStateList(R.color.right_index_selector);
        for (int i = 0; i < str.length; i++) {
            final TextView tv = new TextView(this);
            tv.setLayoutParams(params);
            tv.setText(str[i]);
            tv.setTextColor(colorStateList);
            tv.setPadding(10, 0, 10, 0);
            mLlRightIndex.addView(tv);
            //右边索引栏的滑动
            mLlRightIndex.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float y = event.getY();
                    int index = (int) (y / indexItemHeight);
                    rightPreviousIndex = index;
                    if (index > -1 && index < str.length) {// 防止越界
                        String key = str[index];
                        if (mContactAdapter.getSelector().containsKey(key)) {
                            int pos = mContactAdapter.getSelector().get(key);
                            if (mLvContent.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。
                                mLvContent.setSelectionFromTop(pos + mLvContent.getHeaderViewsCount(), 0);
                            } else {
                                mLvContent.setSelectionFromTop(pos, 0);// 滑动到第一项
                            }
                            mTvCenterLetter.setVisibility(View.VISIBLE);
                            mTvCenterLetter.setText(str[index]);
                            //让选中的右边索引字母变色，之前选中的恢复为默认色
                            if(-1 != rightCurrentIndex){
                                mLlRightIndex.getChildAt(rightCurrentIndex).setSelected(false);
                            }
                            rightCurrentIndex = rightPreviousIndex;
                            mLlRightIndex.getChildAt(index).setSelected(true);
                        }
                    }
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN://按下
                            mLlRightIndex.setBackgroundColor(getResources().getColor(R.color.color_66d7d7d7));
                            break;
                        case MotionEvent.ACTION_MOVE://移动
                            break;
                        case MotionEvent.ACTION_UP://抬起
                            mLlRightIndex.setBackgroundColor(getResources().getColor(R.color.color_00d7d7d7));
                            mTvCenterLetter.setVisibility(View.INVISIBLE);
                            break;
                    }
                    return true;
                }
            });
        }
    }
    private void createData() {
        mContacts = new ArrayList<ContactDomain>();

        ContactDomain n1 = new ContactDomain();
        n1.call = "经理";
        n1.name = "allen";
        n1.mobile = "18217594856";
        n1.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n1.name).charAt(0));
        mContacts.add(n1);

        ContactDomain n11 = new ContactDomain();
        n11.call = "经理";
        n11.name = "allen";
        n11.mobile = "18217594856";
        n11.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n11.name).charAt(0));
        mContacts.add(n11);

        ContactDomain n2 = new ContactDomain();
        n2.call = "工程师";
        n2.name = "android";
        n2.mobile = "13658974521";
        n2.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n2.name).charAt(0));
        mContacts.add(n2);

        ContactDomain n21 = new ContactDomain();
        n21.call = "工程师";
        n21.name = "android";
        n21.mobile = "13658974521";
        n21.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n21.name).charAt(0));
        mContacts.add(n21);

        ContactDomain n3 = new ContactDomain();
        n3.call = "经理";
        n3.name = "周俊";
        n3.mobile = "13658974521";
        n3.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n3.name).charAt(0));
        mContacts.add(n3);

        ContactDomain n31 = new ContactDomain();
        n31.call = "经理";
        n31.name = "周俊";
        n31.mobile = "13658974521";
        n31.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n31.name).charAt(0));
        mContacts.add(n31);

        ContactDomain n41 = new ContactDomain();
        n41.call = "教师";
        n41.name = "王强";
        n41.number = "021-25635784";
        n41.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n41.name).charAt(0));
        mContacts.add(n41);

        ContactDomain n4 = new ContactDomain();
        n4.call = "教师";
        n4.name = "王强";
        n4.number = "021-25635784";
        n4.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n4.name).charAt(0));
        mContacts.add(n4);

        ContactDomain n5 = new ContactDomain();
        n5.call = "客服";
        n5.name = "刘敏";
        n5.number = "010-25635784";
        n5.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n5.name).charAt(0));
        mContacts.add(n5);

        ContactDomain n6 = new ContactDomain();
        n6.call = "客服";
        n6.name = "bruth";
        n6.number = "010-25635784";
        n6.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n6.name).charAt(0));
        mContacts.add(n6);

        ContactDomain n7 = new ContactDomain();
        n7.call = "经理";
        n7.name = "陈文明";
        n7.number = "010-25635784";
        n7.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n7.name).charAt(0));
        mContacts.add(n7);

        ContactDomain n8 = new ContactDomain();
        n8.call = "客服";
        n8.name = "mary";
        n8.number = "010-25635784";
        n8.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n8.name).charAt(0));
        mContacts.add(n8);

        ContactDomain n9 = new ContactDomain();
        n9.call = "客服";
        n9.name = "李勇";
        n9.number = "010-25635784";
        n9.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n9.name).charAt(0));
        mContacts.add(n9);

        ContactDomain n10 = new ContactDomain();
        n10.call = "客服";
        n10.name = "娜娜";
        n10.number = "010-25635784";
        n10.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n10.name).charAt(0));
        mContacts.add(n10);

        ContactDomain n111 = new ContactDomain();
        n111.call = "客服";
        n111.name = "筱筱";
        n111.number = "010-25635784";
        n111.index = String.valueOf(Pinyin4jUtil.getHanyuPinyin(n111.name).charAt(0));
        mContacts.add(n111);
    }
}
