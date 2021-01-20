package com.raywenderlich.android.creaturemon.app

import android.content.Context
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.data.repository.room.RoomRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import com.raywenderlich.android.creaturemon.util.schedulers.SchedulerProvider

/**
 * This is an injection object we will use to perform injection of the dependencies
 * that our processor holders will need in their constructors.
 */
object Injection {

    fun provideCreatureRepository(context: Context): CreatureRepository {
        return RoomRepository()
    }

    fun provideCreatureGenerator(): CreatureGenerator {
        return CreatureGenerator()
    }

    fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider

}