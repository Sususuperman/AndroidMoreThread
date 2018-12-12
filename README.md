# AndroidMoreThread
android多线程的学习与练习
 1. **为什么需要多线程？**
 需要多线程的本质就是要进行异步处理，莫要让用户感觉到“很卡”。更不能出现ANR（Application Not Response）这种现象。
 2. **Android的多线程理解？**
 Android是单线程模型，Android的UI线程是非线程安全的，应用更新UI，是调用`invalidate()`方法来实现页面的重绘，而`invalidate()`是非线程安全的。（可参考博客[为什么说invalidate()不能直接在线程中调用](https://blog.csdn.net/hh2000/article/details/40213835)）所以，假如我们用非UI线程来更新UI的时候，可能会有其他的线程或者UI线程也在更新UI,导致页面的不同步。因此我们不能在非UI线程中去更新UI。因此要遵循两个原则：
 1.所有可能耗时的操作都放在工作线程中处理，不能在UI主线程中做耗时的操作（如网络请求，I/O流操作），不能阻塞UI主线程。
 2.只能UI主线程中做更新UI的操作。
 3. **Android中的UI线程的事件处理不能太耗时，否则后续的事件无法在5秒内得到响应，就会弹出ANR对话框。那么哪些方法会在 Main线程执行呢？**
 1.Activity的生命周期方法，例如：onCreate()、onStart()、onResume()等。
 2.事件处理方法，例如onClick()、onItemClick()等。
　通常Android基类中以on开头的方法是在UI线程被回调的。
 4. **多线程的核心机制？**
 多线程的核心就是线程之间的消息传递，本质就是Handler机制。
 创建Handler的时候要关联一个`Looper`对象，默认的构造方法中是关联的当前Thread的`Looper`对象。我们在UI Thread中创建一个Handler,那么此时就关联了UI Thread的Looper，在Work Thread中创建就关联的Work Thread的Looper。
```
 	public Handler() {
    mLooper = Looper.myLooper();}
```
Handler只是处理它所关联的Looper中的MessageQueue中的Message，至于它哪个线程的Looper，Handler并不是很关心！
 5. **实现多线程有哪几种方式？**
 1）**Handler+Thread**（参考博客：[Android多线程通信机制](https://www.cnblogs.com/WoodJim/p/4737171.html)，[Android--多线程之Handler](https://www.cnblogs.com/shirley-1019/p/3557800.html)）
 使用Handler，就是通过`Handler,Looper,MessageQueue,Message`来配合完成的。
 *类说明*：
 	**Handler**:
 	直接继承自Object,用于发送和处理`Message`对象或`Runnable`对象。Handler在创建的时候会与当前所在的线程的Looper对象相关联（如果当前线程的`Looper`为空或者不存在，会抛异常，那么就需要创建一个`Looper`对象，调用`Looper.prepare()`）。每个Handler具有一个单独的线程，并且关联到一个消息队列的线程，就是说一个Handler有一个固有的消息队列。Handler的主要作用就是将`Message`或`Runnable`对象压入消息队列，并且从消息队列（`MessageQueue`）中取出Message或者Runnable，从而操作它们。
 	***那么Handler是如何完成发送Message和Runnable对象的呢？***
 	1)`post`体系，通过post来把一个`Runnable`对象压入和发送到消息队列中去，方法体有：注意：run()方法执行在UI线程中。
 
    post(Runnable)//放入消息队列，在UI线程中取出并立即执行run()方法里的动作。
    postDelayed(Runnable,long)//放入消息队列，在UI线程中取出在并一定时间后执行run()方法里的动作。
    ,postAtTime(Runnable,long)//放入消息队列，在UI线程中取出并在特定时间执行run()方法里的动作。
	
	
2)`sendMessage，允许把一个包含消息数据的Message对象压入到消息队列中。它的方法有：

    sendEmptyMessage(int)//发送一个空的消息对象，下面的方法同post使用
    sendMessage(Message)、sendMessageAtTime(Message,long)、sendMessageDelayed(Message,long)。

**Message**
上面提到了Message消息对象。那么Message消息对象是怎么组成的呢？

    Message{
    int arg1;//如果我们只需要存储一些简单的Integer数据，则可通过设置这个属性来传递
    int agr2;//使用同arg1
    Object obj; //设置需要发送给接收方的对象,这个对象需要实现序列化接口
    int what; //描述这个消息的标识；
    //设置与这个消息对应的任意数据，这个数据是用Bundle封装的；
    void setData(Bundle data);
    Bundle getData(); 得到与这个消息对应的数据信息;
    ...}
当需要传递一些稍微复杂的数据的时候，通过封装成Bundle对象可以实现，然后通过setData(Bundle data)来进行传递，通过getData()来接收。
如下：
  
		
      				//发送消息对象
      				Message msg = new Message();
                            msg.obj = "更新textview的文字为" + i;
                            // Bundle b = new Bundle();
                            // b.putString("num", "更新textview的文字为：" + i);
                            // msg.setData(b); 也可以放bundle
                            msg.what = update;
                            handler.sendMessage(msg);

 	

     //接收消息并更新ui
                if (msg.what == update) {
    //                Bundle bundle = msg.getData();
    //                String num = bundle.getString("num");
    //                tv.setText(num);
                    tv.setText(msg.obj.toString());
                }
*注*：产生一个Message对象，可以new  ，也可以使用Message.obtain()方法；两者都可以，但是更建议使用obtain方法，因为Message内部维护了一个Message池用于Message的复用，避免使用new 重新分配内存。

**MessageQueue**
消息队列，用于存放Handler发送过来的Message对象。Message对象与Loopser相关联的Handler对象添加进MessageQueue中。（handler.post(Runnable r)系列方法实际上也是将Runnable 对象转化为Message存入到消息队列中去，可以查看源码）

    public final boolean post(Runnable r)
        {
           return  sendMessageDelayed(getPostMessage(r), 0);//发送延时消息
        }

     public final boolean sendMessageDelayed(Message msg, long delayMillis)
        {
            if (delayMillis < 0) {
                delayMillis = 0;
            }
            return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
        }
        
      public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            MessageQueue queue = mQueue;
            if (queue == null) {
                RuntimeException e = new RuntimeException(
                        this + " sendMessageAtTime() called with no mQueue");
                Log.w("Looper", e.getMessage(), e);
                return false;
            }
            return enqueueMessage(queue, msg, uptimeMillis);
        }
**Looper**
可以理解为`Handler`与`MessageQueue`之间的桥梁。`Looper`通过轮询消息队列，将消息对象取出来交给Handler去处理。

一个`Handler`对象仅与一个`Looper`相关联，一个`Message`也仅与一个目标`Handler`对象相关联，一个`Looper`对象拥有一个`MessageQueue`。但多个不同的`Handler`对象可以与同一个对象相关联，也就是说多个Handler可以共享一个`MessageQueue`，从而达到消息共享的目的，这也是Android通过Handler机制实现多线程间通信的核心原理；
需要注意的是线程默认并不会给我们提供一个一个Looper实例来管理消息队列，我们需要在线程中主动调用`Looper.prepare()`方法来实例化一个Looper对象，用于管理消息队列；Looper对象会不断去判断MessageQueue是否为空，如果不空，则将Message取出给相应的Handler进行处理；如果MessageQueue为空，则Looper对象会进行阻塞状态，直到有新的消息进入MessageQueue；所以这并不能很方便的帮助我们去完成和实现线程之间的消息通信，需要手动去实例一个Looper对象。于是Google官方帮我们官方很贴心的帮我们封装好了一个类，**HandlerThread**类。（参考博文：[Thread、Handler和HandlerThread关系何在？](https://blog.csdn.net/ly502541243/article/details/52414637)，[Handler、Thread、HandlerThread三者的区别](https://blog.csdn.net/weixin_41101173/article/details/79687313)）
	**HandlerThread**（参见博客：[Android 多线程之HandlerThread 完全详解](https://blog.csdn.net/javazejian/article/details/52426353)）

 - HandlerThread继承自Thread
 - HandlerThread内部创建了一个Looper实例
 - 通过获取HandlerThread的looper对象传递给Handler对象，可以在handleMessage方法中执行异步任务
 - 创建HandlerThread后必须先调用`HandlerThread.start()`方法，Thread会先调用`run`方法，创建Looper对象。
 
```
 @Override
    public void run() {
        mTid = Process.myTid();
        Looper.prepare();//实例化Looper
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();//空方法，可实现重写，注意是在开启轮询之前。
        Looper.loop();//开启轮询
        mTid = -1;
    }
