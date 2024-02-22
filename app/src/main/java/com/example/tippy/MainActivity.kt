package com.example.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet



private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercentLabel: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var etNumberPeople: EditText
    private lateinit var swSplit: Switch
    private lateinit var constraintLayout: ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercentLabel = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        etNumberPeople = findViewById(R.id.etNumberPeople)
        swSplit = findViewById(R.id.swSplit)
        constraintLayout = findViewById(R.id.constraintLayout)


        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercentLabel.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        swSplit.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                // Create a ConstraintSet
                val constraintSet = ConstraintSet()
                // Clone the constraints from the ConstraintLayout
                constraintSet.clone(constraintLayout)

                if (isChecked) {

                    constraintSet.connect(etNumberPeople.id, ConstraintSet.TOP, swSplit.id, ConstraintSet.TOP, 100)

                    // Apply the new constraints to the ConstraintLayout
                    constraintSet.applyTo(constraintLayout)

                    // Change the visibility of the field to visible in case the switch "Split" is checked
                    etNumberPeople.visibility = android.view.View.VISIBLE
                } else {

                    constraintSet.connect(etNumberPeople.id, ConstraintSet.TOP, swSplit.id, ConstraintSet.TOP, 0)

                    // Apply the new constraints to the ConstraintLayout
                    constraintSet.applyTo(constraintLayout)

                    // Change the visibility of the field to invisible in case the switch "Split" is not checkd
                    etNumberPeople.visibility = android.view.View.INVISIBLE
                }
            }
        })

        //Set a listener to the seekbar
        seekBarTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercentLabel.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        //Set a TextWatcher to the BaseAmount field
        etBaseAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }

        })

        //Set a textWatcher to the Number People field
        etNumberPeople.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                computeTipAndTotal()
            }
        })

    }


    //Compute the values of tip and total
    private fun computeTipAndTotal() {

        var numberPeopleValue : Int
        var tipAmount : Double
        var totalAmount : Double

        //In case the split button is checked and the NumberPeople field is not empty, must divide
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        var splitTip : Editable = etNumberPeople.text


        //In case the split button is checked and the NumberPeople field is not empty, must divide
        if (swSplit.isChecked and splitTip.isNotEmpty()) {

            numberPeopleValue =  splitTip.toString().toInt()
            tipAmount = ((baseAmount * tipPercent / 100) / numberPeopleValue)
            totalAmount = (baseAmount + tipAmount) / numberPeopleValue


        } else {

            tipAmount = (baseAmount * tipPercent / 100)
            totalAmount = (baseAmount + tipAmount)
        }

        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }

    //Update the description of the tip below the SeekBar
    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }

        tvTipDescription.text = tipDescription

        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / seekBarTip.max,

            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)

        ) as Int

        tvTipDescription.setTextColor(color)


    }
}