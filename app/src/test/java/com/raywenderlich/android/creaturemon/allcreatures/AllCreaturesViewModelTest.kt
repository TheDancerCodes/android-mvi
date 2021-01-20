package com.raywenderlich.android.creaturemon.allcreatures

import com.raywenderlich.android.creaturemon.data.model.Creature
import com.raywenderlich.android.creaturemon.data.model.CreatureAttributes
import com.raywenderlich.android.creaturemon.data.model.CreatureGenerator
import com.raywenderlich.android.creaturemon.data.repository.CreatureRepository
import com.raywenderlich.android.creaturemon.util.schedulers.BaseSchedulerProvider
import com.raywenderlich.android.creaturemon.util.schedulers.ImmediateSchedulerProvider
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class AllCreaturesViewModelTest {

    /*
     * Add the properties needed for the test:
     * CreatureRepository
     * A schedulerProvider for the ViewModel processors,
     * A CreatureGenerator to make some test creatures
     * The ViewModel under test
     * A test observer for our Rx tests
     * A list of creatures property
     */
    @Mock
    private lateinit var creatureRepository: CreatureRepository
    private lateinit var schedulerProvider: BaseSchedulerProvider
    private lateinit var generator: CreatureGenerator
    private lateinit var viewModel: AllCreaturesViewModel
    private lateinit var testObserver: TestObserver<AllCreaturesViewState>
    private lateinit var creatures: List<Creature>

    /*
     * Set up method for our tests
     *
     * Initialize our mocks, SchedulerProvider and CreatureGenerator
     *
     * Create our ViewModel to test
     *
     * Initialize our test creature list and test observer.
     */
    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        schedulerProvider = ImmediateSchedulerProvider()

        generator = CreatureGenerator()

        viewModel = AllCreaturesViewModel(AllCreaturesProcessorHolder(
                creatureRepository, schedulerProvider
        ))

        creatures = listOf(
                generator.generateCreature(CreatureAttributes(3, 7, 10), "Creature 1", 1),
                generator.generateCreature(CreatureAttributes(7, 10, 3), "Creature 2", 1),
                generator.generateCreature(CreatureAttributes(10, 3, 7), "Creature 3", 1)
        )

        testObserver = viewModel.states().test()
    }

    /*
     * Test for loading creatures from the repository
     *
     * Have our mock repository return an observable list of creatures when getAllCreatures() is
     * called
     */
    @Test
    fun loadAllCreaturesFromRepositoryAndLoadIntoView() {
        `when`(creatureRepository.getAllCreatures()).thenReturn(Observable.just(creatures))

        // Initiate the loading of creatures by passing a LoadAllCreaturesIntent into the cycle
        viewModel.processIntents(Observable.just(AllCreaturesIntent.LoadAllCreaturesIntent))

        // Assert that the Loading state is emitted first after the default state, and that a
        // non-loading state is emitted next
        testObserver.assertValueAt(1, AllCreaturesViewState::isLoading)
        testObserver.assertValueAt(2) { allCreaturesViewState: AllCreaturesViewState ->
            !allCreaturesViewState.isLoading
        }
    }

    /*
     * Test to make sure an error is sent when there is an error loading creatures from the repository
     *
     * Mock an error when getAllCreatures is called on the repository.
     *
     * Inititiate the laoding of creatures and assert that a non-null error state is passed into the view.
     */
    @Test
    fun errorLoadingCreaturesShowsError() {
        `when`(creatureRepository.getAllCreatures()).thenReturn(Observable.error(Exception()))

        viewModel.processIntents(Observable.just(AllCreaturesIntent.LoadAllCreaturesIntent))

        testObserver.assertValueAt(2) { state -> state.error != null }
    }

}