package com.raywenderlich.android.creaturemon.addcreature

import androidx.lifecycle.ViewModel
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureResult.*
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.mvibase.MviViewModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * actionProcessorHolder - Processor Holder property that extends the ViewModel class & implements
 * the MviViewModel interface.
 *
 * The MviViewModel is typed with an Intent & ViewState.
 */
class AddCreatureViewModel(
        private val actionProcessorHolder: AddCreatureProcessorHolder
) : ViewModel(), MviViewModel<AddCreatureIntent, AddCreatureViewState> {
    override fun processIntents(intents: Observable<AddCreatureIntent>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun states(): Observable<AddCreatureViewState> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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