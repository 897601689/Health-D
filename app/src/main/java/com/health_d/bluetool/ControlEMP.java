package com.health_d.bluetool;

import java.io.IOException;


/**
 * Created by Admin on 2016/1/12.
 */
public class ControlEMP {
    final public static String FIND_CMD = "93 8e 04 00 08 04 10";
    final public static String CONNECT_CMD = "93 8e 08 00 08 01 43 4f 4e 54 45";
    final public static String DELETE_CMD = "93 8E 06 00 08 06 00 00 14";
    private BlueTool tool;
    private ParsingEmp parsingemp;
    private boolean isConnect = false;
    private int errornum=0;
    public ControlEMP(BlueTool tool) {
        this.tool = tool;
        parsingemp = new ParsingEmp();
    }

    public ParsingEmp.EMP_DATA getEMP_DATA() {
        return parsingemp.Get_EMP_DATA;
    }

    public String getErrorCode() {
        return parsingemp.EorrCode;
    }

    public boolean IsDeleteSuccess(){ return parsingemp.isDleteData;}

    public boolean isFindDev() {
        return tool.isFindDev(ParsingEmp.DEV_NAME);
    }

    public boolean Connect() {
        if (!isConnect) {
            DisConnect();
            tool.OperateBlueName = ParsingEmp.DEV_NAME;
            isConnect = tool.Connect();
        }
        return isConnect;
    }

    public void DisConnect() {
        tool.DisConnect();
    }

    public void SendMessage(String data) {
        tool.SendMessage(data);
    }

    public void Run() {
        //Log.i("MyBlueTest", "发送成功");
        try {
            Thread.sleep(1000);
            byte[] buffer = tool.RecvMessage(false);
            isConnect = parsingemp.run(buffer);

            if (!isConnect) {
            	errornum++;
                isConnect=true;
				if(errornum>10)
				{
					isConnect=false;
					errornum=0;
				}
            } else {
                String ss = "";
                if(buffer==null) return;
//                for (int i = 0; i < buffer.length; i++) {
//                    ss += String.format("%X ", buffer[i]);
//                }
//                Log.i("MyBlueTest", ""+ss);
            }




        } catch (IOException e) {

        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }
}
