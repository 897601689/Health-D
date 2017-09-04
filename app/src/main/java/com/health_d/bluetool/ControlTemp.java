package com.health_d.bluetool;

import java.io.IOException;
import java.util.List;

/**
 * Created by Admin on 2016/1/12.
 */
public class ControlTemp {
    final public static String CONNECT_CMD = "FE FD AA A0 0D 0A";
    private BlueTool tool;
    private ParsingTemp parsingtemp;
    private boolean isConnect = false;
    private int errornum = 0;

    public ControlTemp(BlueTool tool) {
        this.tool = tool;
        parsingtemp = new ParsingTemp();
    }

    public String getTemp() {
        return parsingtemp.TempValue;
    }

    public List<String> getErrorCode() {
        return parsingtemp.ErrorCode;
    }

    public boolean isFindDev() {
        return tool.isFindDev(ParsingTemp.DEV_NAME);
    }

    public boolean Connect() {
        if (!isConnect) {
            DisConnect();
            tool.OperateBlueName = ParsingTemp.DEV_NAME;
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
        try {
            Thread.sleep(1000);
            byte[] buffer = tool.RecvMessage(false);
            isConnect = parsingtemp.run(buffer);

            if (!isConnect) {
                errornum++;
                isConnect = true;
                if (errornum > 8) {
                    isConnect = false;
                    errornum = 0;
                }
            } else {
                String ss = "";
                for (int i = 0; i < buffer.length; i++) {
                    ss += String.format("%X ", buffer[i]);
                }
                //Log.i("MyBlueTest", ss);
            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
