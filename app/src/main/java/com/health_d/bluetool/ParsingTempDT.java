package com.health_d.bluetool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2016/8/12.
 */
public class ParsingTempDT {
    public static String DEV_NAME = "BF4030";
    public String TempValue;

    private List<byte[]> array_recv_cmd = new ArrayList<>();
    private List<Byte> array_recv_byte = new ArrayList<>();

    public ParsingTempDT() {
        //    ErrorCode = new List<String>();
    }

    public boolean run(byte[] bDatas) {
        TempValue = null;
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

            if (bDatas[i] == (byte)0xFF)
            {
                array_recv_byte.add(bDatas[i]);
            }
            else
            {
                array_recv_byte.add(bDatas[i]);
                if (array_recv_byte.size() >= 4)
                {
                    byte[] buffer = new byte[array_recv_byte.size()];
                    for (int num = 0; num < buffer.length; num++)
                    {
                        buffer[num] = array_recv_byte.get(num);
                    }
                    array_recv_cmd.add(buffer);
                    array_recv_byte.clear();
                }
            }
        }
    }

    private Boolean isCheckCmd(byte[] buffer) {
        if (buffer[0] == (byte)0xFF)
        {
            if (buffer[3] == (buffer[1] ^ buffer[2]))
            {
                return true;
            }
        }
        return false;

    }


    private void parsingCMD(byte[] buffer) {
        if (buffer[0] == (byte)0xFF)
        {
            String ss;
            ss = String.format("{0:x}{1:x}", buffer[1], buffer[2]);
            float fTempValue = (Integer.valueOf(ss,16) / 10.0f);
            TempValue = String.valueOf(fTempValue);
        }

    }
}
