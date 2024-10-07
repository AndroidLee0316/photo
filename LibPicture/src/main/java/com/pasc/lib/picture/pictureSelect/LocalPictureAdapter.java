package com.pasc.lib.picture.pictureSelect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.lib.picture.R;
import com.pasc.lib.picture.util.DensityUtils;
import com.pasc.lib.picture.util.GlideUtil;

import java.util.List;


public class LocalPictureAdapter extends RecyclerView.Adapter<LocalPictureAdapter.PictureHolder> {

    public interface ItemClick {
        public void click(View view, int position);
    }

    private int canSelect;
    private int hasSelected = 0;
    private List<LocalPicture> list;
    private ItemClick itemClick;
    private int spanCount = 0;
    private int displayWidt = 1080;
    private int itemW, itemH;
    private List<LocalPicture> data;
    Context context;

    public LocalPictureAdapter(Context context, int spanCount, List<LocalPicture> data, int canSelect, List<LocalPicture> list) {
        this.canSelect = canSelect;
        this.list = list;
        this.spanCount = spanCount;
        this.context = context;
        this.data = data;
        displayWidt = DensityUtils.getScreenWidth (context);
        if (spanCount > 0)
            itemW = itemH = displayWidt / spanCount;
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public void notifyDataChanged(int hasSelected, List<LocalPicture> list) {
        this.hasSelected = hasSelected;
        this.list = list;
        notifyDataSetChanged ();
    }

    private String getSelectPosition(LocalPicture picture) {

        for (int i = 0; i < list.size (); i++) {
            LocalPicture localPicture = list.get (i);
            if (localPicture.equals (picture)) {
                return String.valueOf (i + 1);
            }
        }
        return "";
    }

    @NonNull
    @Override
    public PictureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from (context).inflate (R.layout.picture_item_local_picture, parent, false);
        return new PictureHolder (rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PictureHolder holder, final int position) {
        LocalPicture item = data.get (position);
        GlideUtil.loadImage (context, holder.imgLocal,
                item.path,
                R.drawable.picture_bg_default_image_color, R.drawable.picture_bg_default_image_color);
        holder.tvPosition.setSelected (item.isSelect ());
        holder.tvPosition.setText (getSelectPosition (item));
        if (hasSelected >= canSelect) {
            if (item.isSelect ()) {
                holder.itemView.setAlpha (1);
            } else {
                holder.itemView.setAlpha (0.4f);
            }
        } else {
            holder.itemView.setAlpha (1);
        }
        holder.imgLocal.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (itemClick != null) {
                    itemClick.click (holder.imgLocal, position);
                }
            }
        });
        holder.flIcon.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (itemClick != null) {
                    itemClick.click (holder.flIcon, position);
                }
            }
        });
        if (spanCount > 0) {
            ViewGroup.LayoutParams layoutParams = holder.flPicture.getLayoutParams ();
            layoutParams.width = itemW;
            layoutParams.height = itemH;
            holder.flPicture.setLayoutParams (layoutParams);
        }

    }

    @Override
    public int getItemCount() {
        return data.size ();
    }

    public static class PictureHolder extends RecyclerView.ViewHolder {
        public PictureHolder(View itemView) {
            super (itemView);
            assignViews (itemView);
        }

        private FrameLayout flPicture;
        private ImageView imgLocal;
        private FrameLayout flIcon;
        private TextView tvPosition;

        private void assignViews(View itemView) {
            flPicture = (FrameLayout) itemView.findViewById (R.id.fl_picture);
            imgLocal = (ImageView) itemView.findViewById (R.id.img_local);
            flIcon = (FrameLayout) itemView.findViewById (R.id.fl_icon);
            tvPosition = (TextView) itemView.findViewById (R.id.tv_position);
        }

    }
}
