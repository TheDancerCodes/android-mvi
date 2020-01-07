package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.mvibase.MviResult

/**
 * Base Sealed class that represents an AllCreaturesResult.
 *
 * Add nested sealed classes to hold the possible results for loading & clearing AllCreatures.
 */
sealed class AllCreaturesResult : MviResult {

    sealed class LoadAllCreaturesResult : AllCreaturesResult() {

        // LoadAllCreaturesResult subtypes
        object Loading: LoadAllCreaturesResult()
        data class Success(val creatures: List<Creature>) : LoadAllCreaturesResult()
        data class Failure(val error: Throwable) : LoadAllCreaturesResult()
    }

    sealed class ClearAllCreaturesResult : AllCreaturesResult() {

        // ClearAllCreaturesResult subtypes
        object Clearing: ClearAllCreaturesResult()
        object Success: ClearAllCreaturesResult() // object subtype since the list will be empty after clearing.
        data class Failure(val error: Throwable) : ClearAllCreaturesResult()
    }


}