package com.skycaster.sotenmapper.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.skycaster.sotenmapper.R;
import com.skycaster.sotenmapper.adapter.BrowserAdapter;
import com.skycaster.sotenmapper.callbacks.AlertDialogCallBack;
import com.skycaster.sotenmapper.module.FileWriterModule;
import com.skycaster.sotenmapper.module.SerialPortModule;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * Created by 廖华凯 on 2018/3/19.
 */

public class AlertDialogUtils {
    private static AlertDialog alertDialog;


    private AlertDialogUtils() {
    }

    //********************************************************public*******************************************************

    /**
     * 跳出一个设置App端串口路径和波特率的对话框
     * @param context 背景上下文
     * @param defaultPath 默认选择的串口路径
     * @param defaultRate 默认选择的波特率
     * @param callBack 回调
     */
    public static void showAppSpConfigWindow(Context context, String defaultPath, String defaultRate, final AlertDialogCallBack callBack) {
        showSpSettingWindow(
                context,
                defaultPath,
                new SerialPortModule().getAvailablePaths(),
                defaultRate,
                context.getResources().getStringArray(R.array.baud_rates_name),
                callBack
        );
    }

    /**
     * 弹出一个设置SK9042差分数据输出端的波特率的窗口
     * @param context 上下文背景
     * @param callBack 回调
     */
    public static void showSK9042SpConfigWindow(Context context,final AlertDialogCallBack callBack) {
        showSpinSelections(
                context,
                context.getResources().getString(R.string.please_select_bd_rate),
                new String[]{"9600","57600","19200","115200"},
                callBack);
    }

    /**
     * 跳出一个选择串口波特率的对话框
     * @param context 上下文背景
     * @param callBack 回调
     */
    public static void selectBaudRate(Context context, final AlertDialogCallBack callBack) {
        final String[] strings = context.getResources().getStringArray(R.array.baud_rates_name);
        showSpinSelections(context,context.getString(R.string.choose_bd_rate),strings,callBack);
    }

    /**
     * 跳出一个设置SK9042主频的对话框
     * @param context 上下文背景
     * @param callback 回调
     */
    public static void showSK9042SetFreqWindow(Context context , final AlertDialogCallBack callback) {
        showInputWindow(context, context.getResources().getString(R.string.please_input_freq),InputType.TYPE_CLASS_NUMBER,callback);
    }

    public static void showSK9042SetReceiveModeWindow(Context context,AlertDialogCallBack callBack){
        String [] modes=new String[]{"2","3","4"};
        showSpinSelections(context,context.getString(R.string.please_set_receive_mode),modes,callBack);
    }

    public static void showSK9042SetRunningModeWindow(Context context, AlertDialogCallBack callBack){
        String [] modes=new String[]{"1","2","3"};
        showSpinSelections(context,context.getString(R.string.please_set_running_mode),modes,callBack);
    }

    public static void showSK9042SetChipIdWindow(Context context, AlertDialogCallBack callBack) {
        showInputWindow(context,context.getResources().getString(R.string.please_input_chip_id),InputType.TYPE_CLASS_TEXT,callBack);
    }

    public static void showSK9042SetLogLevelWindow(Context context,AlertDialogCallBack callBack) {
        showSpinSelections(context,context.getString(R.string.set_log_level),new String[]{"0","1","2","3","4","5"},callBack);
    }

    /**
     * 弹出一个窗口，输入特定的一个频率，测试Sk9042能否通过该频率接收数据
     */
    public static void showSK9042VerifyFreqWindow(Context context,AlertDialogCallBack callBack) {
        showInputWindow(context,context.getResources().getString(R.string.please_input_freq),InputType.TYPE_CLASS_NUMBER,callBack);
    }


