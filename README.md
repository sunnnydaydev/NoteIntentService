# IntentService

# 简单使用

```kotlin
/**
 * Create by SunnyDay /03/13 17:40:59
 */
class DownLoadService : IntentService("FileDownLoadThread") {
    private val tag = "DownLoadService"
    override fun onHandleIntent(intent: Intent?) {
        Log.d(tag, "onHandleIntent Thread info： thread id -> ${Thread.currentThread().id} thread name -> ${Thread.currentThread().name}")

        Log.d(tag, "开始下载->")
        Log.d(tag, "下载ing...")
        SystemClock.sleep(3000)
        Log.d(tag, "下载完成")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        Log.d(tag, "onStart")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "onStartCommand：startId->$startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(tag, "onBind")
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(tag, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy")
    }
}
```

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.downLoad).setOnClickListener {
            startService(Intent(this,DownLoadService::class.java))
        }
    }
}
```

```kotlin
 //第一次开启 
 D/DownLoadService: onCreate
 D/DownLoadService: onStartCommand：startId->1
 D/DownLoadService: onStart
 D/DownLoadService: onHandleIntent Thread info： thread id -> 62 thread name -> IntentService[FileDownLoadThread]
 D/DownLoadService: 开始下载->
 D/DownLoadService: 下载ing...

 //第二次开启
 D/DownLoadService: onStartCommand：startId->2
 D/DownLoadService: onStart
 D/DownLoadService: 下载完成
 D/DownLoadService: onHandleIntent Thread info： thread id -> 62 thread name -> IntentService[FileDownLoadThread]
 D/DownLoadService: 开始下载->
 D/DownLoadService: 下载ing...
 D/DownLoadService: 下载完成
 D/DownLoadService: onDestroy
```

连续点击两次按钮，可以观察到log：

(1) IntentService的生命周期是这样的

onCreate -> onStartCommand -> onStart -> onHandleIntent -> onDestroy

(2) 系统默认帮我们开启一个子线程，我们可以在onHandleIntent中做一些耗时任务

(3) Service只会被创建一次，多次绑定onStartCommand回调多次

(4) 所有任务完成时onDestroy自动回调

# 介绍

- IntentService是Service的一个子类，内部维护了一个工作线程，他会把所有的任务都放到工作线程中处理。
- 多次开启IntentService时，每一次的开启的任务都会放到工作线程中处理，当所有的任务完成时他会自动调用stopSelf方法来结束Service
- onBind方法不会回调
- onStart方法内部进行了默认实现是IntentService的核心


# 源码分析

在分析IntentService的源码之前我们需要了解一点东西~

###### 1、handler消息机制

[参考](https://blog.csdn.net/qq_38350635/article/details/118683812?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522167886867416782428669510%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fblog.%2522%257D&request_id=167886867416782428669510&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~blog~first_rank_ecpm_v1~rank_v31_ecpm-3-118683812-null-null.blog_rank_default&utm_term=handler&spm=1018.2226.3001.4450)

###### 2、HandlerThread

啥是handlerThread呢？其实就是官方封装的一个api方便我们进行"主线程发送消息，子线程处理消息"，了解了handler的消息机制后
我们可以尝试写下这个demo

（1）主线程发送消息，子线程处理消息demo

```kotlin
class HandlerThreadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler_thread)
        // open a subThread
        thread {
            Looper.prepare()
            val handler = Handler{
                when (it.what) {
                   0x11 ->{
                       Log.d("HandlerThreadActivity","${Thread.currentThread()}收到:${it.obj}")
                   }
                }
                return@Handler true
            }
            runOnUiThread {
                Log.d("HandlerThreadActivity","${Thread.currentThread()}UI线程发送一条消息")
                // ui 线程发送消息
                handler.sendMessage(Message().apply {
                    what = 0x11
                    obj = "UI线程发送一条消息"
                })
            }
            Looper.loop()
        }
    }
}

D/HandlerThreadActivity: Thread[main,5,main]UI线程发送一条消息
        
D/HandlerThreadActivity: Thread[Thread-4,5,main]收到:UI线程发送一条消息
```

观察log发现我们实验成功了，其实不难理解主要是handler与Looper与Thread存在一定的绑定关系。接下来可以看下HandlerThread
如何使用的。

（2）HandlerThread使用

```kotlin
    /**
     * test：HandlerThread usage
     * */
    private fun test2() {
        // 1、创建对象，并开启线程
        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        // 2、核心，使用两个参数的构造（这里的handleMessage 回调再子线程中的）
        val handler = Handler(handlerThread.looper) {
            // 处理消息
            return@Handler true
        }
        // 3、发送消息
        handler.sendEmptyMessage(0x11)
    }
```

借助系统的api就快速了不少，我们可以很方便实现"主线程发送消息，子线程处理消息"，接下来看看源码实现。

（3）HandlerThread源码

```java
// 1、首先HandlerThread是一个普通的线程，只是这个线程绑定可Looper(持有Looper成员变成)
public class HandlerThread extends Thread {
    int mPriority;
    int mTid = -1;
    Looper mLooper;
    // 构造传递的是线程名字
    public HandlerThread(String name) {
        super(name);
        mPriority = Process.THREAD_PRIORITY_DEFAULT;
    }

    public HandlerThread(String name, int priority) {
        super(name);
        mPriority = priority;
    }
    
    protected void onLooperPrepared() {
    }

    // 2、run方法内部进行了实现，核心有两步：
    // (1) 创建Looper对象
    // (2) 开启Loop轮训
    @Override
    public void run() {
        mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();
        Looper.loop();
        mTid = -1;
    }
    
    // 3、提供了getLooper、getThreadHandler来获取对应的对象
    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }

        boolean wasInterrupted = false;

        // If the thread has been started, wait until the looper has been created.
        synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    wasInterrupted = true;
                }
            }
        }
        
        if (wasInterrupted) {
            Thread.currentThread().interrupt();
        }

        return mLooper;
    }
    
    @NonNull
    public Handler getThreadHandler() {
        if (mHandler == null) {
            mHandler = new Handler(getLooper());
        }
        return mHandler;
    }
    
}
```

可以看淡实现十分简单，只要明白handler的消息机制这个还是很好明白的。

// todo 把HandlerThread 回顾下。 再分析IntentService。

D/HandlerThreadActivity: Thread[main,5,main]UI线程发送一条消息
2023-03-15 16:41:08.790 9377-9415/com.sunnyday.noteintentservice D/HandlerThreadActivity: Thread[Thread-4,5,main]收到:UI线程发送一条消息

# 替代方案

JobIntentService

# 参考

[官方文档](https://developer.android.google.cn/reference/android/app/IntentService?hl=en)

[活儿好又不纠缠的IntentService](https://mp.weixin.qq.com/s?__biz=MzIxNjc0ODExMA==&mid=2247484238&idx=1&sn=27f732b316fe3886bb40d429ece96931&chksm=97851a6fa0f293799474fa6d45d7d3ae997c4a0657a438a7ea9fdb77d00aef4bb6986531fdb5&scene=38#wechat_redirect)

[Android IntentService详解（源码分析）](https://juejin.cn/post/6844904054854778894)