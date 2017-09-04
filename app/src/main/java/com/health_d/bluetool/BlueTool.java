package com.health_d.bluetool;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Admin on 2016/1/12.
 */
public class BlueTool {
    private List<String> array_name;        //要搜索的蓝牙名称
    //蓝牙地址
    private HashMap<String, BluetoothDevice> map_dev;
    public String OperateBlueName;        //操作的蓝牙
    public boolean isSearchOver = false;
    private boolean isFindBlueName = true;   //true 查询是搜索 蓝牙名字   false 蓝牙地址
    private boolean isAllSearch = true;
    private String dev_name;
    private Activity mActivity;
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mbluetoothSocket;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    public BlueTool(Activity mActivity, List<String> BlueNames) {
        this.mActivity = mActivity;
        array_name = BlueNames;
        isFindBlueName = true;
        isAllSearch = true;
        initBlueTool();
    }

    public boolean IsSearchOver() {
        return isSearchOver;
    }

    private void initBlueTool() {
        // TODO Auto-generated constructor stub
        map_dev = new HashMap<String, BluetoothDevice>();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!openBlue()) {
            Log.i("MyBlueTest", "没有蓝牙");
        }

      //  initIntentFilter();
        searchBlue();
    }

    public boolean SendMessage(String data) {
        //if(!isConnect) return false;
        if (data.equals("")) return false;
        String[] array = data.split(" ");
        byte[] buffer = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            int exterData = Integer.valueOf(array[i], 16);
            buffer[i] = (byte) exterData;
            //String ss = String.valueOf(buffer[i]);
            //Log.i("read", ss);
        }
        try {
            outputStream.write(buffer);
        } catch (IOException e) {
            Log.i("MyBlueTest", "发送失败");
            return false;
        }
        return true;
    }

    public byte[] RecvMessage(boolean block) throws IOException {
        //if(!isConnect) throw new IOException("没有连接上蓝牙");
        int len = 0;
        try {
            len = inputStream.available();
        } catch (IOException e) {
            Log.i("MyBlueTest", "读取失败");
            throw new IOException("断开了");
        }


        if (block ? true : len > 0) {
            byte[] buffer;
            if (block) {
                buffer = new byte[1024];
            } else {
                buffer = new byte[len];
            }
            try {
                len = inputStream.read(buffer);
                if (!block)
                    return buffer;
                if (block) {
                    byte[] bt = new byte[len];
                    for (int i = 0; i < len; i++) {
                        bt[i] = buffer[i];
                    }
                    return bt;
                }
            } catch (IOException e) {
                Log.i("MyBlueTest", "读取失败");
                throw new IOException("断开了");
            }
        } else {
            //throw new IOException("没有信息");
        }
        return null;
    }

    public boolean isFindDev(String BlueName) {
        BluetoothDevice dev = map_dev.get(BlueName);
        if (dev == null) {
            return false;
        }
        return true;
    }

    public void StartSearch() {
        isAllSearch = true;
        searchBlue();
    }

    public void StartSearch(String BlueName) {
        dev_name = BlueName;
        isAllSearch = false;
        searchBlue();
    }

    public boolean Connect() {
        if (!isSearchOver) return false;
        BluetoothDevice dev = map_dev.get(OperateBlueName);
        if (dev == null) {
            Log.i("MyBlueTest", "没找到蓝牙名称");
            //	if(isSearchOver)
            //		searchBlue();
            return false;
        }

        //if(pairBlue(dev)) return false;
        if (!connectBlue(dev)) {
            Log.i("MyBlueTest", "蓝牙连接失败");
            return false;
        }
        return true;
    }
