package cn.hhh.commonlib.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * function :剪切板工具类
 */
@SuppressWarnings("unused")
public class ClipboardUtil {

    /**
     * 拷贝文本字符串到剪切板
     */
    public static void copyTextToClipboard(Context context, CharSequence content) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null != clipboardManager)
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));
    }

    /**
     * 从剪切板获取已剪切的文本
     */
    public static CharSequence pasteTextFromClipboard(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null != clipboardManager) {
            ClipData clip = clipboardManager.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                return clip.getItemAt(0).coerceToText(context);
            }
        }
        return null;
    }
}
