package event.superman.com.androidmorethread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Random;

public class HandlerThreadTestActivity extends AppCompatActivity {
    String url = "https://img-blog.csdnimg.cn/20181108181055927.jpg";

    private String[] urls = {
            "https://img-blog.csdnimg.cn/20181108181055927.jpg",
            "https://img-blog.csdnimg.cn/20181116105000748.gif",
            "https://img-blog.csdnimg.cn/20181116111835634.gif",
            "https://img-blog.csdnimg.cn/20181114160021890.png",
            "https://img-blog.csdn.net/20180810100959827"
    };
    private ImageView imageView;
    //在主线程中创建一个handler
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            //更新image
            Toast.makeText(HandlerThreadTestActivity.this, msg.what + "", Toast.LENGTH_SHORT);
            imageView.setImageBitmap(bitmap);
        }
    };
    private Handler childHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_thread_test);
        imageView = findViewById(R.id.image);

        HandlerThread handlerThread = new HandlerThread("downloadImage");//传入参数用来标记当前线程的名字，可以是任意字符串。
        handlerThread.start(); //必须先开启线程

        //在子线程开启一个handler
        childHandler = new Handler(handlerThread.getLooper(), new ChildCallBack());
        childHandler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < urls.length; i++) {
                    childHandler.sendEmptyMessageDelayed(i, 1000*i);
                }
            }
        });
//        for (int i = 0; i < urls.length; i++) {
//            childHandler.sendEmptyMessageDelayed(i, 1000 * i);
//        }
    }

    /**
     * 该callback运行于子线程
     */
    class ChildCallBack implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {    //这个msg是还在消息队列中，正在使用，否则会报错“This message is already in use”这个错，可以重新new一个msg或者移除也行
            //在子线程中进行相应的网络请求
            Bitmap bitmap = downBitmap(urls[msg.what]);
            //通知主线程去更新UI
            Message msg1 = new Message();//new一个新的消息对象
            msg1.what = msg.what;
            msg1.obj = bitmap;
//            Message msg1 = childHandler.obtainMessage();//调用obtainMessage重新获取消息对象
            handler.sendMessage(msg1);
            return false;
        }
    }

    private Bitmap downBitmap(String url) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream inputStream = null;
        Bitmap bitmap = null;
        try {
            URL u = new URL(url);
            urlConnection = (HttpURLConnection) u.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();//及时关闭 避免内存泄漏
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
}
