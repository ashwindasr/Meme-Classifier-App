package com.app.docs.memedeleter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

class GridViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<String> images;
    private MainActivity context;

    public GridViewAdapter(ArrayList<String> images, MainActivity context) {
        this.images = images;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.griditem, null);

            convertView.setLayoutParams(new GridView.LayoutParams(250, 250));

            holder.imageButton = (ImageButton) convertView.findViewById(R.id.img_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Bitmap bitmap = BitmapFactory.decodeFile(images.get(position));
        holder.imageButton.setImageBitmap(bitmap);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.itemClicked(position);
            }
        });
        return convertView;
    }
}

