package com.lokilinks.fitnesapp.fragments

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.lokilinks.fitnesapp.R
import com.lokilinks.fitnesapp.databinding.WaitingFragmentBinding
import com.lokilinks.fitnesapp.utils.FragmentManager
import com.lokilinks.fitnesapp.utils.TimeUtils

const val COUNT_DOWN_TIME = 11000L

class WaitingFragment : Fragment() {

    private lateinit var binding: WaitingFragmentBinding
    private lateinit var timer: CountDownTimer
    private var ab: ActionBar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = WaitingFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ab = (activity as AppCompatActivity).supportActionBar
        ab?.title = getString(R.string.waiting)
        binding.pBar.max = COUNT_DOWN_TIME.toInt()
        startTimer()

    }

    private fun startTimer() = with(binding){
        timer = object:  CountDownTimer(COUNT_DOWN_TIME, 1){
            override fun onTick(restTime: Long) {
               tvTimer.text = TimeUtils.getTime(restTime)

                pBar.progress = restTime.toInt()
            }
            override fun onFinish() {
                FragmentManager.setFragment(ExerciseFragment.newInstance(), activity as AppCompatActivity)
            }

        }.start()
    }


    companion object {
        @JvmStatic
        fun newInstance() = WaitingFragment()
    }

    override fun onDetach() {
        super.onDetach()
        timer.cancel()
    }

}