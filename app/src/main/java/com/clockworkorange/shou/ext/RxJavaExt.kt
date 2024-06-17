package com.clockworkorange.shou.ext

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


fun Disposable.addTo(compositeDisposable: CompositeDisposable){
    compositeDisposable.add(this)
}