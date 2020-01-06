package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.mvibase.MviIntent

sealed class AllCreaturesIntent : MviIntent {

    // object intent subtypes for loading all creatures & clearing all creatures.
    object LoadAllCreatures: AllCreaturesIntent()
    object ClearAllCreatures: AllCreaturesIntent()
}