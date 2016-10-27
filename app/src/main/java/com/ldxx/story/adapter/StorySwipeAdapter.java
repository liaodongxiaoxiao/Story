package com.ldxx.story.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.ldxx.story.R;
import com.ldxx.story.bean.Story;

import java.util.List;

/**
 * Created by wangzhuo-neu
 * on 2016/10/27.
 */

public class StorySwipeAdapter extends RecyclerSwipeAdapter<StorySwipeAdapter.SimpleViewHolder> {


    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView title;
        TextView date;
        Button buttonDelete;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            title = (TextView) itemView.findViewById(R.id.title);
            date = (TextView) itemView.findViewById(R.id.date);
            buttonDelete = (Button) itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(getClass().getSimpleName(), "onItemSelected: " + title.getText().toString());
                    Toast.makeText(view.getContext(), "onItemSelected: " + date.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Context mContext;
    private List<Story> mDataset;

    //protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public StorySwipeAdapter(Context context, List<Story> data) {
        this.mContext = context;
        this.mDataset = data;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        Story item = mDataset.get(position);
        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        viewHolder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        viewHolder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                mDataset.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mDataset.size());
                mItemManger.closeAllItems();
                Toast.makeText(view.getContext(), "Deleted " + viewHolder.title.getText().toString() + "!", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.title.setText(item.getTitle());
        viewHolder.date.setText(item.getDate_time());
        mItemManger.bindView(viewHolder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void addData(List<Story> data) {
        this.mDataset.addAll(data);
        this.notifyDataSetChanged();
    }
}