/*	public boolean Connect(String num)
    {
		BluetoothDevice dev = map_dev.get(OperateBlueName);
		if(dev==null)
		{
			Log.i("MyBlueTest", "没找到蓝牙名称");
			if(isSearchOver)
				searchBlue();
			isConnect=false;
			return false;
		}
		if(!connectBlue(dev))
		{
			Log.i("MyBlueTest", "蓝牙连接失败");
			isConnect=false;
			return false;
		}
		isConnect=true;
		return true;
	}*/

    public void DisConnect() {
        try {
            if (mbluetoothSocket != null) {
                mbluetoothSocket.close();
                mbluetoothSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean connectBlue(BluetoothDevice dev) {
        try {
            Log.i("MyBlueTest", "蓝牙开始连接");
            mbluetoothSocket = dev.createRfcommSocketToServiceRecord(uuid);
            mbluetoothSocket.connect();
            outputStream = mbluetoothSocket.getOutputStream();
            inputStream = mbluetoothSocket.getInputStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createBond(BluetoothDevice dev) {
        try {
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            createBondMethod.invoke(dev);                //直到机器码 直接用就行   回来试试  行了就不用搜索了
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //搜索蓝牙
    private void searchBlue() {
        map_dev.clear();
      //  isSearchOver = false;
      // mBluetoothAdapter.startDiscovery();
        Set<BluetoothDevice> devs = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice dev:devs)
        {
            for(int i=0;i<array_name.size();i++)
            {
                Log.i("",dev.getName());
                if (dev.getName().equals(array_name.get(i))) {
                    Log.i("","111111111111111");
                    map_dev.put(dev.getName(), dev);
                    if (map_dev.size() == array_name.size()) {
                        isSearchOver=true;
                        return;
                    }
                }
            }
        }
        isSearchOver=true;
    }

    //打开蓝牙
    private boolean openBlue() {
        if (mBluetoothAdapter != null) {  //判断是否有蓝牙
            if (!mBluetoothAdapter.isEnabled()) {//判断蓝牙 是否开启，未开启则请用户开启
                mBluetoothAdapter.enable();
            }
            return true;
        } else {
            return false;
        }
    }

    // 设置广播信息过滤
    private void initIntentFilter() {
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        intentFilter.addAction(ACTION_PAIRING_REQUEST);
        // 注册广播接收器，接收并处理搜索结果

        mActivity.registerReceiver(receiver, intentFilter);
        //context.registerReceiver(receiver, intentFilter);
    }

    /**
     * 蓝牙广播接收器
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Log.i("MyBlueTest", "+++++++++++++++++++++++" + device.getName());
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    for (int i = 0; i < array_name.size(); i++) {
                        if (isAllSearch) {
                            if (device.getName().equals(array_name.get(i))) {
                                map_dev.put(device.getName(), device);
                                if (map_dev.size() == array_name.size()) {
                                    mBluetoothAdapter.cancelDiscovery();
                                }
                            }
                        } else {
                            //Log.i("MyBlueTest", "+++++++++++++++++++++++" + device.getName() + "  " + dev_name);
                            if (device.getName().equals(dev_name)) {
                                map_dev.put(device.getName(), device);
                                mBluetoothAdapter.cancelDiscovery();
                            }
                        }
                    }

                } else {

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //progressDialog = ProgressDialog.show(context, "请稍等...","搜索蓝牙设备中...", true);
                Log.i("MyBlueTest", "搜索蓝牙设备中...");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                //System.out.println("设备搜索完毕");
                //progressDialog.dismiss();
                Log.i("MyBlueTest", "设备搜索完毕");
                isSearchOver = true;
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                    //System.out.println("--------打开蓝牙-----------");
                } else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    //System.out.println("--------关闭蓝牙-----------");
                }
            }
            /*	if(ACTION_PAIRING_REQUEST.equals(action)){
                    BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					Log.i("MyBlueTest", "1234"+ dev.getName()+"  " + dev.getAddress()+" "+dev.getBondState());
					if(dev.getBondState()==BluetoothDevice.BOND_BONDING)
					{
						try {
						//	BlueTool.cancelPairingUserInput(dev.getClass(), dev);
							BlueTool.setPin(dev.getClass(), dev, "1234");
							BlueTool.createBond(dev.getClass(), dev);
						//	cancelPairingUserInput(dev.getClass(), dev);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Log.i("MyBlueTest", e.getMessage());
							e.printStackTrace();
						}
					}
					else
					{
						try {
						//	BlueTool.cancelPairingUserInput(dev.getClass(), dev);
							BlueTool.setPin(dev.getClass(), dev, "1234");
						//	BlueTool.cancelBondProcess(dev.getClass(), dev);
							BlueTool.createBond(dev.getClass(), dev);
							//BlueTool.cancelPairingUserInput(dev.getClass(), dev);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}*/
        }

    };

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean setPin(BluetoothDevice dev, String str) throws Exception {
        try {
            Method removeBondMethod = BluetoothDevice.class.getDeclaredMethod("setPin", new Class[]{byte[].class});
            Boolean returnValue = (Boolean) removeBondMethod.invoke(dev, new Object[]{str.getBytes()});
            Log.e("MyBlueTest", "  " + returnValue);
        } catch (SecurityException e) {
            Log.e("MyBlueTest", "  1" + e.getMessage());
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Log.e("MyBlueTest", "  2" + e.getMessage());
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("MyBlueTest", "  3" + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    // 取消用户输入
    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean cancelPairingUserInput(BluetoothDevice dev) throws Exception {
        Method createBondMethod = BluetoothDevice.class.getMethod("cancelPairingUserInput");
        // cancelBondProcess()
        Boolean returnValue = (Boolean) createBondMethod.invoke(dev);
        return returnValue.booleanValue();
    }


    static public boolean createBond(Class btClass, BluetoothDevice btDevice)
            throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Log.i("life", "createBondMethod = " + createBondMethod.getName());
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    static public boolean setPin(Class btClass, BluetoothDevice btDevice,
                                 String str) throws Exception {
        Boolean returnValue = null;
        try {
            Method removeBondMethod = btClass.getDeclaredMethod("setPin",
                    new Class[]{byte[].class});
            returnValue = (Boolean) removeBondMethod.invoke(btDevice,
                    new Object[]{str.getBytes()});
            Log.e("MyBlueTest", "returnValue  " + returnValue);
        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return returnValue;
    }

    // 取消用户输入
    static public boolean cancelPairingUserInput(Class btClass,
                                                 BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        cancelBondProcess(btClass, device);
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        Log.i("MyBlueTest", "cancelPairingUserInputreturnValue = " + returnValue);
        return returnValue.booleanValue();
    }

    static public boolean cancelBondProcess(Class btClass, BluetoothDevice device) throws Exception {
        Method createBondMethod = btClass.getMethod("cancelBondProcess");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        Log.i("MyBlueTest", "cancelBondProcess = " + returnValue);
        return returnValue.booleanValue();
    }
}
