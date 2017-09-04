package com.health_d.bluetool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2016/1/12.
 */
public class ParsingSugar {
    public ParsingSugar() {
        // TODO Auto-generated constructor stub

    }

    public enum SugerState {
        glint("glint"), test("test"), shoutdown("shoutdown");
        String ss;

        SugerState(String ss) {
            this.ss = ss;
        }

        public String toString() {
            return ss;
        }
    }    //闪烁 测试 关机

    public SugerState state;
    public String ResultValue;
    public String ErrorCode;
    public boolean isConnect;

    final public static String DEV_NAME = "Sinocare";
    private boolean isFindStartByte = false;    //是否找到头

    private int one_data_num = 0;
    private List<byte[]> array_recv_cmd = new ArrayList<byte[]>();
    private List<Byte> array_recv_byte = new ArrayList<Byte>();
    boolean find_len = true;
    int len = 0, nowlen = 0;

    public boolean run(byte[] bDatas) {
        ResultValue = null;
        ErrorCode = null;
        isConnect = false;
        state = null;
        array_recv_cmd.clear();
        if (bDatas == null) return false;
        dataItem(bDatas);

        for (int i = 0; i < array_recv_cmd.size(); i++) {
            if (isCheckCmd(array_recv_cmd.get(i))) {

                parsingCMD(array_recv_cmd.get(i));
            }

        }
        return true;
    }

    private void dataItem(byte[] bDatas) {
        for (int i = 0; i < bDatas.length; i++) {
            if (!isFindStartByte) {
                if (bDatas[i] == (byte) 0x53) {
                    one_data_num++;
                } else if (bDatas[i] == (byte) 0x4e && one_data_num > 0) {
                    isFindStartByte = true;
                    array_recv_byte.add((byte) 0x53);
                    array_recv_byte.add((byte) 0x4e);
                    one_data_num = 0;
                } else {
                    one_data_num = 0;
                }
            } else {

                if (find_len) {
                    len = bDatas[i];
                    array_recv_byte.add(bDatas[i]);
                    find_len = false;
                } else {
                    nowlen++;
                    array_recv_byte.add(bDatas[i]);
                    if (nowlen == len) {
                        byte[] buffer = new byte[array_recv_byte.size()];
                        for (int num = 0; num < buffer.length; num++) {
                            buffer[num] = array_recv_byte.get(num);
                        }
                        if (isCheckCmd(buffer)) {
                            array_recv_cmd.add(buffer);
                        }
                        array_recv_byte.clear();
                        nowlen = 0;
                        len = 0;
                        find_len = true;
                        isFindStartByte = false;
                    }
                }
            }
        }
    }

    private boolean isCheckCmd(byte[] buffer) {
        byte sum = 0;
        for (int i = 2; i < buffer.length - 1; i++) {
            sum = (byte) (sum + buffer[i]);
        }
        if (sum == buffer[buffer.length - 1]) {
            return true;
        }
        return false;
    }

    private void parsingCMD(byte[] buffer) {
        if (buffer[5] == (byte) 0x01)        //测试连接命令
        {
            isConnect = true;
        }
        if (buffer[5] == (byte) 0x02)        //错误状态
        {
            if (buffer[6] == 0x00 && buffer[7] == 0x01) {
                ErrorCode = "E-1";
            } else if (buffer[6] == 0x00 && buffer[7] == 0x02) {
                ErrorCode = "E-2";
            } else if (buffer[6] == 0x00 && buffer[7] == 0x03) {
                ErrorCode = "E-3";
            } else if (buffer[6] == 0x01 && buffer[7] == 0x01) {
                ErrorCode = "HI";
            } else if (buffer[6] == 0x01 && buffer[7] == 0x02) {
                ErrorCode = "LO";
            }
        }
        if (buffer[5] == (byte) 0x03)        //滴血符号闪烁
        {
            state = SugerState.glint;
        }
        if (buffer[5] == (byte) 0x04)        //读当前结果命令
        {
            int num = ((buffer[buffer.length - 6] & 0xff) << 8) | (buffer[buffer.length - 5] & 0xff);
            float fnum = (float) (num / 10.0);
            ResultValue = String.valueOf(fnum);
        }
        if (buffer[5] == (byte) 0x0A)        //开始测试命令
        {
            state = SugerState.test;
        }
        if (buffer[5] == (byte) 0x0B)        //仪器关机命令
        {
            state = SugerState.shoutdown;
        }


    }
}
