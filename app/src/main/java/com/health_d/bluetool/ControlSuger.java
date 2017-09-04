package com.health_d.bluetool;

import android.util.Log;

import java.io.IOException;

/**
 * Created by Admin on 2016/1/12.
 */
public class ControlSuger {
    final public static String CONNECT_CMD = "53 4e 08 00 04 01 53 49 4e 4f 46";
    private BlueTool tool;
    private ParsingSugar parsingsugar;
    private boolean isConnect = false;
    private int errornum = 0;

    public ControlSuger(BlueTool tool) {
        this.tool = tool;
        parsingsugar = new ParsingSugar();
    }

    public String getResult() {
        return parsingsugar.ResultValue;
    }

    public String getErrorCode() {
        return parsingsugar.ErrorCode;
    }

    public boolean isConnect() {
        return parsingsugar.isConnect;
    }

    public ParsingSugar.SugerState getSugerState() {
        return parsingsugar.state;
    }

    public boolean isFindDev() {
        return tool.isFindDev(ParsingSugar.DEV_NAME);
    }

    public boolean Connect() {
        if (!isConnect) {
            DisConnect();
            tool.OperateBlueName = ParsingSugar.DEV_NAME;
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
        //SendMessage(CONNECT_CMD);
        try {
            Thread.sleep(1000);
            byte[] buffer = tool.RecvMessage(false);
            isConnect = parsingsugar.run(buffer);

            if (!isConnect) {
            /*	errornum++;
                isConnect=true;
				if(errornum>3)
				{
					isConnect=false;
					errornum=0;
				}*/
            } else {
                if(getSugerState()== ParsingSugar.SugerState.shoutdown)
                {
                    isConnect=false;
                }
                String ss = "";
                for (int i = 0; i < buffer.length; i++) {
                    ss += String.format("%X ", buffer[i]);
                }
                Log.i("MyBlueTest", ss);
            }
            //	if(buffer==null) return;
        } catch (IOException e) {

        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

}
