package ih.pam.pamobile_jelahjahmalang.utils

import android.location.Location

object LocationUtils {

    fun getDistance(lat1: Double, lon1: Double,lat2: Double, lon2: Double): Float{
        val result = FloatArray(1)
        Location.distanceBetween(lat1,lat2,lon1,lon2, result)
        return result[0]
    }
}