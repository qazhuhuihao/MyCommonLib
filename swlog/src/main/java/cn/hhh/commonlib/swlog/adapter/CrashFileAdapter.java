package cn.hhh.commonlib.swlog.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import cn.hhh.commonlib.swlog.R;

/**
 * @author qazhu
 * @date 2017/12/21
 */

public class CrashFileAdapter extends RecyclerView.Adapter<CrashFileAdapter.CrashFileViewHolder> {

    private Activity activity;
    private File[] files;
    private View.OnClickListener onClickListener;

    public CrashFileAdapter(Activity activity, File[] files, View.OnClickListener onClickListener) {
        this.activity = activity;
        this.files = files;
        this.onClickListener = onClickListener;
    }

    @Override
    public CrashFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CrashFileViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carsh_file, parent, false));
    }

    @Override
    public void onBindViewHolder(CrashFileViewHolder holder, int position) {
        holder.tvName.setText(files[position].getName());
        holder.itemView.setTag(files[position]);
        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return null == files ? 0 : files.length;
    }

    class CrashFileViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public CrashFileViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
