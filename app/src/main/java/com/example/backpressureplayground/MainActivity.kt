package com.example.backpressureplayground

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.acivity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acivity_main)

        observable.setOnClickListener {
            startActivity<ObservableExampleActivity>()
        }

        flowable.setOnClickListener {
            startActivity<FlowableExampleActivity>()
        }

        processor.setOnClickListener {
            startActivity<ProcessorExampleActivity>()
        }
    }
}