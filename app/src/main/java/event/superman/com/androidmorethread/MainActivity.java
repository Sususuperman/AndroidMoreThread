package event.superman.com.androidmorethread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/***
 * Android多线程学习与练习
 */
public class MainActivity extends AppCompatActivity {
    Handler handler;
    HandlerThread handlerThread = new HandlerThread("test");

    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //打印UI线程的名称
        System.out.println("onCreate  CurrentThread = " + Thread.currentThread().getName());
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                //打印线程的名称
                System.out.println(" handleMessage CurrentThread = " + Thread.currentThread().getName());
                return false;
            }
        });
    }

    /**
     * 启动handler
     *
     * @param view
     */
    public void onStart(View view) {
        System.out.println("Start............");
        handler.post(runnable);
    }

    /**
     * 结束handler
     *
     * @param view
     */
    public void onEnd(View view) {
        System.out.println("End............");
        handler.removeCallbacks(runnable);

    }

    class MyHandlerThread extends HandlerThread implements Handler.Callback{

        public MyHandlerThread(String name) {
            super(name);
        }

        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    }
}