    /**
     * 弹出一个窗口，点选升级文件，开始升级SK9042的系统
     */
    public static void showSK9042SysUpgradeWindow(Context context, final AlertDialogCallBack callBack) {
        //views
        View rootView=View.inflate(context,R.layout.dialog_pick_src_file,null);
        Button btn_back=rootView.findViewById(R.id.btn_back);
        Button btn_exit=rootView.findViewById(R.id.btn_exit);
        final TextView tv_path=rootView.findViewById(R.id.tv_path);
        RecyclerView browser=rootView.findViewById(R.id.browser);
        final File rootFile = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS);
        //data
        final BrowserAdapter adapter=new BrowserAdapter(rootFile, context, new BrowserAdapter.Listener() {
            @Override
            public void onFilePicked(File file) {
                callBack.onGetFile(file);
                alertDialog.dismiss();
            }

            @Override
            public void onDirChange(File newDir) {
                tv_path.setText(newDir.getAbsolutePath());
            }
        });
        browser.setLayoutManager(new GridLayoutManager(context,10,GridLayoutManager.VERTICAL,false));
        browser.setAdapter(adapter);
        //listener
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.back();
            }
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        //build dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        alertDialog=builder.setView(rootView).setCancelable(false).create();
        alertDialog.show();
    }

    /**
     * 创建新文件前，设置文件名称的窗口
     * @param context 上下文
     * @param callBack 回调 ，onGetInput()中可以获取文件名
     */
    public static void showGenNewFileNameWindow(Context context, AlertDialogCallBack callBack){
        showInputWindow(
                context,
                context.getResources().getString(R.string.please_input_file_name),
                InputType.TYPE_CLASS_TEXT,
                FileWriterModule.genFileNameByDate(),
                false,
                callBack);
    }



    //***************************************************private*********************************************************

    /**
     * 跳出一个包含标题，下滑菜单，确定/取消按钮的窗口
     * @param context 上下文
     * @param title 标题
     * @param selections 下滑菜单的选项
     * @param callBack 回调
     */
    private static void showSpinSelections(Context context, String title, final String [] selections, final AlertDialogCallBack callBack){
        //init views
        View rootView = View.inflate(context, R.layout.dialog_selection_spinner, null);
        TextView tv_title=rootView.findViewById(R.id.dialog_tv_title);
        Spinner spinner=rootView.findViewById(R.id.dialog_spn);
        Button btn_confirm=rootView.findViewById(R.id.dialog_btn_confirm);
        Button btn_cancel=rootView.findViewById(R.id.dialog_btn_cancel);
        //init data
        tv_title.setText(title);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                selections
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        final String[] temp=new String[1];
        //init listeners
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp[0]=selections[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callBack.onGetInput(temp[0]);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        alertDialog=builder.setView(rootView).create();
        alertDialog.show();
    }

    private static void showInputWindow(Context context,String title,int inputType,final AlertDialogCallBack callBack){
        showInputWindow(context,title,inputType,null,true,callBack);
    }


    private static void showInputWindow(Context context,String title,int inputType,@Nullable String defaultInput, boolean isCancelable,final AlertDialogCallBack callBack){
        View rootView = View.inflate(context, R.layout.dialog_get_input, null);
        TextView tv_title=rootView.findViewById(R.id.dialog_tv_title);
        final EditText edt_input=rootView.findViewById(R.id.dialog_edt_input);
        edt_input.setInputType(inputType);
        Button btn_confirm=rootView.findViewById(R.id.dialog_btn_confirm);
        Button btn_cancel=rootView.findViewById(R.id.dialog_btn_cancel);
        tv_title.setText(title);
        if(defaultInput!=null){
            edt_input.setText(defaultInput);
            edt_input.setSelection(defaultInput.length());
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edt_input.getText().toString();
                alertDialog.dismiss();
                callBack.onGetInput(input);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        alertDialog=builder.setView(rootView).setCancelable(isCancelable).create();
        alertDialog.show();
    }



    private static void showSpSettingWindow(
            Context context,
            @Nullable String defaultPath,
            final String[] pathSelection,
            @Nullable String defaultRate,
            final String[] bdRateSelection,
            final AlertDialogCallBack callBack){
        //init view
        View rootView = View.inflate(context, R.layout.dialog_sp_config, null);
        Spinner spn_path=rootView.findViewById(R.id.dialog_spin_sp_path);
        Spinner spn_rate=rootView.findViewById(R.id.dialog_spin_bd_rate);
        Button btn_confirm=rootView.findViewById(R.id.dialog_btn_confirm);
        Button btn_cancel=rootView.findViewById(R.id.dialog_btn_cancel);
        //init data
        //串口路径下滑菜单
        ArrayAdapter<String> pathAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                pathSelection
        );
        pathAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_path.setAdapter(pathAdapter);
        if(defaultPath!=null){
            for(int i=0;i<pathSelection.length;i++){
                if(pathSelection[i].equals(defaultPath)){
                    spn_path.setSelection(i);
                    break;
                }
            }
        }


        //波特率下滑菜单
        ArrayAdapter<String> rateAdapter=new ArrayAdapter<String>(
                context,
                android.R.layout.simple_list_item_1,
                bdRateSelection
        );
        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_rate.setAdapter(rateAdapter);
        if(defaultRate!=null){
            for(int i=0;i<bdRateSelection.length;i++){
                if(bdRateSelection[i].equals(defaultRate)){
                    spn_rate.setSelection(i);
                    break;
                }
            }
        }

        final String temp[]=new String[]{defaultPath,defaultRate};

        //init listener
        spn_path.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp[0]=pathSelection[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_rate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                temp[1]=bdRateSelection[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                callBack.onGetSpParams(temp[0],temp[1]);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        alertDialog=builder.setView(rootView).create();
        alertDialog.show();
    }

    private static void showLog(String msg){
        Log.e("AlertDialogUtils",msg);
    }



}
