package com.liql.photograph;

import android.content.Context;
import android.net.Uri;

/**
 *
 * @author hhh
 * @date 2017/11/18
 */
@SuppressWarnings("unused")
public class FileAddressGet {

    public static String getPath(final Context context, final Uri uri) {
        return GalleryAddressTool.getPath(context, uri);
    }
}
