package co.softov.morestuff.android.domain.model

import co.softov.morestuff.android.domain.Failure

typealias SimpleResult<T> = Result<T, Throwable>
typealias SuccessResult<T> = Result.Success<T>
typealias FailureResult = Result.Failure<Failure>