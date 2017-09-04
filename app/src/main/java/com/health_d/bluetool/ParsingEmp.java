package com.health_d.bluetool;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2016/1/12.
 */
public class ParsingEmp {
    final public static String DEV_NAME = "EMP-Ui";
    String EorrCode;
    final String[] LEUS = {"-", "+-", "+1", "+2", "+3"};
    final String[] NITS = {"-", "+"};
    final String[] UBGS = {"-", "+1", "+2", "+3"};
    final String[] PROS = {"-", "+-", "+1", "+2", "+3", "+4"};
    final String[] PHS = {"5.0", "6.0", "6.5", "7.0", "7.5", "8.0", "8.5"};
    final String[] BLDS = {"-", "+-", "+1", "+2", "+3"};
    final String[] SGS = {"1.000", "1.005", "1.010", "1.015", "1.020", "1.025", "1.030"};
    final String[] KETS = {"-", "+-", "+1", "+2", "+3", "+4"};
    final String[] BILS = {"-", "+1", "+2", "+3"};
    final String[] GLUS = {"-", "+-", "+1", "+2", "+3", "+4"};
    final String[] VCS = {"-", "+-", "+1", "+2", "+3"};
    public EMP_DATA Get_EMP_DATA;
    public boolean isDleteData;
    public class EMP_DATA {
        public boolean isExist;
        public String SN;
        public String effective;
        public String TmDay;
        public String TmMon;
        public String TmYear;
        public String LEU;
        public String TmMinute;
        public String TmHour;
        public String BLD;
        public String PH;
        public String PRO;
        public String UBG;
        public String NIT;
        public String PF;
        public String VC;
        public String GLU;
        public String BIL;
        public String KET;
        public String SG;

        public EMP_DATA(byte[] data, boolean isExist) {
            // TODO Auto-generated constructor stub
            this.isExist = isExist;
            if(!isExist) return;
            SN = String.valueOf(data[0] & 0x3) + String.valueOf(data[1]);
            effective = String.valueOf(data[2] & 0xf) + String.valueOf(data[3]);
            TmDay = String.valueOf((data[4] & 0xff) >> 3);
            TmMon = String.valueOf((data[4] & 0x7) << 1 | (data[5] >> 7) & 0x1);
            TmYear = String.valueOf(data[5] & 0x7F);
            LEU = LEUS[data[6] >> 3 & 0x7];
            TmMinute = String.valueOf((data[6] & 0x7) << 3 | (data[7] >> 5) & 0x1);
            TmHour = String.valueOf(data[7] & 0x1F);
            BLD = BLDS[(data[8] >> 4) & 0x7];
            PH = PHS[(data[8] >> 1) & 0x7];
            PRO = PROS[(data[8] & 0x1) << 2 | data[9] >> 6];
            UBG = UBGS[(data[9] >> 3) & 0x7];
            NIT = NITS[data[9] & 0x7];
            PF = String.valueOf(data[10] >> 7);
            VC = VCS[(data[10] >> 4) & 0x7];
            GLU = GLUS[(data[10] >> 1) & 0x7];
            BIL = BILS[(data[10] & 0x1) << 2 | data[11] >> 6];
            KET = KETS[(data[11] >> 3) & 0x7];
            SG = SGS[data[11] & 0x7];
        }

    }

    private boolean isFindStartByte = false;    //是否找到头
    private int one_data_num = 0;
    private List<byte[]> array_recv_cmd = new ArrayList<byte[]>();
    private List<Byte> array_recv_byte = new ArrayList<Byte>();
    boolean find_len = true;
    int len = 0, nowlen = 0;

    public ParsingEmp() {
        // TODO Auto-generated constructor stub
    }

