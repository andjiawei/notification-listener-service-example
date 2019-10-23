package com.github.chagall.notificationlistenerexample;

import android.os.Handler;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by jiawei on 2019/9/17.
 */

public class NetUtils {

//    POST /WebServices/MobileCodeWS.asmx HTTP/1.1
//    Host: ws.webxml.com.cn
//    Content-Type: text/xml; charset=utf-8
//    Content-Length: length
//    SOAPAction: "http://WebXml.com.cn/getMobileCodeInfo"
//
//    <?xml version="1.0" encoding="utf-8"?>
//<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
//  <soap:Body>
//    <getMobileCodeInfo xmlns="http://WebXml.com.cn/">
//      <mobileCode>string</mobileCode>
//      <userID>string</userID>
//    </getMobileCodeInfo>
//  </soap:Body>
//</soap:Envelope>

    public static void startSend(final String msg, final String pkg) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 命名空间
//                String nameSpace = "http://wangjiansk.gicp.net/";
//                String nameSpace = "http://WebXml.com.cn/";
                String nameSpace = "http://tempuri.org/";
                // 调用方法的名称
                String methodName = "GetMsg";
                // EndPoint
//               String endPoint = "http://wangjiansk.gicp.net/pmservice.asmx";
//                String endPoint = "http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx";
                String endPoint = "http://my.xun3.com/msgservice.asmx";


                // SOAP Action
//                String soapAction = "http://wangjiansk.gicp.net/pmservice.asmx/LoginState"
//                String soapAction = "http://WebXml.com.cn/getDatabaseInfo";
                String soapAction = "http://tempuri.org/GetMsg";

                // 指定WebService的命名空间和调用方法
                SoapObject soapObject = new SoapObject(nameSpace, methodName);
                soapObject.addProperty("userName", "huawei");
                soapObject.addProperty("pwd", "a123456789");
//                String newMsg = msg;
                soapObject.addProperty("msg", msg);
                soapObject.addProperty("appId", pkg);
                // 设置需要调用WebService接口的两个参数

//                soapObject.addProperty("mobileCode", mobileCode);
//                soapObject.addProperty("userID", userID);
                // 生成调用WebService方法调用的soap信息，并且指定Soap版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER12);
                envelope.bodyOut = soapObject;
                // 是否调用DotNet开发的WebService
                envelope.dotNet = true;

                envelope.setOutputSoapObject(soapObject);

                HttpTransportSE transport = new HttpTransportSE(endPoint);

                try {
                    Log.e("test","开发发送"+msg);
                    transport.call(soapAction, envelope);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//            // 获取返回的数据


//                SoapFault error = (SoapFault)envelope.bodyIn;
//                System.out.println("Error message : "+error.toString());
//                Log.e("test", error.toString());
//                SoapObject object = (SoapObject) envelope.bodyIn;
                SoapPrimitive object=null;
                try {
                    object = (SoapPrimitive)envelope.getResponse();

                } catch (SoapFault soapFault) {
                    soapFault.printStackTrace();
                }

                try{
                    Log.e("test",object.toString());
                }catch (Exception ex){
                    ex.printStackTrace();
                }


//                for (int i = 0; i < object.getPropertyCount(); i++) {
//                SoapObject child = (SoapObject) object.getProperty(i);
//                Log.e("test", child.getProperty(0).toString() );
//                Log.e("test", child.getProperty(1).toString() );
//            }
//            Message message = handler.obtainMessage();
//            message.obj = XX;
//            handler.sendMessage(message);
            }
        }).start();
    }

//    private Handler handler = new Handler() {
//        public void handleMessage(android.os.Message msg1) {
//            ToastUtils.showLong(msg);
//        }
//    };


}
