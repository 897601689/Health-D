package com.health_d.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.MySerialPort;


/**
 * Created by Admin on 2016/3/4. 血氧解析类
 */
public class Spo2_Parsing {
    byte[] bytes = new byte[512];
    private final List<Byte> list = new ArrayList<>();
    byte[] buffer = new byte[5];

    private int signal_intensity = 0;  // 信号强度
    private int spo2_value;    // 血氧值
    private int pulse_value;   // 脉率值
    private String state;      // 血氧状态
    private int pi;            //灌注指数

    List<Integer> spo2_Curve = new ArrayList<>();

    public int getSignal_intensity() {
        return signal_intensity;
    }

    public int getSpo2_value() {
        return spo2_value;
    }

    public int getPulse_value() {
        return pulse_value;
    }

    public String getState() {
        return state;
    }

    public int getPi() {
        return pi;
    }

    public Spo2_Parsing() {

    }

    /**
     * 解析血氧数据
     *
     * @param port 串口对象
     */
    public void Parsing(MySerialPort port) {
        spo2_Curve.clear();
        try {
            bytes = port.Read();

            if (bytes != null) {

                for (byte aByte : bytes) {
                    list.add(aByte);
                }
                for (int i = 0; i < list.size(); i++) {
                    if ((list.get(i) >> 7 & 0x1) == 1 && list.size() >= i + 5) {//
                        for (int index = 0; index < 5; index++) {
                            buffer[index] = list.get(i);
                            list.remove(i);
                        }
                        i = -1;

                        signal_intensity = buffer[0] & 0xF; //信号强度
                        if ((buffer[0] >> 4 & 0x1) == 1) {
                            state = "搜索时间太长";
                        }
                        if ((buffer[0] >> 5 & 0x1) == 1) {
                            state = "未接传感器";
                        }
                        if ((buffer[0] >> 6 & 0x1) == 1) {
                            state = "脉搏跳动声";
                        }
                        if ((buffer[3] >> 4 & 0x1) == 1) {
                            state = "传感器错误";
                        }
                        if ((buffer[3] >> 5 & 0x1) == 1) {
                            state = "搜索脉搏";
                        }

                        spo2_Curve.add(buffer[1] & 0x7F);   //体积描记图
                        pi = buffer[2] & 0xF;    //棒图

                        //脉率
                        pulse_value = (buffer[2] & 0x40) << 1 | (buffer[3] & 0x7F);
                        //血氧
                        spo2_value = buffer[4] & 0x7F;
                    }
                }
            } else {
                //Log.e("null", "null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
