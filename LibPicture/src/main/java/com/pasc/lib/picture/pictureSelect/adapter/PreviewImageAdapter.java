package com.pasc.lib.picture.pictureSelect.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.lib.picture.R;
import com.pasc.lib.picture.pictureSelect.LocalPicture;
import com.pasc.lib.picture.util.GlideUtil;
import java.util.List;

/**
 *
 */
public class PreviewImageAdapter extends BaseQuickAdapter<LocalPicture, BaseViewHolder> {
  private int selectMax = 4;
  private int selectedItem = 0;

  public PreviewImageAdapter(@Nullable
      List<LocalPicture> data) {
    super(R.layout.picture_item_preview_image, data);
  }

  public void setSelectMax(int selectMax) {
    this.selectMax = selectMax;
  }

  public void setSelectedItem(int selectedItem) {
    this.selectedItem = selectedItem;
    notifyDataSetChanged();
  }

  @Override
  protected void convert(BaseViewHolder helper, LocalPicture item) {

    GlideUtil.loadImage(mContext, (ImageView) helper.getView(R.id.img_opinion), item.getPath(),
        R.drawable.picture_bg_default_image_color, R.drawable.picture_bg_default_image_color);

    helper.addOnClickListener(R.id.img_remove).addOnClickListener(R.id.img_opinion);
    if (selectedItem == helper.getPosition()) {
      helper.getView(R.id.view_border).setBackgroundResource(R.color.theme_color);
    } else {
      helper.getView(R.id.view_border).setBackgroundResource(R.color.transparent);
    }
  }
}
