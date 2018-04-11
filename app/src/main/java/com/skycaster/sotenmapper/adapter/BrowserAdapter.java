package com.skycaster.sotenmapper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.vh.BrowserAdapterViewHolder;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/4/11.
 */

public class BrowserAdapter extends RecyclerView.Adapter<BrowserAdapterViewHolder> {
    private ArrayList<File> mList=new ArrayList<>();
    private Context mContext;
    private File preDir;
    private File curDir;
    private Listener mListener;

    public BrowserAdapter(File rootFile, Context context,Listener listener) {
        updateList(rootFile);
        mContext = context;
        mListener=listener;
    }

    /**
     * 以当前文件为根目录，显示目录下所有文件/文件夹
     * @param dir 当前文件
     */
    private void updateList(File dir) {
        curDir=dir;
        mList.clear();
        File[] files =dir.listFiles();
        if(files!=null&&files.length>0){
            for(File f:files){
                mList.add(f);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public BrowserAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(mContext, R.layout.item_brower,null);
        return new BrowserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BrowserAdapterViewHolder holder, int position) {
        final File file = mList.get(position);
        //先把所有图标隐藏起来，再根据当前文件的情况显示特定的一个图标
        holder.getIv_file().setVisibility(View.GONE);
        holder.getIv_filledDir().setVisibility(View.GONE);
        holder.getIv_emptyDir().setVisibility(View.GONE);
        if(file.isDirectory()){
            int length = file.listFiles().length;
            if(length>0){
                //当前不为空文件夹
                holder.getIv_filledDir().setVisibility(View.VISIBLE);
            }else {
                //当前为空文件夹
                holder.getIv_emptyDir().setVisibility(View.VISIBLE);
            }
            //点击图标可打开当前文件夹
            holder.getIv_filledDir().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    preDir=curDir;
                    updateList(file);
                }
            });
        }else {
            //当前为文件
            holder.getIv_file().setVisibility(View.VISIBLE);
            //点击图标可点选升级文件
            holder.getIv_file().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFilePicked(file);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 返回上一层
     */
    public void back(){
        if(preDir!=null){
            updateList(preDir);
        }
    }

    public interface Listener{
        void onFilePicked(File file);
    }
}