    public boolean run(byte[] bDatas) {
        isDleteData=false;
        Get_EMP_DATA = null;
        EorrCode = null;
        array_recv_cmd.clear();
        if (bDatas == null) return false;
        dataItem(bDatas);
        for (int i = 0; i < array_recv_cmd.size(); i++) {
//            byte[] buffer = array_recv_cmd.get(i);
//            String ss="";
//            for (int i1 = 0; i1 < buffer.length; i1++) {
//                ss += String.format("%X ", buffer[i1]);
//            }
//            Log.i("MyBlueTest", ss);
            if (isCheckCmd(array_recv_cmd.get(i))) {
                parsingCMD(array_recv_cmd.get(i));

            }

        }
        return true;
    }

    private void dataItem(byte[] bDatas) {
        for (int i = 0; i < bDatas.length; i++) {
            if (!isFindStartByte) {
                if (bDatas[i] == (byte) 0x93) {
                    one_data_num++;
                } else if (bDatas[i] == (byte) 0x8e && one_data_num > 0) {
                    isFindStartByte = true;
                    array_recv_byte.add((byte) 0x93);
                    array_recv_byte.add((byte) 0x8e);
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

                        if (buffer[5] == 0x07)        //返回错误代码指令
                        {
                            EorrCode = String.valueOf(buffer[6]);
                            Log.i("MyBlueTest", "EorrCode  " + EorrCode);
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

        if (buffer[5] == 0x04)        //单条数据传输指令
        {
            int num = 0;
            byte[] data = new byte[buffer.length - 6];
            boolean isExist = false;
            boolean isTest = true;
            for (int i = 6; i < buffer.length - 1; i++) {
                if (isTest) {
                    if (buffer[i] != (byte) 0xff) {
                        isExist = true;
                    }
                }
                if (isExist) {
                    isTest = false;
                }

                data[num] = buffer[i];
                num++;
            }
        /*	String SN =String.valueOf(data[0]&0x3) + String.valueOf(data[1]);
			String effective =String.valueOf(data[2]&0xf)+String.valueOf(data[3]);
			String TmDay =String.valueOf((data[4]&0xff)>>3);
			String TmMon = String.valueOf((data[4]&0x7)<<1 | (data[5]>>7)&0x1);
			String TmYear = String.valueOf(data[5]&0x7F);
			String LEU = LEUS[data[6]>>3&0x7];
			String TmMinute = String.valueOf((data[6]&0x7)<<3 | (data[7]>>5)&0x1);
			String TmHour = String.valueOf(data[7]&0x1F);
			String BLD = BLDS[(data[8]>>4)&0x7];
			String PH = PHS[(data[8]>>1) &0x7];
			String PRO = PROS[(data[8]&0x1)<<2 | data[9]>>6];
			String UBG = UBGS[(data[9]>>3) &0x7];
			String NIT = NITS[data[9] &0x7];
			String PF = String.valueOf(data[10]>>7);
			String VC = VCS[(data[10]>>4) & 0x7];
			String GLU = GLUS[(data[10]>>1) & 0x7];
			String BIL = BILS[(data[10]&0x1)<<2 | data[11]>>6];
			String KET = KETS[(data[11]>>3) &0x7];
			String SG = SGS[data[11] &0x7];*/
            Get_EMP_DATA = new EMP_DATA(data, isExist);
		/*	Log.i("MyBlueTest","SG  " + Get_EMP_DATA.SG);
			Log.i("MyBlueTest","TmYear  " + Get_EMP_DATA.TmYear+Get_EMP_DATA.TmMon+Get_EMP_DATA.TmDay+Get_EMP_DATA.TmHour+" "+Get_EMP_DATA.TmMinute);
			Log.i("MyBlueTest","BLD  " + Get_EMP_DATA.BLD);*/
        }
       /* if (buffer[5] == 0x07)        //返回错误代码指令
        {
            EorrCode = String.valueOf(buffer[6]);
            Log.i("MyBlueTest", "EorrCode  " + EorrCode);
        }*/
        if (buffer[5] == 0x06)        //删除数据
        {
            isDleteData=true;
        }

    }
}
