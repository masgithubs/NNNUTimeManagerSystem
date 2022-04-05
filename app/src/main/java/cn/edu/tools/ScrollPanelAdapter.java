package cn.edu.tools;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kelin.scrollablepanel.library.PanelAdapter;

import java.util.List;

import cn.edu.nnnu.R;

public class ScrollPanelAdapter extends PanelAdapter {
       private List<List<String>> lists;
       private Context context;

    public ScrollPanelAdapter(Context context) {
        this.lists = lists;
        this.context = context;
    }

    public void setLists(List<List<String>> lists) {
        this.lists = null;
        this.lists = lists;
    }

    @Override
    public int getRowCount() {
        return lists.size();
    }

    @Override
    public int getColumnCount() {
        return lists.get(0).size();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, int i1) {
        Holder holder = (Holder) viewHolder;
        holder.textView.setText(lists.get(i).get(i1));

        if (lists.get(i).get(i1).equals("")){
            holder.layout.setOnClickListener(null);
            holder.layout.setVisibility(View.INVISIBLE);
            holder.layout.setBackgroundColor(Color.TRANSPARENT);
            holder.cardView.setCardElevation(0);
            holder.layout.getLayoutParams().width = context.getResources().getDimensionPixelOffset(R.dimen.item_width);
            holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
        }else {
            holder.layout.setVisibility(View.VISIBLE);
        }

        if (lists.get(i).get(i1).contains("：")){
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("课程提示")
                            .setMessage(lists.get(i).get(i1))
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    alertDialog.show();
                }
            });
            holder.cardView.setCardElevation(15);
            holder.cardView.setCardBackgroundColor(context.getColor(R.color.purple_200));
            holder.layout.getLayoutParams().width = context.getResources().getDimensionPixelOffset(R.dimen.item_width);
        }
        if (lists.get(i).get(i1).contains("星期")){
            holder.layout.getLayoutParams().width = context.getResources().getDimensionPixelOffset(R.dimen.item_width);
        }

        if (lists.get(i).get(i1).contains("：1")){
            holder.layout.setBackgroundResource(R.drawable.item_bg_1);
        }
        if (lists.get(i).get(i1).contains("：2")){
            holder.layout.setBackgroundResource(R.drawable.item_bg_2);
        }
        if (lists.get(i).get(i1).contains("：3")){
            holder.layout.setBackgroundResource(R.drawable.item_bg_3);
        }
        if (lists.get(i).get(i1).contains("：4")){
            holder.layout.setBackgroundResource(R.drawable.item_bg_4);
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,null);
        Holder holder = new Holder(view);
        return holder;
    }
    class Holder extends RecyclerView.ViewHolder{
        public TextView textView;
        private CardView cardView;
        private LinearLayout layout;
        public Holder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_card);
            layout = itemView.findViewById(R.id.item_bg);
            textView = itemView.findViewById(R.id.item_text);
        }
    }
}
