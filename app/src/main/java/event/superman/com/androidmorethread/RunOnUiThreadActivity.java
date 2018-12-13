package event.superman.com.androidmorethread;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RunOnUiThreadActivity extends AppCompatActivity {
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_on_ui_thread);
        btn = findViewById(R.id.btn);

        btn.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RunOnUiThreadActivity.this, "button高度:" + btn.getHeight() + "button宽度：" + btn.getWidth(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 1s后吐司
     *
     * @param view
     */
    public void onStart(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);//模仿耗时操作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {//程序首先会判断当前线程是否是UI线程，如果是就直接运行，如果不是则post，
                    // 这时其实质还是使用的Handler机制来处理线程与UI通讯。
                    @Override
                    public void run() {
                        Toast.makeText(RunOnUiThreadActivity.this, "执行runOnUiThread", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
