package com.raywenderlich.android.creaturemon.addcreature

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureAction.*
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureIntent.*
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureResult.*
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * actionProcessorHolder - Processor Holder property that extends the ViewModel class & implements
 * the MviViewModel interface.
 *
 * The MviViewModel is typed with an Intent & ViewState.
 *
 * NOTE: We don't have to worry about an initial loading state for this screen,
 * so we don't need an intentFilter
 */
class AddCreatureViewModel(
        private val actionProcessorHolder: AddCreatureProcessorHolder
) : ViewModel(), MviViewModel<AddCreatureIntent, AddCreatureViewState> {

    /*
     * Rx properties that we will use to process intents from the view & create state events that
     * will be observed by the view.
     *
     * intentsSubject - PublishSubject that will start our Observable stream in the ViewModel.
     * statesObservable - This value is set up using a private method compose()
     */
    private val intentsSubject: PublishSubject<AddCreatureIntent> = PublishSubject.create()
    private val stateObservable: Observable<AddCreatureViewState> = compose()

    // Subscribe to intents passed in from the view using our intentsSubject as the subscriber
    override fun processIntents(intents: Observable<AddCreatureIntent>) {
        intents.subscribe(intentsSubject)
    }

    // Return our private statesObservable property
    override fun states(): Observable<AddCreatureViewState> = stateObservable

    /*
     * compose() - produces our statesObservable property
     *
     * We then map() the intent to an action
     *
     * compose() with our actionProcessor in order to process to results that are then fed into
     * the scan() operator to reduce to a new state.
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
    private fun compose(): Observable<AddCreatureViewState> {
        return intentsSubject
                .map(this::actionFromIntent)
                .compose(actionProcessorHolder.actionProcessor)
                .scan(AddCreatureViewState.default(), reducer)
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)
    }

    // To map/convert Intents into Actions
    private fun actionFromIntent(intent: AddCreatureIntent): AddCreatureAction {
        return when(intent) {
            is AvatarIntent -> AvatarAction(intent.drawable)
            is NameIntent -> NameAction(intent.name)
            is IntelligenceIntent -> IntelligenceAction(intent.intelligenceIndex)
            is StrengthIntent -> StrengthAction(intent.strengthIndex)
            is EnduranceIntent -> EnduranceAction(intent.enduranceIndex)
            is  SaveIntent -> SaveAction(
                    intent.drawable,
                    intent.name,
                    intent.intelligenceIndex,
                    intent.strengthIndex,
                    intent.enduranceIndex
            )
        }
    }

    /*
     * NOTE:
     * There are more possible results for the AddCreature screen than the AllCreatures screen
     *
     * Since we have more actions, we'll create helper functions for each reducer, and combine them
     * at the end.
     *
     * We use a when expression & handle the success, failure and processing cases.
     */
    companion object {

        // CreatureGenerator property
        private val generator = CreatureGenerator()

        /*
         * Full Reducer:
         *
         * We route to one of the helper functions based on the return type.
         */
        private val reducer = BiFunction { previousState: AddCreatureViewState, result: AddCreatureResult ->
            when(result) {
                is AvatarResult -> reduceAvatar(previousState, result)
                is NameResult -> reduceName(previousState, result)
                is IntelligenceResult -> reduceIntelligence(previousState, result)
                is StrengthResult -> reduceStrength(previousState, result)
                is EnduranceResult -> reduceEndurance(previousState, result)
                is SaveResult -> reduceSave(previousState, result)
            }
        }

        /* Helper functions for each reducer */

        // Avatar case
        private fun reduceAvatar(previousState: AddCreatureViewState, result: AvatarResult):
                AddCreatureViewState = when(result) {

            is AvatarResult.Success -> {
                previousState.copy(
                        isProcessing = false,
                        error = null,
                        creature = generator.generateCreature(
                                previousState.creature.attributes,
                                previousState.creature.name,
                                result.drawable),
                        isDrawableSelected = (result.drawable != 0)
                )
            }

            is AvatarResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }

            is AvatarResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }

        // Name case
        private fun reduceName(previousState: AddCreatureViewState, result: NameResult):
                AddCreatureViewState = when(result) {

            is NameResult.Success -> {
                previousState.copy(
                        isProcessing = false,
                        error = null,
                        creature = generator.generateCreature(
                                previousState.creature.attributes,
                                result.name,
                                previousState.creature.drawable))
            }

            is NameResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }

            is NameResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }

        // Intelligence case
        private fun reduceIntelligence(
                previousState: AddCreatureViewState, result: IntelligenceResult):
                AddCreatureViewState = when(result) {

            is IntelligenceResult.Success -> {
                val attributes = CreatureAttributes(
                        result.intelligence,
                        previousState.creature.attributes.strength,
                        previousState.creature.attributes.endurance)

                previousState.copy(
                        isProcessing = false,
                        error = null,
                        creature = generator.generateCreature(
                                attributes,
                                previousState.creature.name,
                                previousState.creature.drawable))
            }

            is IntelligenceResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }

            is IntelligenceResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }

        // Strength case
        private fun reduceStrength(
                previousState: AddCreatureViewState, result: StrengthResult):
                AddCreatureViewState = when(result) {

            is StrengthResult.Success -> {
                val attributes = CreatureAttributes(
                        previousState.creature.attributes.intelligence,
                        result.strength,
                        previousState.creature.attributes.endurance)

                previousState.copy(
                        isProcessing = false,
                        error = null,
                        creature = generator.generateCreature(
                                attributes,
                                previousState.creature.name,
                                previousState.creature.drawable))
            }

            is StrengthResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }

            is StrengthResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }

        // Endurance case
        private fun reduceEndurance(
                previousState: AddCreatureViewState, result: EnduranceResult):
                AddCreatureViewState = when(result) {

            is EnduranceResult.Success -> {
                val attributes = CreatureAttributes(
                        previousState.creature.attributes.intelligence,
                        previousState.creature.attributes.strength,
                        result.endurance)

                previousState.copy(
                        isProcessing = false,
                        error = null,
                        creature = generator.generateCreature(
                                attributes,
                                previousState.creature.name,
                                previousState.creature.drawable))
            }

            is EnduranceResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }

            is EnduranceResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }

        // Save case
        private fun reduceSave(
                previousState: AddCreatureViewState, result: SaveResult):
                AddCreatureViewState = when(result) {

            is SaveResult.Success -> {
                previousState.copy(isProcessing = false, isSaveComplete = true, error = null)
            }

            is SaveResult.Failure -> {
                previousState.copy(isProcessing = false, error = result.error)
            }

            is SaveResult.Processing -> {
                previousState.copy(isProcessing = true, error = null)
            }
        }
    }

}