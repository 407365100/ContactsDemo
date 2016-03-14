package com.allen.indexablelist.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.allen.indexablelist.R;
import com.allen.indexablelist.domain.ContactDomain;



public class ContactAdapter extends BaseAdapter {
    private Context context;
    private List<ContactDomain> list;
    private Map<String, Integer> selector;//键值是索引表的字母，值为对应在listview中的位置
    //字母表
    private String sts[];
    private Map<String, Integer> charSelector;//有序的字母表

    public ContactAdapter(Context context, List<ContactDomain> list, String[] sts) {
        this.context = context;
        this.list = list;
        this.sts = sts;
        initData(list, sts);
    }

    private void initData(List<ContactDomain> list, String[] sts) {
        selector = new HashMap<String, Integer>();
        charSelector = new HashMap<String, Integer>();
        for (int j = 0; j < sts.length; j++) {// 循环字母表，找出list中对应字母的位置
            charSelector.put(sts[j], j);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).index.equals(sts[j].toLowerCase())) {
                    selector.put(sts[j], i);
                    break;
                }
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        try {
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_contact_item, null);
                vh.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                vh.tv2 = (TextView) convertView.findViewById(R.id.tv2);
                vh.tv3 = (TextView) convertView.findViewById(R.id.tv3);
                vh.layout = convertView.findViewById(R.id.layout);
                vh.index = (TextView) convertView.findViewById(R.id.tv_index);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            // 绑定数据
            ContactDomain contact = list.get(position);
            vh.tv1.setText(contact.name);
            vh.tv2.setText(contact.number.equals("null") ? contact.mobile : contact.number);
            vh.tv3.setText(contact.call);
            // 上一项的index
            String previewStr = (position - 1) >= 0 ? list.get(position - 1).index : " ";
            //判断是否上一次的存在
            if (!previewStr.equals(contact.index)) {
                vh.index.setVisibility(View.VISIBLE);
                vh.index.setText(contact.index);//中间提示的文本显示当前滑动的字母
            } else {
                vh.index.setVisibility(View.GONE);
            }
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        View layout;
        /**
         * 索引字母
         */
        TextView index;
    }

    /**
     * 获取右边的字母对应的索引号
     *
     * @param st
     * @return
     */
    public int getPosition(String st) {
        if (TextUtils.isEmpty(st)) {
            return -1;
        }
        return charSelector.containsKey(st.toUpperCase()) ? charSelector.get(st.toUpperCase()) : -1;
    }

    public Map<String, Integer> getSelector() {
        return selector;
    }

    public void setSelector(Map<String, Integer> selector) {
        this.selector = selector;
    }

    public String[] getSts() {
        return sts;
    }

    public void setSts(String[] sts) {
        this.sts = sts;
    }
}
