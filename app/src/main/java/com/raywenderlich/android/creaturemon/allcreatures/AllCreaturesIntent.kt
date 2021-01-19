package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.mvibase.MviIntent

/**
 * AllCreaturesIntent implements MviIntent to have all the intents be MVI intents
 */
sealed class AllCreaturesIntent : MviIntent {

    // object intent subtypes for loading all creatures & clearing all creatures.
    object LoadAllCreaturesIntent: AllCreaturesIntent()
    object ClearAllCreaturesIntent: AllCreaturesIntent()
}