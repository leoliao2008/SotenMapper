package com.skycaster.sotenmapper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.adapter.LocalMapListAdapter;
import com.skycaster.sotenmapper.base.BaseFragment;
import com.skycaster.sotenmapper.bean.MyMKOLUpdateElement;
import com.skycaster.sotenmapper.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class LocalMapListFragment extends BaseFragment {
    private MKOfflineMap mManager;
    private ArrayList<MyMKOLUpdateElement> mLocalMaps=new ArrayList<>();
    private LocalMapListAdapter mAdapter;
    private LocalMapListAdapter.CallBack mCallBack = new LocalMapListAdapter.CallBack() {
        @Override
        public void onDelete(int cityId) {
            mManager.remove(cityId);
            refreshList();
        }

        @Override
        public void onRequestUpdate(int cityId) {
            mManager.update(cityId);
        }
    };


    public LocalMapListFragment(MKOfflineMap manager) {
        mManager = manager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_maps_list, null);
        ListView listView = view.findViewById(R.id.list_view);
        mAdapter=new LocalMapListAdapter(
                mLocalMaps,
                getActivity(),
                R.layout.item_local_map,
                mCallBack
        );
        listView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void refreshList() {
        mLocalMaps.clear();
        ArrayList<MKOLUpdateElement> allUpdateInfo = mManager.getAllUpdateInfo();
        if(allUpdateInfo!=null){
            for(MKOLUpdateElement temp:allUpdateInfo){
                mLocalMaps.add(new MyMKOLUpdateElement(temp));
            }
        }
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onGetOfflineMapState(int type, int state){
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement downLoadProgress = mManager.getUpdateInfo(state);
                if(!isLocalMapsContain(downLoadProgress)){
                    mLocalMaps.add(new MyMKOLUpdateElement(downLoadProgress));
                }
                // 处理下载进度更新提示
                if(downLoadProgress!=null){
                    for(int i=0;i<mLocalMaps.size();i++){
                        if(mAdapter.getItemId(i)==downLoadProgress.cityID){
                            mLocalMaps.get(i).ratio=downLoadProgress.ratio;
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    showLog(String.format(Locale.CHINA,"地图%s 下载进度%d%%...",downLoadProgress.cityName,downLoadProgress.ratio));
                    if(downLoadProgress.ratio==100){
                        ToastUtil.showToast( String.format("地图下载完成：%s!",downLoadProgress.cityName));
                    }
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                //Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                MKOLUpdateElement newMapVersion = mManager.getUpdateInfo(state);
                if(newMapVersion!=null){
                    for(int i=0;i<mLocalMaps.size();i++){
                        if(mAdapter.getItemId(i)==newMapVersion.cityID){
                            mLocalMaps.get(i).update=newMapVersion.update;
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private boolean isLocalMapsContain(MKOLUpdateElement downLoadProgress) {
        for(MyMKOLUpdateElement temp:mLocalMaps){
            if(temp.equals(downLoadProgress)){
                return true;
            }
        }
        return false;
    }
}