```
所以拿过来用就行了，不用每次去`Looper.prepare(),Looper.loop()`了。记住必须得先`start()`。了解HandlerThread源码解析的，请参考博客（[Android 多线程之HandlerThread 完全详解](https://blog.csdn.net/javazejian/article/details/52426353)）
2）**AsyncTask**（参考博客：[Android 多线程-----AsyncTask详解](https://www.cnblogs.com/xiaoluo501395377/p/3430542.html)，[android多线程-AsyncTask之工作原理深入解析(上)，](https://blog.csdn.net/javazejian/article/details/52462830)）
AsyncTask，异步任务，是一个抽象泛型类。从字面上理解就是在UI线程运行的过程中，异步的完成一些操作。AsyncTask相对于Handler代码简单，使用方便。AsyncTask就相当于Android给我们提供了一个多线程编程的一个框架，其介于Thread和Handler之间。也因为是抽象类，使用时需要继承AsyncTask创建一个新类。
它提供了Params、Progress、Result 三个泛型参数。
```
public abstract class AsyncTask<Params, Progress, Result> {...}
```
 - Params: 这个泛型指定的是我们传递给异步任务执行时的参数的类型
 - Progress: 这个泛型指定的是我们的异步任务在执行的时候将执行的进度返回给UI线程的参数的类型
 - Result: 这个泛型指定的异步任务执行完后返回给UI线程的结果的类型
如果AsyncTask不需要传递具体参数，那么这三个泛型参数可以使用`Void`代替

```
/**
 * 作者 Superman
 * 日期 2018/12/12 16:50.
 * 文件 AndroidMoreThread
 * 描述 异步任务task
 */

