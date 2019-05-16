package com.aniapps.flicbuzzapp.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.aniapps.flicbuzzapp.R;
import com.aniapps.flicbuzzapp.models.MyVideos;

import com.squareup.picasso.Picasso;

import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int VIEW_TYPE_EMPTY = 0;
    public static final int VIEW_TYPE_NORMAL = 1;

    private List<MyVideos> mInfoList;

    public VideoRecyclerViewAdapter(List<MyVideos> infoList) {
        mInfoList = infoList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.autoplayer_item, parent, false));
            case VIEW_TYPE_EMPTY:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_view, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }


    @Override
    public int getItemViewType(int position) {
        if (mInfoList != null && mInfoList.size() > 0) {
            return VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_EMPTY;
        }
    }

    @Override
    public int getItemCount() {
        if (mInfoList != null && mInfoList.size() > 0) {
            return mInfoList.size();
        } else {
            return 1;
        }
    }


    public void onRelease() {
        if (mInfoList != null) {
            mInfoList.clear();
            mInfoList = null;
        }
    }

    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.tvTitle)
        TextView textViewTitle;
        @BindView(R.id.tvDesc)
        TextView userHandle;
        @BindView(R.id.video_layout)
        public FrameLayout videoLayout;
        @BindView(R.id.cover)
        public ImageView mCover;

        @BindView(R.id.progressBar)
        public ProgressBar mProgressBar;
        public final View parent;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            parent = itemView;
        }

        protected void clear() {

        }

        public void onBind(int position) {
            super.onBind(position);
            parent.setTag(this);
            MyVideos videoInfo = mInfoList.get(position);
            textViewTitle.setText(videoInfo.getHeadline());
            userHandle.setText(videoInfo.getDescription());
            Picasso.with(itemView.getContext())
                    .load(videoInfo.getThumb())
                    .error(R.mipmap.launcher_icon)
                    .into(mCover);

        }
    }

    public class EmptyViewHolder extends BaseViewHolder {

        @BindView(R.id.btn_retry)
        Button retryButton;

        @BindView(R.id.tv_message)
        TextView messageTextView;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setVisibility(View.GONE);
        }

        @Override
        protected void clear() {

        }

        @OnClick(R.id.btn_retry)
        void onRetryClick() {

        }
    }
}
