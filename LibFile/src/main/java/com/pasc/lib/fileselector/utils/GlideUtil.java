package com.pasc.lib.fileselector.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author yangzijian
 * @date 2018/12/3
 * @des
 * @modify
 **/
public class GlideUtil {

    public static void loadImage(Context context, ImageView imageView,String url,int placeRes,int errorRes){
        Glide.with (context).load (url)
                .apply (new RequestOptions ().error (placeRes)
                        .placeholder (errorRes)
                )
                .into (imageView);
    }
}
