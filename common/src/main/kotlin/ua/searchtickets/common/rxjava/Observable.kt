package ua.searchtickets.common.rxjava

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun <E> Observable<E>.onError(
    clearErrorEffect: E,
    delay: Long = 3,
    errorEffect: (Throwable) -> E
): Observable<E> = onErrorResumeNext { error: Throwable ->
  Observable.timer(delay, TimeUnit.SECONDS)
      .map<E> { clearErrorEffect }
      .startWith(errorEffect(error))
}
