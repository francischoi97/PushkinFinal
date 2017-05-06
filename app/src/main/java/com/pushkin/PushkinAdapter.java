package com.pushkin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pushkin.other.CircleTransform;
import com.pushkin.other.ListViewClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harith on 04/28/17.
 */

public class PushkinAdapter extends ArrayAdapter<ConversationPreview> {

    private ArrayList<ConversationPreview> previews;

    public PushkinAdapter(Context context, int textViewResourceId, ArrayList<ConversationPreview> previews) {
        super(context, textViewResourceId, previews);
        this.previews = previews;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {

        ConversationPreview item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.conversation_preview, parent, false);
        }

        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout);
        layout.setOnClickListener(new ListViewClickListener(item.getChatID()));

        ImageView pic = (ImageView) convertView.findViewById(R.id.pic);

        Glide.with(getContext()).load(item.getImageURL())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(300,200)
                .into(pic);

        TextView message = (TextView) convertView.findViewById(R.id.message);
        message.setText(item.getName());

        TextView lastActive = (TextView) convertView.findViewById(R.id.lastActive);
        lastActive.setText(item.getLastActive());

        TextView tpoints = (TextView) convertView.findViewById(R.id.tpoints);
        tpoints.setText("Total points: " + item.getPoints());

        return convertView;
    }
}