public class DownLoadTask extends AsyncTask<Void,Void,Void>{

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
     * @param voids
     * @return
     */
    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    /**
     * 可以不用重写
     * 在UI线程中调用，当异步任务进度发生变化的时候执行。
     * 我们必须在doInBackground方法中调用publishProgress()
     * 来设置进度变化的值
     * @param values
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    /**
     * 可以不用重写
     * 在UI线程中调用，当异步任务完成的时候执行
     * 一般用于更新UI或其他必须在主线程执行的操作,传递参数为
     * doInBackground方法中的返回值
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    /**
     * 可以不用重写
     * 在UI线程中调用，当异步任务被取消时,该方法将被执行
     * 要注意的是这个时onPostExecute将不会被执行
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
```
***为什么我们的AsyncTask抽象类只有一个 doInBackground 的抽象方法呢？***
原因是，我们如果要做一个异步任务，我们必须要为其开辟一个新的Thread，让其完成一些操作，而在完成这个异步任务时，我可能并不需要弹出要给ProgressDialog，我并不需要随时更新我的ProgressDialog的进度条，我也并不需要将结果更新给我们的UI界面，所以除了 doInBackground 方法之外的三个方法，都不是必须有的，因此我们必须要实现的方法是 doInBackground 方法。
**执行顺序**：onPreExecute>>doInBackground(在doInBackground中如果调用了publishProgress那么onProgressUpdate方法将会被执行)>>onProgressUpdate>>onPostExecute。
***AsyncTask如何启动？***

```
public final AsyncTask<Params, Progress, Result> execute(Params... params){...}
```
该方法是一个final方法，参数类型是可变类型，实际上这里传递的参数和doInBackground(Params…params)方法中的参数是一样的，该方法最终返回一个AsyncTask的实例对象，可以使用该对象进行其他操作，比如结束线程之类的。启动范例如下：

    new DownLoadAsyncTask().execute(url1,url2,url3);
***调用AsyncTask需要注意的一些点？***

 - AsyncTask的实例必须在主线程（UI线程）中创建 ，execute方法也必须在主线程中调用
 -  不要在程序中直接的调用onPreExecute(), onPostExecute(Result)，doInBackground(Params…), onProgressUpdate(Progress…)这几个方法
 - 不能在doInBackground(Params… params)中更新UI
 - 一个AsyncTask对象只能被执行一次，也就是execute方法只能调用一次，否则多次调用时将会抛出异常

  
