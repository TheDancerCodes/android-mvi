package com.raywenderlich.android.creaturemon.util.schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * ImmediateSchedulerProvider - create an immediate scheduler that our test will use to avoid the
 * need for accessing the Android main thread.
 *
 * Add overrides for each type of scheduler that use the schedulers trampoline function.
 *
 * The trampoline method causes the Rx task to execute on the current thread.
 *
 */
class ImmediateSchedulerProvider : BaseSchedulerProvider {
    override fun computation(): Scheduler = Schedulers.trampoline()

    override fun io(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()
}