package com.health_d.bluetool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2016/1/12.
 */
public class ParsingTemp {
    final public static String DEV_NAME = "Bluetooth BP";
    public String TempValue;
    public List<String> ErrorCode;
    private boolean isFindStartByte = false;    //是否找到头

    private int one_data_num = 0;

    private List<byte[]> array_recv_cmd = new ArrayList<byte[]>();
    private List<Byte> array_recv_byte = new ArrayList<Byte>();
    boolean find_len = true;
    int len = 0, nowlen = 0;

    public ParsingTemp() {
        ErrorCode = new ArrayList<String>();
    }

    public boolean run(byte[] bDatas) {
        TempValue = null;
        ErrorCode.clear();
        array_recv_cmd.clear();
        if (bDatas == null) return false;
        dataItem(bDatas);

        for (int i = 0; i < array_recv_cmd.size(); i++) {
            if (isCheckCmd(array_recv_cmd.get(i))) {
                parsingCMD(array_recv_cmd.get(i));
              //  byte[] buffer = new byte[]{(byte)0xfe,(byte)0xfd,(byte)0x15,(byte)0x0,(byte)0x5,(byte)0xb,0xd,0xa};
              //  parsingCMD(buffer);
            }
        }
        return true;
    }

    private void dataItem(byte[] bDatas) {
        for (int i = 0; i < bDatas.length; i++) {

            if (!isFindStartByte) {
                if (bDatas[i] == (byte) 0xFE) {
                    one_data_num++;
                } else if (bDatas[i] == (byte) 0xFD && one_data_num > 0) {
                    isFindStartByte = true;
                    array_recv_byte.add((byte) 0xFE);
                    array_recv_byte.add((byte) 0xFD);
                    one_data_num = 0;
                } else {
                    one_data_num = 0;
                }
            } else {
                if (bDatas[i] == (byte) 0xD) {
                    one_data_num++;
                } else if (bDatas[i] == (byte) 0xA && one_data_num > 0) {

                    one_data_num = 0;
                    byte[] buffer = new byte[array_recv_byte.size()];
                    for (int num = 0; num < buffer.length; num++) {
                        buffer[num] = array_recv_byte.get(num);
                    }

                    array_recv_cmd.add(buffer);
                    array_recv_byte.clear();
                    isFindStartByte = false;
                } else {
                    array_recv_byte.add(bDatas[i]);
                    one_data_num = 0;
                }
            }
        }
    }

    private boolean isCheckCmd(byte[] buffer) {
        return true;
/*		if(buffer[0]== (byte)0xFE && buffer[1]== (byte)0xFD && buffer[buffer.length-2]== (byte)0xD && buffer[buffer.length-1]== (byte)0xA)
        {
			return true;
		}
		else
		{
			return false;
		}*/
    }


    private void parsingCMD(byte[] buffer) {

        if (buffer[2] == (byte) 0x1a) {
            if (buffer[3] == (byte) 0x00) {
              //  float num = (float) ((buffer[4] * 256 + buffer[5]) / 10.0);
                int num1 = buffer[4];
                if(num1<0)
                {
                    num1 = 256+num1;
                }
                num1 = num1*256;
                int num2 = buffer[5];
                if(num2<0)
                {
                    num2 = 256+num2;
                }
                int num = num1+num2;
                float fnum = (float) (num / 10.0);
                TempValue = "实际温度:" + fnum + "℃";
            }

            if (buffer[3] == (byte) 0x01) {
                int num1 = buffer[4];
                if(num1<0)
                {
                    num1 = 256+num1;
                }
                num1 = num1*256;
                int num2 = buffer[5];
                if(num2<0)
                {
                    num2 = 256+num2;
                }
                int num = num1+num2;
                float fnum = (float) (num / 10.0);
                TempValue = "体温:" + fnum + "℃";
            }

            if (buffer[3] == (byte) 0xAA) {
                if (buffer[4] == (byte) 0x55 && buffer[5] == (byte) 0x5f) {

                }
            }

            if (buffer[3] == (byte) 0x81) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x01) {
                    String error = "人体模式：量测温度过高";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x82) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x02) {
                    String error = "人体模式：量测温度过低";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x83) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x03) {
                    String error = "环境温度过高";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x84) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x04) {
                    String error = "环境温度过低";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x85) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x05) {
                    String error = "硬件错误";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x86) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x06) {
                    String error = "低电压";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x87) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x07) {
                    String error = "物体模式：量测温度过高";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x88) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x08) {
                    String error = "物体模式：量测温度过低";
                    ErrorCode.add(error);
                }
            }

        } else if (buffer[2] == (byte) 0x15) {
            if (buffer[3] == (byte) 0x00) {
             //   int num = buffer[4] * 256 + buffer[5] + 256;
             //   float fnum = (float) (num / 10.0);
                int num1 = buffer[4];
                if(num1<0)
                {
                    num1 = 256+num1;
                }
                num1 = num1*256;
                int num2 = buffer[5];
                if(num2<0)
                {
                    num2 = 256+num2;
                }
                int num = num1+num2;
                float fnum = (float) (num / 10.0);
                //TempValue = "实际温度"+num+ " "+ fnum+ "F";
                TempValue = "实际温度:" + fnum + "℉";
            }

            if (buffer[3] == (byte) 0x01) {
              //  int num = buffer[4] * 256 + buffer[5] + 256;
              //  float fnum = (float) (num / 10.0);
                int num1 = buffer[4];
                if(num1<0)
                {
                    num1 = 256+num1;
                }
                num1 = num1*256;
                int num2 = buffer[5];
                if(num2<0)
                {
                    num2 = 256+num2;
                }
                int num = num1+num2;
                float fnum = (float) (num / 10.0);
                //TempValue = "体温"+num+ " "+ fnum+ "F";
                TempValue = "体温:" + fnum + "℉";
            }

            if (buffer[3] == (byte) 0x81) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x01) {
                    String error = "人体模式：量测温度过高";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x82) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x02) {
                    String error = "人体模式：量测温度过低";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x83) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x03) {
                    String error = "环境温度过高";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x84) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x04) {
                    String error = "环境温度过低";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x85) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x05) {
                    String error = "硬件错误";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x86) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x06) {
                    String error = "低电压";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x87) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x07) {
                    String error = "物体模式：量测温度过高";
                    ErrorCode.add(error);
                }
            }

            if (buffer[3] == (byte) 0x88) {
                if (buffer[4] == (byte) 0x00 && buffer[5] == (byte) 0x08) {
                    String error = "物体模式：量测温度过低";
                    ErrorCode.add(error);
                }
            }
        }
    }
}
