package com.github.huymaster.materialweather.core.engine.node

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.github.huymaster.materialweather.R
import com.github.huymaster.materialweather.core.engine.Node
import com.github.huymaster.materialweather.core.engine.NodeException
import com.github.huymaster.materialweather.core.engine.NodeExecutionEngine
import com.github.huymaster.materialweather.core.engine.NodeParam
import com.github.huymaster.materialweather.core.engine.serialization.RestoreData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

class LocationFetcherNode(data: RestoreData = RestoreData.EMPTY) : Node(data) {
    override val name: Int = R.string.node_location_fetch

    private val latitude =
        NodeParam.output<Double>(LATITUDE_KEY, data.getParamId(LATITUDE_KEY))
    private val longitude =
        NodeParam.output<Double>(LONGITUDE_KEY, data.getParamId(LONGITUDE_KEY))

    override fun getInputs(): Set<NodeParam.Input<*>> = emptySet()
    override fun getOutputs(): Set<NodeParam.Output<*>> = setOf(latitude, longitude)

    override suspend fun execute(context: NodeExecutionEngine.ExecutionContext) {
        val coarse = ContextCompat.checkSelfPermission(
            context.androidContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val fine = ContextCompat.checkSelfPermission(
            context.androidContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (coarse != 0 && fine != 0)
            throw NodeException.CannotGetLocation(R.string.exception_cannot_get_location_no_permission)
        runCatching {
            fetchCurrentLocation(context.androidContext)
        }.onSuccess { location ->
            context.set(latitude, location.latitude)
            context.set(longitude, location.longitude)
        }.onFailure {
            if (it is NodeException.CannotGetLocation)
                throw it
            if (it is CancellationException) return@onFailure
            throw it
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun fetchCurrentLocation(
        context: Context
    ): Location {
        val service: LocationManager =
            context.getSystemService(LocationManager::class.java) ?: skip()

        val isGpsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = service.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled)
            throw NodeException.CannotGetLocation(R.string.exception_cannot_get_location_service_disabled)

        val lastKnownLocation = service.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (lastKnownLocation != null) return lastKnownLocation

        return suspendCancellableCoroutine { continuation ->
            val provider =
                if (isGpsEnabled) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER

            val cancellationSignal = CancellationSignal()
            continuation.invokeOnCancellation {
                cancellationSignal.cancel()
            }

            LocationManagerCompat.getCurrentLocation(
                service,
                provider,
                cancellationSignal,
                ContextCompat.getMainExecutor(context),
                continuation::resume
            )
        }
    }

    private companion object {
        const val LATITUDE_KEY = "latitude"
        const val LONGITUDE_KEY = "longitude"
    }
}