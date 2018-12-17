package event.superman.com.androidmorethread.demo;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import event.superman.com.androidmorethread.R;

/**
 * http://blog.csdn.net/lmj623565791/article/details/38476887 ，本文出自【张鸿洋的博客】
 * 异步消息处理机制，创建强大的图片加载类。
 */
public class ImageGridActivity extends AppCompatActivity {
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);
        gridView = findViewById(R.id.gridview);
        getImages();
    }
    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
    }
}
