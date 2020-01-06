package com.raywenderlich.android.creaturemon.mvibase

import io.reactivex.Observable

/**
 * A ViewModel must be able to process intents coming from the view and provide a stream
 * of states for the view to observe.
 *
 * The MviViewModel is typed by an MviIntent & MviViewState.
 */
interface MviViewModel<I: MviIntent, S: MviViewState> {

    fun processIntents(intents: Observable<I>)
    fun states(): Observable<S>
}