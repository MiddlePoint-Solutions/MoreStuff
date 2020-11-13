package co.softov.morestuff.androidApp.domain.model

import co.softov.morestuff.androidApp.domain.Failure

typealias SimpleResult<T> = Result<T, Throwable>
typealias SuccessResult<T> = Result.Success<T>
typealias FailureResult = Result.Failure<Failure>