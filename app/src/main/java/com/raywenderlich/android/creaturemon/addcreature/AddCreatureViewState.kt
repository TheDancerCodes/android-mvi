package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.mvibase.MviViewState

data class AddCreatureViewState(
        val isProcessing : Boolean,
        val creature : Creature,
        val isDrawableSelected : Boolean,
        val isSaveComplete : Boolean,
        val error : Throwable?
) : MviViewState {

    /* Use a default method in the companion object to give a default state when the screen is
     * first shown.
     *
     * Inside the default view state return value, we set:
     * (a) isProcessing to false
     * (b) a default creature with empty attributes, no drawable selected, no name
     * (c) save is not complete
     * (d) there is no error
     *
     * NOTE: As the user starts building out the creature, the MVI cycle will send through new
     * creature values that will allow us to show the current value for the creature hit points.
     */
    companion object {
        fun default(): AddCreatureViewState = AddCreatureViewState(
                false,
                CreatureGenerator().generateCreature(CreatureAttributes(), name = "", drawable = 0),
                false,
                false,
                null
        )
    }
}