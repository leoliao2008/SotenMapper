package com.skycaster.sotenmapper.vh;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skycaster.sotenmapper.R;

/**
 * Created by 廖华凯 on 2018/4/11.
 */

public class BrowserAdapterViewHolder extends RecyclerView.ViewHolder {
    private TextView tv_fileName;
    private ImageView iv_file;
    private ImageView iv_emptyDir;
    private ImageView iv_filledDir;
    public BrowserAdapterViewHolder(View itemView) {
        super(itemView);
        tv_fileName=itemView.findViewById(R.id.tv_fileName);
        iv_file=itemView.findViewById(R.id.iv_file);
        iv_emptyDir=itemView.findViewById(R.id.iv_empty_dir);
        iv_filledDir=itemView.findViewById(R.id.iv_content_dir);
    }

    public TextView getTv_fileName() {
        return tv_fileName;
    }

    public ImageView getIv_file() {
        return iv_file;
    }

    public ImageView getIv_emptyDir() {
        return iv_emptyDir;
    }

    public ImageView getIv_filledDir() {
        return iv_filledDir;
    }
}
