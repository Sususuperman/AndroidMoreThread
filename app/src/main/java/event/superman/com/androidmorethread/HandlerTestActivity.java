package event.superman.com.androidmorethread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HandlerTestActivity extends AppCompatActivity {
    TextView tv;

    private static final int update = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //接收消息并更新ui
            if (msg.what == update) {
//                Bundle bundle = msg.getData();
//                String num = bundle.getString("num");
//                tv.setText(num);
                tv.setText(msg.obj.toString());
            }
        }
    };

    @Override
    public Looper getMainLooper() {
        return super.getMainLooper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_test);
        tv = findViewById(R.id.tv);
        new Thread() {
            @Override
            public void run() {
                //起一个线程 通过handler发送消息 handler接收 更新ui 每0.3秒更新一次
                try {
                    for (int i = 0; i < 100; i++) {
                        Thread.sleep(300);
                        Message msg = new Message();
                        msg.obj = "更新textview的文字为" + i;
                        // Bundle b = new Bundle();
                        // b.putString("num", "更新textview的文字为：" + i);
                        // msg.setData(b); 也可以放bundle
                        msg.what = update;
                        handler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
