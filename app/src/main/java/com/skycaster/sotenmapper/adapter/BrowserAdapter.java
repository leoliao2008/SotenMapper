package com.skycaster.sotenmapper.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private Listener mListener;
    private File mCurDir;

    public BrowserAdapter(File rootFile, Context context,Listener listener) {
        mContext = context;
        mListener=listener;
        updateList(rootFile);
    }

    /**
     * 以当前文件为根目录，显示目录下所有文件/文件夹
     * @param dir 当前文件
     */
    private void updateList(File dir) {
        mCurDir=dir;
        mListener.onDirChange(mCurDir);
        mList.clear();
        File[] files =mCurDir.listFiles();
        if(files!=null){
            for(File f:files){
                mList.add(f);
            }
        }
        //实测中发现有的文件跳到上一层后无法在目录中显示 // TODO: 2018/4/12  
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
        holder.getTv_fileName().setText(file.getName());//显示文件名
        //显示文件图标
        //先把所有图标隐藏起来，再根据当前文件的情况显示特定的一个图标
        holder.getIv_file().setVisibility(View.GONE);
        holder.getIv_filledDir().setVisibility(View.GONE);
        holder.getIv_emptyDir().setVisibility(View.GONE);
        if(file.isDirectory()){
            File[] listFiles = file.listFiles();
            int len=0;
            if(listFiles!=null){
                len=listFiles.length;
            }
            if(len>0){
                //当前不为空文件夹
                holder.getIv_filledDir().setVisibility(View.VISIBLE);
                //点击图标可打开当前文件夹
                holder.getIv_filledDir().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateList(file);
                    }
                });
            }else {
                //当前为空文件夹
                holder.getIv_emptyDir().setVisibility(View.VISIBLE);
                //点击图标可打开当前文件夹
                holder.getIv_emptyDir().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateList(file);
                    }
                });
            }

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
        File file = mCurDir.getParentFile();
        if(file!=null){
            updateList(file);
        }
    }

    public interface Listener{
        void onFilePicked(File file);
        void onDirChange(File newDir);
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }
}
