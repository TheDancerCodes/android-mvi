package com.raywenderlich.android.creaturemon.allcreatures

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesResult.*
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * actionProcessorHolder - Processor Holder property that extends the ViewModel class & implements
 * the MviViewModel interface.
 *
 * The MviViewModel is typed with an Intent & ViewState.
 */
class AllCreaturesViewModel(
        private val actionProcessorHolder: AllCreaturesProcessorHolder
) : ViewModel(), MviViewModel<AllCreaturesIntent, AllCreaturesViewState> {

    override fun processIntents(intents: Observable<AllCreaturesIntent>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun states(): Observable<AllCreaturesViewState> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /*
     * Add a reducer property.
     *
     * The reducer is an Rx BiFunction - an interface that takes two values to produce a new value.
     * We take in the previous ViewState & the results and the output will be a new state.
     */
    companion object {
        private val reducer = BiFunction {
            previousState: AllCreaturesViewState, result: AllCreaturesResult ->

            // To determine the new ViewState, we take advantage of our results being sealed classes
            // which lets us use a Kotlin when() expression to return a new value for the ViewState.
            when(result) {

                /* LoadAllCreatureResult:
                 * Handles all loading states resulting from a loading intent.
                 */
                is LoadAllCreaturesResult -> when(result) {

                    // Success case
                    is LoadAllCreaturesResult.Success -> {
                        previousState.copy(isLoading = false, creatures = result.creatures)
                    }

                    // Failure case
                    is LoadAllCreaturesResult.Failure ->
                        previousState.copy(isLoading = false, error = result.error)

                    // Loading case
                    is LoadAllCreaturesResult.Loading -> previousState.copy(isLoading = true)
                }

                /* ClearAllCreatureResult:
                 *
                 * We use an empty list in the success case.
                 */
                is ClearAllCreaturesResult -> when(result) {

                    // Success case
                    is ClearAllCreaturesResult.Success -> {
                        previousState.copy(isLoading = false, creatures = emptyList())
                    }

                    // Failure case
                    is ClearAllCreaturesResult.Failure ->
                        previousState.copy(isLoading = false, error = result.error)

                    // Loading case
                    is ClearAllCreaturesResult.Clearing -> previousState.copy(isLoading = true)


                }

            }
        }
    }

}