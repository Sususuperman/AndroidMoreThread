package event.superman.com.androidmorethread;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 在android开发中synchronized主要有四种用法。
 * 第一是在方法声明时使用；
 * 第二是在对某一代码块时使用；
 * 第三是对某一对象使用；
 * 第四是对某一类使用。
 */
public class SynchronizedTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronized_test);
    }

    /*
     *方法声明时使用
     *
     *放在范围操作符（public等）之后，返回类型声明（void等）之前，线程获得的是成员锁，即一次只能有一个线程进入该方法，其他线程
     * 想在此时调用该方法，只能排队等候，当前线程（就是在synchronized方法内部的线程）执行完方法后，别的线程才能进入。
     */
    public synchronized void syncMethod1() {
    }

    /**
     * 对某一代码块使用
     */

    public int syncMethod2(Object n) {
        synchronized (n) {//锁就是n这个对象，谁拿到这个锁谁就能运行它所控制的那段代码。当有一个明确的对象作为锁时，可以这样写程序，
                            //没有明确的对象作为锁，只是想让一段代码同步时，能创建一个instance变量（）来充当锁

        }
        return (int) n;
    }

    /**
     * 修饰静态方法
     *
     * 我们知道 静态方法是属于类的而不属于对象的 。
     * 同样的， synchronized修饰的静态方法锁定的是这个类的所有对象，所有类用它都会有锁的效果
     */
    public synchronized static void syncMethod3(){
        //todo
    }

    /**
     * 修饰一个类
     * 其作用的范围是synchronized后面括号括起来的部分，作用的对象是这个类的所有对象，
     * 只要是这个类型的class不管有几个对象都会起作用。如下代码
     */
    class ClassName{
        void method(){
            synchronized (ClassName.class){
                //todo
            }
        }
    }
}
