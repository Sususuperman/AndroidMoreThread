package event.superman.com.androidmorethread;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AsyncTaskTestActivity extends AppCompatActivity {
    TextView tv;
    Button cancle;
   static AsyncTask task;//若AsyncTask被声明为Activity的非静态内部类，当Activity需销毁时，
    // 会因AsyncTask保留对Activity的引用 而导致Activity无法被回收，最终引起内存泄露
    //AsyncTask应被声明为Activity的静态内部类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_test);
        tv=findViewById(R.id.tv);
        cancle = findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel(true);
            }
        });

       task= new MyTask(new MyTask.CallbackListener() {
            @Override
            public void updateProgress(int progress) {
                tv.setText("当前进度为："+progress);
            }

            @Override
            public void updateCompelete(String result) {
                tv.setText(result);
            }

           @Override
           public void updateCancle() {
               tv.setText("取消");
           }

       }).execute();

    }
}
