package com.raywenderlich.android.creaturemon.mvibase

import io.reactivex.Observable

/**
 * The View must provide intents for the ViewModel and also be able to render new state
 * coming from the ViewModel.
 *
 * The MviView interface is typed by an MviIntent & MviViewState.
 */
interface MviView<I: MviIntent, S: MviViewState> {
    fun intents(): Observable<I>
    fun render(state: S)
}