package com.example.emotionalapp.ui.body

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.emotionalapp.R
import com.example.emotionalapp.ui.alltraining.AllTrainingPageActivity
import com.example.emotionalapp.util.setSingleListener
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class BodyRecordActivity : AppCompatActivity() {

    private val viewModel: BodyRecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_practice_record)

        val trainingId = intent.getStringExtra("TRAINING_ID") ?: ""
        viewModel.setTrainingId(trainingId)

        val konfettiView = findViewById<nl.dionsegijn.konfetti.xml.KonfettiView>(R.id.konfettiView)
        val btnBack = findViewById<View>(R.id.btnBack)
        val etFeedback = findViewById<EditText>(R.id.etFeedback1)
        val btnSave = findViewById<TextView>(R.id.btnSaveFeedback)

        konfettiView.visibility = View.VISIBLE
        konfettiView.start(
            Party(
                speed = 0f,
                maxSpeed = 30f,
                damping = 0.9f,
                spread = 360,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 1, TimeUnit.SECONDS).max(100),
                position = Position.Relative(0.5, 0.3)
            )
        )

        AlertDialog.Builder(this)
            .setTitle("훈련 완료")
            .setMessage("잘하셨습니다! 오늘도 당신은 당신의 몸에 주의를 기울였습니다!")
            .setPositiveButton("확인", null)
            .show()

        btnBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("훈련 종료")
                .setMessage("훈련을 종료하고 나가시겠어요?")
                .setPositiveButton("예") { _, _ -> finish() }
                .setNegativeButton("아니오", null)
                .show()
        }

        etFeedback.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateFeedbackText(s?.toString().orEmpty())
            }
        })

        btnSave.setSingleListener {
            val error = viewModel.validate()
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                return@setSingleListener
            }

            viewModel.save(this)
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                btnSave.isEnabled = !state.isSaving

                state.errorMessage?.let {
                    Toast.makeText(this@BodyRecordActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }

                if (state.saveSuccess) {
                    viewModel.consumeSaveSuccess()
                    Toast.makeText(this@BodyRecordActivity, "소감이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@BodyRecordActivity, AllTrainingPageActivity::class.java))
                    finish()
                }
            }
        }
    }
}