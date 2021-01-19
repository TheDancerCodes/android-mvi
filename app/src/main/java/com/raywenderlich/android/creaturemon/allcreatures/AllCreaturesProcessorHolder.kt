package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesAction.*
import com.raywenderlich.android.creaturemon.allcreatures.AllCreaturesResult.*
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import java.lang.IllegalArgumentException

/**
 * A Processor Holder that will be used for AllCreatures processing.
 *
 * The creatureRepository property is used to call into the repository.
 * The schedulerProvider property is used to put the processing on the RxJava IO Thread.
 *
 * Create both a loadAllCreaturesProcessor & clearAllCreaturesProcessor.
 *
 * NOTE: An RxJava Observable transformer takes an observable of one type & returns an observable
 * of another type. We will use our Observable transformer to transform our Actions into Results.
 */
class AllCreaturesProcessorHolder(
        private val creatureRepository: CreatureRepository,
        private val schedulerProvider: BaseSchedulerProvider
) {

    // Use Observable Transformer to turn a LoadAllCreaturesAction into a LoadAllCreaturesResult
    private val loadAllCreaturesProcessor =
            ObservableTransformer<LoadAllCreaturesAction, LoadAllCreaturesResult> { actions ->

                // For loading all creatures, we'll flat-map the action into a call to get
                // all creatures in the creature repository.
                actions.flatMap {
                    creatureRepository.getAllCreatures()

                            // This will return an observable list of creatures which we'll map
                            // to a success result.
                            .map { creatures -> LoadAllCreaturesResult.Success(creatures) }

                            // Use a cast operator for the result
                            .cast(LoadAllCreaturesResult::class.java)

                            // Call onErrorReturn and pass in a failure result should one occur
                            .onErrorReturn(LoadAllCreaturesResult::Failure)

                            // Subscribe to the Observable on the IO thread to ensure the DB call
                            // is done in the background.
                            .subscribeOn(schedulerProvider.io())

                            // Observe the results on the main thread.
                            .observeOn(schedulerProvider.ui())

                            // Add a call to the start with operator to make sure the first result in the
                            // result stream is a loading result.
                            .startWith(LoadAllCreaturesResult.Loading)
                }
            }

    private val clearAllCreaturesProcessor =
            ObservableTransformer<ClearAllCreaturesAction, ClearAllCreaturesResult> { actions ->

                actions.flatMap {
                    creatureRepository.clearAllCreatures()
                            .map { ClearAllCreaturesResult.Success } // map to success object result
                            .cast(ClearAllCreaturesResult::class.java)
                            .onErrorReturn(ClearAllCreaturesResult::Failure)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .startWith(ClearAllCreaturesResult.Clearing)
                }
            }

    /* We now have a processor for the loading action and a processor for the clear all action.
     *
     * In order to keep these actions as one stream of observables, we'll merge the streams of
     * observables together using RX merge operators.
     *
     * Create an Action Processor Property for Processor Holder that turns Actions into Results &
     * merges the loadAllCreaturesProcessor and clearAllCreaturesProcessor.
     *
     * We do so by using the compose operator on the individual processors.
     */
    internal var actionProcessor =
            ObservableTransformer<AllCreaturesAction, AllCreaturesResult> { actions ->
                actions.publish { shared ->
                    Observable.merge(
                            shared.ofType(LoadAllCreaturesAction::class.java).compose(loadAllCreaturesProcessor),
                            shared.ofType(ClearAllCreaturesAction::class.java).compose(clearAllCreaturesProcessor))

                            // Merge with some error checks to make sure that we're processing
                            // one of the two known actions
                            .mergeWith(
                                    // Error for not implemented actions
                                    shared.filter { v ->
                                        v !is LoadAllCreaturesAction
                                                && v !is ClearAllCreaturesAction
                                    }.flatMap { w ->
                                        Observable.error<AllCreaturesResult>(
                                                IllegalArgumentException("Unknown Action type: $w"))
                                    }
                            )
                }
            }
}