package com.example.notes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoticeViewHolder extends RecyclerView.ViewHolder {

   public TextView title, text,date;
   public RelativeLayout viewBackground,viewForeground;

   public ImageView delete_icon,img_favourite;

    public NoticeViewHolder(@NonNull View itemView) {
        super(itemView);
        title=itemView.findViewById(R.id.list_title);
        text=itemView.findViewById(R.id.list_text);
        date=itemView.findViewById(R.id.txt_date);

        delete_icon=itemView.findViewById(R.id.delete_icon);
        img_favourite=itemView.findViewById(R.id.img_star_favourite);

        viewBackground=itemView.findViewById(R.id.view_background);
        viewForeground=itemView.findViewById(R.id.view_foreground);
    }
}
