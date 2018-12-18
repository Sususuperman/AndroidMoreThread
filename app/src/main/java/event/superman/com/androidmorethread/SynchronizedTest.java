package event.superman.com.androidmorethread;

import android.util.Log;

/**
 * 作者 Superman
 * 日期 2018/12/18 11:35.
 * 文件 AndroidMoreThread
 * 描述
 */

public class SynchronizedTest {
    private static String TAG ="SynchronizedTest";

    /**
     * m1和m2方法等同。
     * @param s
     */
    public synchronized void m1(String s){
        Log.e(TAG,"锁方法 1 "+s);}
    public void m2(String s){
        synchronized (this){
            Log.e(TAG,"锁方法 2 "+s);
        }
           }
/*************************************/
    /**
     * m3和m4方法等同。
     * @param s
     */
    public synchronized static void m3(String s){
        Log.e(TAG,"锁方法 3"+s);
    }

    public static void m4(String s){
        Log.e(TAG,"锁方法 4"+s);
    }

}
