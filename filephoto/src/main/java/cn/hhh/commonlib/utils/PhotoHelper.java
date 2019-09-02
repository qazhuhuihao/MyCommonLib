package cn.hhh.commonlib.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.liql.photograph.PhotographStaticUtils;
import com.liql.photograph.interfa.OnDisposeOuterListener;
import com.liql.photograph.interfa.OnPhotographGetDataListener;

import java.io.File;

/**
 * 拍照 图库 压缩 等操作
 *
 * @author hhh
 * @date 2017/5/18
 */
@SuppressWarnings("unused")
public class PhotoHelper {
    private static final String COMPRESS_PATH = FileStorageUtil.getPictureDirPath();
    private static final String IMAGE_PATH = FileStorageUtil.getPictureCacheDirPath();
    private static final long IMAGE_SIZE = 1024 * 1024;
    /**
     * 图库和照相机处理操作对象暴露接口
     */
    private OnDisposeOuterListener mOnDisposeOuterListener;
    private Dialog mDialog;
    private Activity mActivity;
    private OnPhotographGet onPhotographGet;

    public PhotoHelper(Activity activity, OnPhotographGet onPhotographGet, @Nullable Dialog dialog) {
        this.onPhotographGet = onPhotographGet;
        this.mActivity = activity;
        this.mDialog = dialog;
        mOnDisposeOuterListener = PhotographStaticUtils.getPhotographBuilder(activity)
                //设置图片压缩路径
                .setCompressPath(COMPRESS_PATH)
                //设置照片暂时存路径
                .setImagePath(IMAGE_PATH)
                //设置是否删除没有压缩的拍照照片（默认是拍一张删一张）
                .setDelePGImage(false)
                //设置处理好的图片路径接口
                .setOnPhotographGetDataListener(new OnPhotographGetDataListener<File>() {
                    @Override
                    public void getPhotographData(File file) {
                        closeDialog();
                        PhotoHelper.this.onPhotographGet.getPhotographData(file);
                    }
                })
                //设置图片压缩大小（默认是1M）
                .setImageSize(IMAGE_SIZE)
                //构建图库和照相机处理操作对象暴露接口（OnDisposeOuterListener）
                .builder();
    }

    /**
     * 打开相册
     */
    public void openAlbum() {
        mOnDisposeOuterListener.startPhoto();
    }

    /**
     * 打开相机
     */
    public void openCamera() {
        mOnDisposeOuterListener.startCamera();
    }

    /**
     * 处理activity界面中图片回调操作
     *
     * @param requestCode ...
     * @param data        ...
     */
    public void onActivityResult(int requestCode, Intent data) {
        showDialog();
        mOnDisposeOuterListener.onActivityResult(requestCode, data);
    }

    /**
     * 防止内存泄漏,请求在界面kill时调用
     */
    public void clear() {
        mOnDisposeOuterListener.clear();
        mOnDisposeOuterListener = null;
        mDialog = null;
        mActivity = null;
    }

    /**
     * 显示对话框
     */
    private void showDialog() {
        if (null != mDialog) {
            mDialog.show();
        }
    }

    /**
     * 关闭对话框
     */
    private void closeDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public interface OnPhotographGet {
        /**
         * 照片处理完成
         *
         * @param file 照片路径
         */
        void getPhotographData(File file);
    }

}
