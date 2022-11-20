package com.lokilinks.fitnesapp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lokilinks.fitnesapp.R
import com.lokilinks.fitnesapp.adapters.DayModel
import com.lokilinks.fitnesapp.adapters.DaysAdapter
import com.lokilinks.fitnesapp.adapters.ExerciseModel
import com.lokilinks.fitnesapp.databinding.FragmentDaysBinding
import com.lokilinks.fitnesapp.utils.DialogManager
import com.lokilinks.fitnesapp.utils.FragmentManager
import com.lokilinks.fitnesapp.utils.MainViewModel
import kotlin.math.roundToInt

class DaysFragment : Fragment(), DaysAdapter.Listener {

    private lateinit var binding: FragmentDaysBinding
    private lateinit var adapter: DaysAdapter
    private val model: MainViewModel by activityViewModels()
    private var ab: ActionBar? = null

   override fun onCreate (savedInstanceState: Bundle?){
       super.onCreate(savedInstanceState)
       setHasOptionsMenu(true)
   }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.currentDay = 0
        initRvcView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.reset){
            DialogManager.showDialog(
                activity as AppCompatActivity, R.string.reset_days,
                object : DialogManager.Listener {
                override fun onClick() {
                    model.pref?.edit()?.clear()?.apply()
                    adapter.submitList(fillDaysArray())
                }
            }
            )
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRvcView() = with (binding){
        adapter = DaysAdapter(this@DaysFragment)
        ab = (activity as AppCompatActivity).supportActionBar
        ab?.title = getString(R.string.days)
        rcViewDays.layoutManager = LinearLayoutManager(activity as AppCompatActivity)
        rcViewDays.adapter = adapter
        adapter.submitList(fillDaysArray())
    }

    private fun fillDaysArray() :ArrayList<DayModel> {
        val tArray= ArrayList<DayModel>()
        var restDoneCounter = 0
        resources.getStringArray(R.array.day_exercises).forEach {
            model.currentDay++
            val exCounter = it.split(",").size
            tArray.add(DayModel(it, 0,model.getExerciseCount() == exCounter))
        }
        binding.pB.max = tArray.size

        tArray.forEach{
            if(it.isDone) restDoneCounter++
        }
        upDateRestDaysUI(tArray.size-restDoneCounter,tArray.size )
        return tArray
    }

    private fun upDateRestDaysUI(restDays: Int, days: Int) = with (binding){
        val rDays = getString(R.string.rest) + " $restDays " + getString(R.string.rest_days)
        tvRestDays.text = rDays
        pB.progress = days - restDays
    }

    private fun fillExerciseList(day: DayModel){
        val tempList = ArrayList<ExerciseModel>()

        day.exercises.split(",").forEach {
            val exerciseList = resources.getStringArray(R.array.exercises)
            val exercise = exerciseList[it.toDouble().roundToInt()]
            val exerciseArray = exercise.split("|")
            tempList.add(ExerciseModel(exerciseArray[0], exerciseArray[1], false, exerciseArray[2]) )
        }
        model.mutableListExercise.value = tempList
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(day: DayModel) {
        if (!day.isDone){
            fillExerciseList(day)
            model.currentDay = day.dayNumber.toInt()
            FragmentManager.setFragment(ExerciseListFragment.newInstance(), activity as AppCompatActivity)
        } else{
            DialogManager.showDialog(
                activity as AppCompatActivity, R.string.reset_day,
                object : DialogManager.Listener {
                    override fun onClick() {
                        model.savePref(day.dayNumber.toString(), 0)
                        fillExerciseList(day)
                        model.currentDay = day.dayNumber.toInt()
                        FragmentManager.setFragment(ExerciseListFragment.newInstance(), activity as AppCompatActivity)
                    }
                }
            )
        }

    }
}