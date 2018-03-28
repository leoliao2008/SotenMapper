package com.skycaster.sotenmapper.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.activity.MapAdminActivity;
import com.skycaster.sotenmapper.adapter.AvailMapListAdapter;
import com.skycaster.sotenmapper.base.BaseFragment;
import com.skycaster.sotenmapper.bean.MyMKOLSearchRecord;

import java.util.ArrayList;

/**
 * Created by 廖华凯 on 2018/3/27.
 */

public class AvailMapListFragment extends BaseFragment {
    private MKOfflineMap mManager;
    private ArrayList<MyMKOLSearchRecord> mOfflineCityList=new ArrayList<>();
    private EditText mEdt_input;
    private Button mBtn_search;
    private ListView mListView;
    private AvailMapListAdapter mAdapter;
    private AvailMapListAdapter.CallBack mCallBack = new AvailMapListAdapter.CallBack() {
        @Override
        public void onRequestDownLoad(int cityId) {
            showLog("start to download city id = "+cityId);
            mManager.start(cityId);
            for(MyMKOLSearchRecord temp:mOfflineCityList){
                if(temp.cityID==cityId){
                    temp.isDownLoaded=true;
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    };
    private MapAdminActivity mActivity;

    public AvailMapListFragment(MKOfflineMap manager) {
        mManager = manager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mManager=new MKOfflineMap();
//        mManager.init(new MKOfflineMapListener() {
//            @Override
//            public void onGetOfflineMapState(int i, int i1) {
//
//            }
//        });
        showLog("onCreate");
        mActivity = (MapAdminActivity) getActivity();
//        mManager=mActivity.getMapManager();

    }

    /**
     * 更新离线地图清单
     * @param keyWord 根据关键字搜索并更新离线地图清单，当为空时，搜索所有离线地图
     */
    private void updateMapList(@Nullable String keyWord) {
        showLog("updateMapList");
        mOfflineCityList.clear();
        ArrayList<MKOLSearchRecord> cityList;
        if(keyWord==null){
            cityList=new ArrayList<>();
            ArrayList<MKOLSearchRecord> list = mManager.getOfflineCityList();
            for(MKOLSearchRecord record:list){
                if(record.cityType==1){
                    //城市类型0:全国；1：省份；2：城市,如果是省份，可以通过childCities得到子城市列表
                    cityList.addAll(record.childCities);
                }
            }
        }else {
            cityList=mManager.searchCity(keyWord);
        }
        if(cityList!=null){
            Log.e(getClass().getSimpleName(),"city list size ="+cityList.size());
            ArrayList<MKOLUpdateElement> localMaps = mManager.getAllUpdateInfo();
            for(MKOLSearchRecord city:cityList){
                MyMKOLSearchRecord temp = new MyMKOLSearchRecord(city);
                if(localMaps!=null){
                    for(MKOLUpdateElement map:localMaps){
                        if(map.cityID==temp.cityID){
                            temp.isDownLoaded=true;
                            break;
                        }
                    }
                }
                mOfflineCityList.add(temp);
            }
        }
        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        showLog("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_avail_offline_maps, null);
        mEdt_input = rootView.findViewById(R.id.edt_search_input);
        mBtn_search = rootView.findViewById(R.id.btn_search);
        mListView = rootView.findViewById(R.id.list_view);
        mAdapter=new AvailMapListAdapter(
                mOfflineCityList,
                getContext(),
                R.layout.item_avail_offline_map,
                mCallBack
        );
        mListView.setAdapter(mAdapter);
        updateMapList(null);
        mBtn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEdt_input.getText().toString().trim();
                updateMapList(input);
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
