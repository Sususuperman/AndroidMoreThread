package event.superman.com.androidmorethread;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AsyncTaskTestActivity extends AppCompatActivity {
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_test);
        tv=findViewById(R.id.tv);
        new MyTask(new MyTask.CallbackListener() {
            @Override
            public void updateProgress(int progress) {
                tv.setText("当前进度为："+progress);
            }

            @Override
            public void updateCompelete(String result) {
                tv.setText(result);
            }

        }).execute();
    }

}
