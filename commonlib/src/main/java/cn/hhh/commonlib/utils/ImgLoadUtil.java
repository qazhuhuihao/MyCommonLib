package cn.hhh.commonlib.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

/**
 * function : 图片加载工具类.
 * <p></p>
 * Created by hhh on 2017/3/22.
 */

@SuppressWarnings("unused")
public class ImgLoadUtil {
    private static final String TAG = Util.class.getSimpleName();

    public static void load(String path, ImageView view) {
        Glide.with(UIUtil.getContext()).load(path).into(view);
    }

    public static void load(Context context, String path, ImageView view) {
        Glide.with(context).load(path).into(view);
    }

    public static void load(Context context, Integer resourceId, ImageView view) {
        Glide.with(context).load(resourceId).into(view);
    }

    public static void load(Fragment fragment, String path, ImageView view) {
        Glide.with(fragment).load(path).into(view);
    }

    public static void load(Fragment fragment, Integer resourceId, ImageView view) {
        Glide.with(fragment).load(resourceId).into(view);
    }

//    public static void loadGaussianBlur(Context context, String path, ImageView view) {
//        Glide.with(context).load(path).bitmapTransform(new BlurTransformation(context, 35)).skipMemoryCache(true).into(view);
//    }
//
//    public static void loadGaussianBlur(Context context, Integer resourceId, ImageView view) {
//        Glide.with(context).load(resourceId).bitmapTransform(new BlurTransformation(context, 35)).skipMemoryCache(true).into(view);
//    }
//
//    public static void download(Context context, String imageUrl, GlideFileTarget target) {
//        Glide.with(context).load(imageUrl).downloadOnly(target);
//    }
//
//    public static void download(Fragment fragment, String imageUrl, GlideFileTarget target) {
//        Glide.with(fragment).load(imageUrl).downloadOnly(target);
//    }
//
//    public static void downloadAsBitmap(Context context, String imageUrl, GlideBitmapTarget target) {
//        Glide.with(context).load(imageUrl).asBitmap().into(target);
//    }
//
//    public static void downloadAsBitmap(Fragment fragment, String imageUrl, GlideBitmapTarget target) {
//        Glide.with(fragment).load(imageUrl).asBitmap().into(target);
//    }
//
//    public static void clear(final ClearUp clearUp) {
//
//        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
//                Glide.get(UIUtil.getContext()).clearDiskCache();
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                Glide.get(UIUtil.getContext()).clearMemory();
//                if (null != clearUp)
//                    clearUp.OnComplete();
//            }
//        };
//
//        asyncTask.execute();
//
//    }
//
//    public interface ClearUp {
//        void OnComplete();
//    }
//
//    public static abstract class GlideFileTarget implements Target<File> {
//
//        protected Request request;
//
//        @Override
//        public void onLoadStarted(Drawable placeholder) {
//
//        }
//
//        @Override
//        public void onLoadFailed(Exception e, Drawable errorDrawable) {
//
//        }
//
//        @Override
//        public abstract void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation);
//
//
//        @Override
//        public void onLoadCleared(Drawable placeholder) {
//
//        }
//
//        @Override
//        public void getSize(SizeReadyCallback cb) {
//            cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
//        }
//
//        @Override
//        public void setRequest(Request request) {
//            this.request = request;
//        }
//
//        @Override
//        public Request getRequest() {
//            return request;
//        }
//
//        @Override
//        public void onStart() {
//
//        }
//
//        @Override
//        public void onStop() {
//
//        }
//
//        @Override
//        public void onDestroy() {
//
//        }
//    }
//
//    public static abstract class GlideBitmapTarget implements Target<Bitmap> {
//
//        protected Request request;
//
//        @Override
//        public void onLoadStarted(Drawable placeholder) {
//
//        }
//
//        @Override
//        public void onLoadFailed(Exception e, Drawable errorDrawable) {
//
//        }
//
//        @Override
//        public abstract void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation);
//
//
//        @Override
//        public void onLoadCleared(Drawable placeholder) {
//
//        }
//
//        @Override
//        public void getSize(SizeReadyCallback cb) {
//            cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
//        }
//
//        @Override
//        public void setRequest(Request request) {
//            this.request = request;
//        }
//
//        @Override
//        public Request getRequest() {
//            return request;
//        }
//
//        @Override
//        public void onStart() {
//
//        }
//
//        @Override
//        public void onStop() {
//
//        }
//
//        @Override
//        public void onDestroy() {
//
//        }
//    }
}