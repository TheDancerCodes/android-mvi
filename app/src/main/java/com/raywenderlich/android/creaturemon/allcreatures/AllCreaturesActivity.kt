/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.creaturemon.allcreatures

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.raywenderlich.android.creaturemon.R
import com.raywenderlich.android.creaturemon.addcreature.CreatureActivity
import com.raywenderlich.android.creaturemon.mvibase.MviView
import com.raywenderlich.android.creaturemon.util.CreaturemonViewModelFactory
import com.raywenderlich.android.creaturemon.util.visible
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_all_creatures.*
import kotlinx.android.synthetic.main.content_all_creatures.*

/**
 * Make the AllCreaturesActivity an MVI view,
 * typed by the corresponding Intent type and ViewState type.
 */
class AllCreaturesActivity : AppCompatActivity(),
        MviView<AllCreaturesIntent, AllCreaturesViewState> {

  private val adapter = CreatureAdapter(mutableListOf())

  // A PublishSubject that emits a clear all intent when a user taps clear all in the menu.
  private val clearAllCreaturesPublisher = PublishSubject.create<AllCreaturesIntent.ClearAllCreaturesIntent>()

  // Dispose of subscriptions in our activities
  private val disposables = CompositeDisposable()

  // ViewModel property
  private val viewModel: AllCreaturesViewModel by lazy(LazyThreadSafetyMode.NONE) {
    ViewModelProviders
            .of(this, CreaturemonViewModelFactory.getInstance(this))
            .get(AllCreaturesViewModel::class.java)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_all_creatures)
    setSupportActionBar(toolbar)

    creaturesRecyclerView.layoutManager = LinearLayoutManager(this)
    creaturesRecyclerView.adapter = adapter

    fab.setOnClickListener {
      startActivity(Intent(this, CreatureActivity::class.java))
    }
  }

  override fun onStart() {
    super.onStart()

    // Bind the ViewModel
    bind()
  }

  override fun onStop() {
    super.onStop()

    // Clear our Composite Disposable
    disposables.clear()
  }

  /*
   * Subscribe to the ViewModel states & tell the ViewModel to process our user intents
   *
   * Pass the render function into the ViewModel state subscription as an onNext handler
   */
  private fun bind() {
    disposables.add(viewModel.states().subscribe(this::render))
    viewModel.processIntents(intents())
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  // Publish a ClearAllCreaturesIntent when a user selects clear all in the menu
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_clear_all -> {
        clearAllCreaturesPublisher.onNext(AllCreaturesIntent.ClearAllCreaturesIntent)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  /*
   * MVI View Functions
   *
   * intents() - provides the intents that the ViewModel will subscribe to.
   * Merge the loadIntent and clearIntent observables returned by the private helper functions.
   *
   * render() - used to render a new state to the screen.
   */
  override fun intents(): Observable<AllCreaturesIntent> {
    return Observable.merge(
            loadIntent(),
            clearIntent()
    )
  }

  override fun render(state: AllCreaturesViewState) {

    // Set ProgressBar visibility based on whether the state is a LoadingState
    progressBar.visible = state.isLoading

    // Handle and empty state if our creature list is empty
    if (state.creatures.isEmpty()) {

      // Otherwise update the recycler view adapter with the list if creatures in the state.
      // Also set visibility of various views based on whether the creature list is empty
      creaturesRecyclerView.visible = false
      emptyState.visible
    } else {
      creaturesRecyclerView.visible = true
      emptyState.visible = false
      adapter.updateCreatures(state.creatures)
    }

    // Handle error state by showing a toasting to the user and logging the error
    if (state.error != null) {
      Toast.makeText(this, getString(R.string.error_loading_creatures), Toast.LENGTH_SHORT).show()
      Log.e(TAG, "Error loading creatures: ${state.error.localizedMessage}")
    }

  }

  // Function to create a Load Intent Observable
  private fun loadIntent(): Observable<AllCreaturesIntent.LoadAllCreaturesIntent> {
    return Observable.just(AllCreaturesIntent.LoadAllCreaturesIntent)
  }

  // Private function to return a clear all event observable
  // which just returns the corresponding published subject.
  private fun clearIntent(): Observable<AllCreaturesIntent.ClearAllCreaturesIntent> {
    return clearAllCreaturesPublisher
  }

  companion object {
    private const val TAG = "AllCreaturesActivity"
  }
}
