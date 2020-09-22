package com.clloret.speakingpractice.exercise.list

import androidx.appcompat.widget.SearchView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

object RxSearchObservable {
    fun fromView(searchView: SearchView): Observable<String> {
        val subject = PublishSubject.create<String>()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String): Boolean {
                Timber.d("onQueryTextSubmit: %s", text)
                subject.onNext(text)
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                Timber.d("onQueryTextChange: %s", text)
                subject.onNext(text)
                return true
            }
        })
        return subject
    }
}
