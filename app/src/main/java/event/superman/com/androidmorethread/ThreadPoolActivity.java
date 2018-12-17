package event.superman.com.androidmorethread;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1)
            Toast.makeText(ThreadPoolActivity.this,"吐司",Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(100);
    }

    /**
     *数量固定并且都是核心线程的线程池。线程空闲时不会被回收，除非线程关闭，没有超时机制，没有任务大小限制。
     * @return
     */
    private ExecutorService newFixedThreadPool(){
        return Executors.newFixedThreadPool(3);//长度为3的线程池。
    }

    /**
     *可缓存线程池，数量不确定。最大线程数是Integer.MAX_VALUE。有空闲线程就使用空闲线程来处理新任务，没有就直接创建新线程。超时时间是60s，超过
     * 60s就会被回收。
     * @return
     */
    private ExecutorService newCachedThreadPool(){
        return Executors.newCachedThreadPool();
    }

    /**
     *从字面意思可知道：只有一个核心线程，所有的任务都在这一个核心线程中按顺序执行。SingleThreadExecutor 的意义在于统一所有的外界任务到一个线程中，
     * 这使得这些任务之间不需要处理线程同步的问题。
     */
    private ExecutorService newSingleThreadPool(){
        return Executors.newSingleThreadExecutor();
    }

    /**
     * 固定的核心线程数，非核心线程数无限大。当非核心线程闲置的时候，立即被回收。
     * ScheduledThreadPool也是四个当中唯一一个具有定时定期执行任务功能的线程池。它适合执行一些周期性任务或者延时任务。
     * @return
     */
    private ScheduledExecutorService newScheduledThreadPool(){
        return Executors.newScheduledThreadPool(3);
    }

    public void onScheduled(View view) {
        ScheduledExecutorService service = newScheduledThreadPool();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        service.schedule(runnable,1, TimeUnit.SECONDS);
    }

    public void onScheduled2(View view) {
        ScheduledExecutorService service = newScheduledThreadPool();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        };
        service.scheduleAtFixedRate(runnable,3,1, TimeUnit.SECONDS);
    }

    public void onUpdate(View view) {
        ExecutorService service = newCachedThreadPool();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.e("TAG","pb:"+Thread.currentThread().getName());
                while (progressBar.getProgress()<progressBar.getMax()){
                    progressBar.setProgress(progressBar.getProgress()+5);//progressbar,seekbar等可以直接在子线程中更新ui进度，内部做了处理。
                }
                Looper.prepare();//子线程中调用toast，toast内部默认会创建一个handler，调用当前线程的looper，所以缺少looper，直接调用会报一个
                                    // ava.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
                //  所以需要实例化一个looper对象。
                Toast.makeText(ThreadPoolActivity.this,"吐司",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        };
        service.execute(runnable);
    }
}
