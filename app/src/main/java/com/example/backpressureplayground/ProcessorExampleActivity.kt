package com.example.backpressureplayground

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_example.*

class ProcessorExampleActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()
    private val sourceProcessor = PublishProcessor.create<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        var counter = 0

        disposable.addAll(
            sourceProcessor
                //.onBackpressureBuffer()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { println("Backpressure counter: $counter") }
                .subscribe {
                    counter++
                    text_view.text = it.toString()
                    progress_bar.progress = ((it / 10_000_000f) * 100).toInt()
                },

            Observable.fromCallable {
                for (i in 0 until 10_000_000) {
                    sourceProcessor.onNext(i)
                }
                sourceProcessor.onComplete()
            }
                .subscribeOn(Schedulers.computation())
                .subscribe()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
