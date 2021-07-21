package com.dsmmachina.marvelmadness.base

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class BaseObserver<T : Any> : Observer<T> {

    override fun onSubscribe(disposable : Disposable) {
        /** no default behavior */
    }

    override fun onComplete() {
        /** no default behavior */
    }

    override fun onError(error : Throwable) {
        /** no default behavior */
    }

    override fun onNext(next : T) {
        /** no default behavior */
    }
}