package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.mvibase.MviAction

/**
 * Kotlin sealed class with objects to represent the actions.
 *
 * The actions correspond to LoadAllCreatures and ClearAllCreatures intents.
 */
sealed class AllCreaturesAction : MviAction {
    object LoadAllCreaturesAction : AllCreaturesAction()
    object ClearAllCreaturesAction : AllCreaturesAction()
}