package event.superman.com.androidmorethread;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class IntentServiceActivity extends AppCompatActivity {
    Handler mUiHandler=new Handler(){//这里handler也可以通过广播的形式来接收。
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(IntentServiceActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
        }
    };
    private String toasts[] = {
            "toast测试1",
            "toast测试2",
            "toast测试3",
            "toast测试4",
            "toast测试5",
            "toast测试6"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_service);
        MyIntentService.setCallbackListener(new MyIntentService.CallbackListener() {
            @Override
            public void update(Message msg) {//必须通过Handler去更新，该方法为异步方法，不可更新UI
                Message message =Message.obtain();
                message.what = msg.what;
                message.obj = msg.obj;

                mUiHandler.sendMessageDelayed(message,1000);
            }
        });
        for (int i=0;i<toasts.length;i++){
            Intent intent = new Intent(this,MyIntentService.class);
            intent.putExtra(MyIntentService.TOAST_ACTION,toasts[i]);
            intent.putExtra(MyIntentService.INDEX,i);
            startService(intent);
        }
    }

    public void stop(View view) {
    }
}
