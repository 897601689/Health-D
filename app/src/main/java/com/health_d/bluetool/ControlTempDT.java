package com.health_d.bluetool;

import java.io.IOException;

/**
 * Created by Admin on 2016/8/12.
 */
public class ControlTempDT {
    public static String CONNECT_CMD = "FE FD AA A0 0D 0A";
    private BlueTool tool;
    private ParsingTempDT parsingtemp;
    private boolean isConnect = false;
    private int errornum = 0;

    public ControlTempDT(BlueTool tool) {
        this.tool = tool;
        parsingtemp = new ParsingTempDT();
    }

    public String getTemp() {
        return parsingtemp.TempValue;
    }



    public boolean isFindDev() {
        return tool.isFindDev(ParsingTempDT.DEV_NAME);
    }

    public boolean Connect() {
        if (!isConnect) {
            DisConnect();
            tool.OperateBlueName = ParsingTempDT.DEV_NAME;
            isConnect = tool.Connect();
        }
        return isConnect;
    }

    public void DisConnect() {
        isConnect = false;
        tool.DisConnect();
    }

    public void SendMessage(String data) {
        isConnect = tool.SendMessage(data);
    }

    public void Run() {
        try {
            Thread.sleep(1000);
            byte[] buffer=null;

            buffer = tool.RecvMessage(false);
            parsingtemp.run(buffer);
            if (!isConnect) {

            } else {
                if (buffer == null) return;
                String ss = "";
                for (int i = 0; i < buffer.length; i++) {
                    ss += String.format("%X ", buffer[i]);
                }
                //Log.i("MyBlueTest", ss);
            }
        }  catch (IOException e) {
            //e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
