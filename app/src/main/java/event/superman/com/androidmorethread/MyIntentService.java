package event.superman.com.androidmorethread;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.util.Log;

/**
 * 作者 Superman
 * 日期 2018/12/13 15:36.
 * 文件 AndroidMoreThread
 * 描述 四大组件activity和service是不能通过new来进行创建的。
 * 四大组件是系统管理的
 组件要“注册”给系统才会有作用
 你只有“注册”或“不注册”的权利。
 */

public class MyIntentService extends IntentService {
    public static final String TOAST_ACTION = "toast_action";
    public static final String INDEX = "index";
    public static CallbackListener listener;
    public MyIntentService() {
        super("MyIntentService");
    }

    /**
     * 实现异步任务的方法
     *
     * @param intent Activity传递过来的Intent,数据封装在intent中
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String toast = intent.getStringExtra(TOAST_ACTION);
        int index = intent.getIntExtra(INDEX,0);
        try {
            Thread.sleep(1000);
            Message message = Message.obtain();
            message.obj = toast+"。。经过耗时";
            message.what = index;
            //更新ui
            if(listener!=null){
                listener.update(message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public static void setCallbackListener(CallbackListener listener1){
        listener = listener1;
    }
    interface CallbackListener{
        void update(Message msg);
//        default void toast(){
//
//        }
    }

    Handler handler = new Handler(){};//与主线程looper绑定。在主线程中运行

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("TAG",handler.getLooper().getThread().getName());
    }
}
