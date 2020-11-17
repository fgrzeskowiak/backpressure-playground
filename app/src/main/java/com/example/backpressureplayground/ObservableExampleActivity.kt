package com.example.backpressureplayground

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_example.*
import java.util.concurrent.TimeUnit

class ObservableExampleActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        var counter = 0

        disposable.addAll(
            Observable.create<Int> { emitter ->
                for (i in 0 until 10_000_000) {
                    emitter.onNext(i)
                }
                emitter.onComplete()
            }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { println("Backpressure counter: $counter") }
                .subscribe {
                    counter++
                    text_view.text = it.toString()
                    progress_bar.progress = ((it / 1_000_000) * 100)
                }

            // TODO uncomment to see different solution
//            Observable.range(0, 10_000_000)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete { println("Backpressure counter: $counter") }
//                .subscribe {
//                    counter++
//                    text_view.text = it.toString()
//                    progress_bar.progress = ((it / 1_000_000) * 100)
//                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
