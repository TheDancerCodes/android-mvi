package com.raywenderlich.android.creaturemon.addcreature

import com.raywenderlich.android.creaturemon.mvibase.MviIntent

/**
 * Data intent subtypes to capture all the options.
 */
sealed class AddCreatureIntent : MviIntent {

    // Avatar intent data class for when a user selects an avatar.
    // The Avatar intent holds an int for the drawable resource a user has selected.
    data class AvatarIntent(val drawable: Int) : AddCreatureIntent()

    // Name intent for when a user types in a name
    data class NameIntent(val name: String) : AddCreatureIntent()

    // Intent for when a user selects an item for the attributes spinner drop down
    // These intents take an index value for the currently selected index in the spinner
    data class IntelligenceIntent(val intelligenceIndex: Int) : AddCreatureIntent()
    data class StrengthIntent(val strengthIndex: Int) : AddCreatureIntent()
    data class EnduranceIntent(val enduranceIndex: Int) : AddCreatureIntent()

    // Intent for when user taps save button.
    // This intent will need all of the items on the screen to save a new creature.
    data class SaveIntent(
       val drawable: Int,
       val name: String,
       val intelligenceIndex: Int,
       val strengthIndex: Int,
       val enduranceIndex: Int) : AddCreatureIntent()
}