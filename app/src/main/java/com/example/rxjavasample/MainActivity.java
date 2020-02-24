package com.example.rxjavasample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Observable<Task> taskObservable = Observable // create a new Observable object
                .fromIterable(DataSource.createTasksList()) // apply 'fromIterable' operator
                .subscribeOn(Schedulers.io())// designate worker thread (background)
                .filter(new Predicate<Task>() {
                            @Override
                            public boolean test(Task task) throws Exception {

                                Log.d(TAG, "test: This filter thread is: "+ Thread.currentThread().getName());

                                try{
                                    Thread.sleep(1000);
                                }catch (Exception e){
                                    e.getStackTrace();
                                }

                                return task.isComplete();
                            }
                        }
                )
                .observeOn(AndroidSchedulers.mainThread()); // designate observer thread (main thread)


        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: Invoked");
                disposable.add(d);


            }

            @Override
            public void onNext(Task task) {
                Log.d(TAG, "onNext: Started");
                Log.d(TAG, "onNext: Thread Name is: " + Thread.currentThread().getName());
                Log.d(TAG, "onNext: The task decription is: "+ task.getDescription());

            }

            @Override
            public void onError(Throwable e) {

                Log.d(TAG, "onError: The thrown exceptions are: "+ e.getMessage());

            }

            @Override
            public void onComplete() {

                Log.d(TAG, "onComplete: The observing is completed");

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}
