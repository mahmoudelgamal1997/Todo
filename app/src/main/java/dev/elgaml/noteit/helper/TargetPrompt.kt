package dev.elgaml.noteit.helper

import android.app.Activity
import android.util.Log
import android.view.View
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence

class TargetPrompt constructor(val context: Activity, val list: List<PromptType>){
    fun showTapTarget(){
            // 1
            var target = TapTargetSequence(context)
            for (i in 0..list.size-1) {
            target.target(TapTarget.forView(list[i].view, list[i].title, list[i].desc)
          .cancelable(true).transparentTarget(true).targetRadius(70))
                }

            target.listener(object : TapTargetSequence.Listener {
                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    }
                    // 4
                    override fun onSequenceFinish() {
                        SharedHelper.saveBoolean(context,SharedHelper.PROMPT,true)
                    }
                    // 5
                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        SharedHelper.saveBoolean(context,SharedHelper.PROMPT,true)
                    }
                })
                // 6
                .start()
    }
    data class PromptType(
        val title:String,
        val desc:String,
        val view:View
        )
}