package com.raywenderlich.android.creaturemon.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureProcessorHolder
import com.raywenderlich.android.creaturemon.addcreature.AddCreatureViewModel
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesProcessorHolder
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesViewModel
import com.raywenderlich.android.creaturemon.app.Injection

/**
 * CreaturemonViewModelFactory - used to create an AllCreaturesViewModel by passing in
 * an AllCreaturesProcessorHolder.
 */
class CreaturemonViewModelFactory private constructor(
        private val applicationContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass == AllCreaturesViewModel::class.java) {
            return AllCreaturesViewModel(
                    AllCreaturesProcessorHolder(
                            Injection.provideCreatureRepository(applicationContext),
                            Injection.provideSchedulerProvider())) as T
        }

        // Allow CreaturemonViewModelFactory to create AddCreatureViewModel instances
        if (modelClass == AddCreatureViewModel::class.java) {
            return AddCreatureViewModel(
                    AddCreatureProcessorHolder(
                            Injection.provideCreatureRepository(applicationContext),
                            Injection.provideCreatureGenerator(),
                            Injection.provideSchedulerProvider())) as T
        }

        throw IllegalAccessException("Unknown model class $modelClass")
    }

    companion object : SingletonHolderSingleArg<CreaturemonViewModelFactory, Context>
        (::CreaturemonViewModelFactory)
}