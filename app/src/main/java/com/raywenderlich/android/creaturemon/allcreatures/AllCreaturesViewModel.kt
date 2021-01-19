package com.raywenderlich.android.creaturemon.allcreatures

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesAction.*
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesIntent.*
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesResult.*
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import com.raywenderlich.android.creaturemon.util.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * actionProcessorHolder - Processor Holder property that extends the ViewModel class & implements
 * the MviViewModel interface.
 *
 * The MviViewModel is typed with an Intent & ViewState.
 */
class AllCreaturesViewModel(
        private val actionProcessorHolder: AllCreaturesProcessorHolder
) : ViewModel(), MviViewModel<AllCreaturesIntent, AllCreaturesViewState> {

    /*
     * Rx properties that we will use to process intents from the view & create state events that
     * will be observed by the view.
     *
     * intentsSubject - PublishSubject that will start our Observable stream in the ViewModel.
     * statesObservable - This value is set up using a private method compose()
     */
    private val intentsSubject: PublishSubject<AllCreaturesIntent> = PublishSubject.create()
    private val statesObservable: Observable<AllCreaturesViewState> = compose()

    /*
     * Build Observable stream that lets us produce states from the ViewModel.
     *
     * When the view load we want to make sure that only one load intent makes it into our stream.
     * Use ObservableTransformer to take in only that first load intent and then pass through any
     * intents that are not of type load.
     *
     * Use a take(1) operator to load the intents and the notOfType extension function in the
     * util package to pass through any other intents that come through after that first load intent.
     */
    private val intentFilter: ObservableTransformer<AllCreaturesIntent, AllCreaturesIntent>
        get() = ObservableTransformer { intents ->
            intents.publish { shared ->
                Observable.merge(
                        shared.ofType(LoadAllCreaturesIntent::class.java).take(1),
                        shared.notOfType(LoadAllCreaturesIntent::class.java)
                )
            }
        }


    // Subscribe to intents passed in from the view using our intentsSubject as the subscriber
    override fun processIntents(intents: Observable<AllCreaturesIntent>) {
        intents.subscribe(intentsSubject)
    }

    // Return our private statesObservable property
    override fun states(): Observable<AllCreaturesViewState> = statesObservable

    /*
     * compose() - produces our statesObservable property
     *
     * Start with intentsSubject and compose it with IntentFilter to make sure there is only a
     * single load intent the stream.
     *
     * We then map() the intent to an action and compose() with our actionProcessor in order to process
     * to results that are then fed into the scan() operator.
     *
     * scan() operator - applies the reducer and uses the state provided by the idle function as a
     * default state, which it feeds back into the stream along with the second item emitted in the
     * stream to which the reducer is applied & then that is fed back into the stream and so on.
     *
     * NOTE: Compare this use of the scan() operator to the use of the reduce method on a Kotlin
     * collection, and you can see why we use the term reducer.
     *
     * distinctUntilChanged() - to ensure contiguous duplicate states do not come through,
     * to prevent unnecessary rendering of duplicate states.
     *
     * replay(1) - to ensure a new subscriber will get the last emitted Observable right away.
     *
     * autoConnect(0) - to make sure that the connection is immediate.
     */
    private fun compose(): Observable<AllCreaturesViewState> {
        return intentsSubject
                .compose(intentFilter)
                .map(this::actionFromIntent)
                .compose(actionProcessorHolder.actionProcessor)
                .scan(AllCreaturesViewState.idle(), reducer)
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)
    }


    // To map/convert Intents into Actions
    private fun actionFromIntent(intent: AllCreaturesIntent): AllCreaturesAction {
        return when (intent) {
            is LoadAllCreaturesIntent -> LoadAllCreaturesAction
            is ClearAllCreaturesIntent -> ClearAllCreaturesAction
        }
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