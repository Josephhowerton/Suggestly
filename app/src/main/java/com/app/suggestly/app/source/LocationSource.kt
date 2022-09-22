package com.app.suggestly.app.source

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.HandlerThread
import androidx.annotation.VisibleForTesting
import com.google.android.gms.location.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

@VisibleForTesting
class LocationSource constructor(application: Application) : FusedLocationProviderClient(application) {
    private val locationUpdatesBehaviorSubject: BehaviorSubject<Location> = BehaviorSubject.create()
    private val locationSourceThread: HandlerThread = HandlerThread("LocationSource")
    private val locationCallback: LocationCallback
    private val locationRequest: LocationRequest
    var isLocationUpdatesActive = false
        private set

    fun subscribeToLocationUpdates(locationObserver: Observer<Location>) {
        locationUpdatesBehaviorSubject
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .debounce(1000, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribe(locationObserver)
    }

    fun unsubscribeToLocationUpdates() {
        removeLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(): Single<Boolean> =
        Single.create{ emitter ->
            requestLocationUpdates(locationRequest, locationCallback, locationSourceThread.looper)
                .addOnCanceledListener {
                    isLocationUpdatesActive = true
                    emitter.onSuccess(false)
                }
                .addOnSuccessListener {
                    emitter.onSuccess(true)
                }
                .addOnFailureListener {
                    emitter.onSuccess(false)
                }
        }

    private fun removeLocationUpdates() {
        isLocationUpdatesActive = false
        removeLocationUpdates(locationCallback)
    }

    init {
        locationSourceThread.start()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    locationUpdatesBehaviorSubject.onNext(it)
                }
            }
        }
        locationRequest = LocationRequest.create()
        locationRequest.setSmallestDisplacement(ONE_MILE)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setInterval(TEN_SECONDS).fastestInterval = TEN_SECONDS
    }

    private companion object{
        const val TEN_SECONDS = 10000L
        const val ONE_MILE = 100F
    }
}