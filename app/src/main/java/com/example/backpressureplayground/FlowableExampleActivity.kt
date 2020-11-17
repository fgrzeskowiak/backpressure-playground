package com.example.backpressureplayground

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Emitter
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Supplier
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_example.*
import java.util.concurrent.TimeUnit

class FlowableExampleActivity : AppCompatActivity() {
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)

        var counter = 0

        disposable.addAll(
            Flowable.create<Int>({ emitter ->
                repeat(10_000_000) { count ->
                    emitter.onNext(count)
                }
                emitter.onComplete()
            }, BackpressureStrategy.BUFFER) // BackpressureStrategy.LATEST, BackpressureStrategy.DROP
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { println("Backpressure counter: $counter") }
                .subscribe { count ->
                    counter++
                    text_view.text = count.toString()
                    progress_bar.progress = ((count / 10_000_000f) * 100).toInt()
                }

            // TODO uncomment to see different solutions
//            Flowable.range(0, 10_000_000)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete { println("Backpressure counter: $counter") }
//                .subscribe {
//                    counter++
//                    text_view.text = it.toString()
//                    progress_bar.progress = ((it / 10_000_000f) * 100).toInt()
//                }

//            Flowable
//                .generate(
//                    Supplier { 0 },
//                    BiFunction { current: Int, emitter: Emitter<Int> ->
//                        if (current < 10_000_000) {
//                            emitter.onNext(current)
//                            current + 1
//                        } else {
//                            emitter.onComplete()
//                            current
//                        }
//                    }
//                )
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete { println("Backpressure counter: $counter") }
//                .subscribe {
//                    counter++
//                    text_view.text = it.toString()
//                    progress_bar.progress = ((it / 10_000_000f) * 100).toInt()
//                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
