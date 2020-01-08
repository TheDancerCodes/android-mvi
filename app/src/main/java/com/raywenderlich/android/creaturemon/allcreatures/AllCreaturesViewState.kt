package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.mvibase.MviViewState

/**
 * isLoading - The Boolean is to indicate whether an Action is in progress, in which case a
 * progress bar is displayed.
 *
 * creatures - List of creatures to be shown when loading is complete.
 *
 * error - displays error if any.
 *
 * NOTE: When the user clears all creatures, we can rely on having an empty list of creatures to
 * trigger showing our empty state.
 *
 * This is also shown when the user first runs the app as no creatures have been created yet.
 */
data class AllCreaturesViewState(
        val isLoading : Boolean,
        val creatures : List<Creature>,
        val error : Throwable?
) : MviViewState {

    /*
     * Companion object with idle() function, used for the default state of the screen when the
     * screen is first shown before the loading action is kicked off.
     *
     * Inside the idle view state return value, we set:
     * (a) isLoading to false,
     * (b) creatures to an empty list
     * (c) error to null
     *
     * NOTE: The Loading state will be triggered in our events stream as the app starts up.
     * The idle function gives us the very first state in the view state stream.
     */
    companion object {
        fun idle(): AllCreaturesViewState = AllCreaturesViewState(
                isLoading = false,
                creatures = emptyList(),
                error = null
        )
    }
}