package com.example.healthconsultapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

public class AvatarAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> avatarList;           // Danh sách ID drawable
    private LayoutInflater inflater;

    public AvatarAdapter(Context context, List<Integer> avatarList) {
        this.context = context;
        this.avatarList = avatarList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return avatarList.size();
    }

    @Override
    public Object getItem(int position) {
        return avatarList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate layout item
            convertView = inflater.inflate(R.layout.item_avatar, parent, false);
            holder = new ViewHolder();
            holder.imgAvatar = convertView.findViewById(R.id.imgAvatarItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Gán ảnh avatar
        int resId = avatarList.get(position);
        holder.imgAvatar.setImageResource(resId);

        return convertView;
    }

    // ViewHolder để tối ưu hiệu suất
    static class ViewHolder {
        ImageView imgAvatar;
    }
}