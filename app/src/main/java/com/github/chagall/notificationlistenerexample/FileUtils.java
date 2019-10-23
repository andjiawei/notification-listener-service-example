package com.github.chagall.notificationlistenerexample;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by jiawei on 2019/9/2.
 */

public class FileUtils {

    /**
     * 字符串保存到手机内存设备中
     *
     * @param str
     */
    public static void saveFile(String str, String fileName) {
        // 创建String对象保存文件名路径
        try {
            // 创建指定路径的文件
//            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            //Environment.getDataDirectory().getParentFile().getAbsolutePath()
            File file = new File(Environment.getDataDirectory().getParentFile().getAbsolutePath(), fileName);
            // 如果文件不存在
            if (file.exists()) {
                // 创建新的空文件
                file.delete();
            }
            file.createNewFile();
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write(str.getBytes());
            // 最后关闭文件输出流
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 删除已存储的文件
     */
    public static boolean deletefile() {
        try {
            // 找到文件所在的路径并删除该文件
            File file = new File(Environment.getExternalStoragePublicDirectory("") + "/00localMessage/", "wechatMsg.txt");
              return file.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 读取文件里面的内容
     *
     * @return
     */
    public static String getFile() {
        try {
            // 创建文件
            String path = Environment.getExternalStoragePublicDirectory("") + "/00localMessage/";
            String fileName = "applist.txt";
            File file = new File(path,fileName);

            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 创建FileInputStream对象
            FileInputStream fis = new FileInputStream(file);
            // 创建字节数组 每次缓冲1M
            byte[] b = new byte[1024];
            int len = 0;// 一次读取1024字节大小，没有数据后返回-1.
            // 创建ByteArrayOutputStream对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 一次读取1024个字节，然后往字符输出流中写读取的字节数
            while ((len = fis.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            // 将读取的字节总数生成字节数组
            byte[] data = baos.toByteArray();
            // 关闭字节输出流
            baos.close();
            // 关闭文件输入流
            fis.close();
            // 返回字符串对象
            return new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void mCreatFile(String message,String time) {
        try {
            //获取手机本身存储根目录Environment.getExternalStoragePublicDirectory("")
            //sd卡根目录Environment.getExternalStorageDirectory()
            // new File(Environment.getExternalStorageDirectory(),"a.txt")
            String path = Environment.getExternalStoragePublicDirectory("") + "/00localMessage/";
//            String path = Environment.getExternalStorageDirectory() + "/222myprint/";
            String fileName = "wechatMsg.txt";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            //第三个参数：真，后续内容被追加到文件末尾处，反之则替换掉文件全部内容
            FileWriter fw = new FileWriter(path + fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
//            bw.append("在已有的基础上添加字符串");
//            bw.write();
            bw.write(time+"\r\n");
            bw.write(message+"\r\n");// 往已有的文件上添加字符串
            bw.write(" \r\n");
            bw.close();
            fw.close();
            Log.e("保存","保存成功："+message);

        } catch (Exception e) {
            Log.e("保存","保存失败");
            e.printStackTrace();
        }
    }

}
