package event.superman.com.androidmorethread;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * 作者 Superman
 * 日期 2018/12/12 16:50.
 * 文件 AndroidMoreThread
 * 描述 异步任务task String:url  Integer:进度 Bitmap:返回的
 */

public class MyTask extends AsyncTask<Integer,Integer,String>{
    private CallbackListener listener;
    public MyTask(CallbackListener listener){
        this.listener = listener;
    }
    /**
     * 可以不用重写
     * 在UI线程中调用，异步任务开始之前执行。
     * 一般用来在执行后台任务前对UI做一些标记和准备工作，
     * 如在界面上显示一个进度条。
     */
    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    /**
     * 必须进行重写
     * 在工作线程中调用，执行耗时操作。如网络请求等。
     * @param urls
     * @return
     */
    @Override
    protected String doInBackground(Integer... urls) {
        for(int i=0;i<100;i++){
            try {
                Thread.sleep(1000);
                publishProgress(i);//如果要显示更新进度，就必须调用这个方法。
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "计时完成";
    }

    /**
     * 可以不用重写
     * 在UI线程中调用，当异步任务进度发生变化的时候执行。
     * 我们必须在doInBackground方法中调用publishProgress()
     * 来设置进度变化的值
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        if(listener!=null){
            listener.updateProgress(values[0]);
        }
    }

    /**
     * 可以不用重写
     * 在UI线程中调用，当异步任务完成的时候执行
     * 一般用于更新UI或其他必须在主线程执行的操作,传递参数bitmap为
     * doInBackground方法中的返回值
     * @param aVoid
     */
    @Override
    protected void onPostExecute(String aVoid) {
        if(listener!=null){
            listener.updateCompelete(aVoid);
        }
    }

    /**
     * 可以不用重写
     * 在UI线程中调用，当异步任务被取消时,该方法将被执行
     * 要注意的是这个时onPostExecute将不会被执行
     */
    @Override
    protected void onCancelled() {
        listener.updateCancle();
    }

    public interface CallbackListener{
        void updateProgress(int progress);
        void updateCompelete(String result);
        void updateCancle();
    }
}
