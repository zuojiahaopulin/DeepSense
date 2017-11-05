package com.lanytek.deepsensev3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import android.util.Log;

import com.buptant.deepeye.Constants;import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by JiaoJiao on 2017/8/19.
 */

public class SocketRec {
    private String TAG = "socket";
    private ServerSocket socket;
    private Socket clientSocket;
    private InputStream recvStream;
    private Handler mHandler;
    public static String filename = "deepeye_log";
    final int JSON_VALUE_OK = 1;
    final int JSON_REQUEST_STREAMING = 1;
    final int JSON_REQUEST_DISCONNECT = 9;

    public SocketRec(Handler handler){

        new initSocket().start();
    }

    private class initSocket extends Thread{  //initsocket链接
        @Override
        public void run() {
            super.run();
                try {
                    socket = new ServerSocket(1050);
                    Log.d(TAG, "客户端连接成功");
                    clientSocket = socket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            new RecvThread().start();
            }
        }

    public class RecvThread extends Thread {
        @Override
        public void run() {
            int i =0;
            while(true) {
                int request = -1;
                boolean run = true;
                while(run){
                    InputStream recvStream = null;
                    try {
                        recvStream = clientSocket.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] buf = new byte[1024];
                    int bytes = -1;
                    try {
                        bytes = recvStream.read(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObj = new JSONObject();
                    try {
                        jsonObj = new JSONObject(new String(buf, 0, bytes));
                        request =jsonObj.getInt("request");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (StringIndexOutOfBoundsException e){

                        e.printStackTrace();}
                    switch (request){
                        case JSON_REQUEST_STREAMING:
                            InputStream in = null;
                            try {
                                in = clientSocket.getInputStream();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int l =0;
                            buf = new byte[1024];
                            bytes = 0;
                            try {
                                bytes = in.read(buf);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            JSONObject bj = new JSONObject();
                            try{
                                bj = new JSONObject(new String(buf, 0, bytes));
                                System.out.println(bj.toString());
                                l =bj.getInt("bytes");
                                Log.d("jj", jsonObj.toString());}
                            catch(JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                sendAcknowledgement(clientSocket,1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            InputStream in1 = null;
                            try {
                                in1 = clientSocket.getInputStream();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                            // FileOutputStream outStream = new FileOutputStream("server.bmp");
                            byte[] buffer = new byte[1024];
                            byte[] data =new byte[1024];
                            int len =0;
                            int offset = 0;
                            try {
                                while ((len=in1.read(buffer))>0){
                                    outStream.write(buffer,0,len);
                                    outStream.flush();
                                    offset += len;
                                    //Log.d("jj", String.valueOf(len));
                                    if (offset >= l) {
                                        break;
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                outStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // Log.d("jj", "据流");
                            data = outStream.toByteArray();
                            Log.d("jj", outStream.toString());
                            try {
                                sendAcknowledgement(clientSocket,1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
//                Log.d("jj", bitmap.toString());
                            Message msg = mHandler.obtainMessage();
                            msg.what = Constants.MESSAGE_IMAGE_RECEIVED;
                            msg.obj = bitmap;
                            mHandler.sendMessage(msg);
                            break;
                        case JSON_REQUEST_DISCONNECT:
                            run = false;
                            break;
                        default:
                            System.out.println("No request received");
                            break;
                    }
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

         /*       if (bitmap !=null && !bitmap.isRecycled()){
                    bitmap.recycle();
                }*/
                //      byte[] data = outStream.toByteArray();


      /*          try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                //    in1.close();

            }
         /*
           //         data = outStream.toByteArray();
                //     data = readStream(dis);
                   // readStream(dis);
                 //   Log.i("jj", String.valueOf(data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
             //   if (data!=null){
                   Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                   // BitmapFactory.Options opt = new BitmapFactory.Options();
                  //  opt.inJustDecodeBounds = false;

               //     Bitmap bitmap =  byteToBitmap(data);

              /*      Message msg = mHandler.obtainMessage();
                    msg.what = Constants.MESSAGE_IMAGE_RECEIVED;
             //       msg.obj = bitmap;
                    mHandler.sendMessage(msg);
                    try {
                        recvStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }  */

            //    }
//                Bitmap img = BitmapFactory.decodeStream(recvStream);

                //          try {                recvStream.close();
                //        } catch (IOException e) {
                //             e.printStackTrace();
                //        }
//                Message msg = mHandler.obtainMessage();
////                Message msg = new Message();
//                msg.what = Constants.MESSAGE_IMAGE_RECEIVED;
//                msg.obj = img;
//                mHandler.sendMessage(msg);






    public static void sendAcknowledgement(Socket s, int jSON_VALUE_OK) throws JSONException, IOException {
        // TODO Auto-generated method stub
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("acknowledgement", jSON_VALUE_OK);
        } catch (org.json.JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        OutputStream out = s.getOutputStream();
        out.write(jobj.toString().getBytes());
        out.flush();
        Log.d("jj", jobj.toString());
       // out.close();

    }}
 /*   public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      // FileOutputStream outStream = new FileOutputStream("server.bmp");
        byte[] buffer = new byte[1024];
        int len =0;
        int offset = 0;
        while ((len=inStream.read(buffer))!=-1){
            outStream.write(buffer,0,len);
            outStream.flush();
            //Log.d("jj", String.valueOf(len));
            if (offset >= l) {
                break;
            }
        }
         Log.d("jj", "据流");
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return  data;

    }*/



