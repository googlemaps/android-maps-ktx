package com.google.maps.android.ktx.demo.components

import android.graphics.Point
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.maps.android.ktx.cameraMoveEvents
import com.google.maps.android.ktx.utils.sphericalDistance
import kotlinx.coroutines.ExperimentalCoroutinesApi

val DarkGray = Color(0xFF3a3c3b)

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ScaleBar(
    modifier: Modifier = Modifier,
    googleMap: GoogleMap,
    textColor: Color = if (!isSystemInDarkTheme()) DarkGray else Color.White,
    lineColor: Color = if (!isSystemInDarkTheme()) DarkGray else Color.White,
    shadowColor: Color = if (!isSystemInDarkTheme()) Color.White else DarkGray,
) {
    val projection: Projection by googleMap.cameraMoveEvents()
        .collectAsState(googleMap.projection)

    Box(
        modifier = modifier
            .size(width = 65.dp, height = 50.dp)
    ) {
        var eightNinthsCanvasMeters by remember {
            mutableStateOf(0)
        }
        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                // Get width of canvas in meters
                val upperLeftLatLng = projection.fromScreenLocation(Point(0, 0))
                val upperRightLatLng = projection.fromScreenLocation(Point(0, size.width.toInt()))
                val canvasWidthMeters = upperLeftLatLng.sphericalDistance(upperRightLatLng)

                eightNinthsCanvasMeters = (canvasWidthMeters * 8 / 9).toInt()

                val oneNinthWidth = size.width / 9
                val midHeight = size.height / 2
                val oneThirdHeight = size.height / 3
                val twoThirdsHeight = size.height * 2 / 3
                val strokeWidth = 4f
                val shadowStrokeWidth = strokeWidth + 3

                // Middle horizontal line shadow (drawn under main lines)
                drawLine(
                    color = shadowColor,
                    start = Offset(oneNinthWidth, midHeight),
                    end = Offset(size.width, midHeight),
                    strokeWidth = shadowStrokeWidth,
                    cap = StrokeCap.Round
                )
                // Top vertical line shadow (drawn under main lines)
                drawLine(
                    color = shadowColor,
                    start = Offset(oneNinthWidth, oneThirdHeight),
                    end = Offset(oneNinthWidth, midHeight),
                    strokeWidth = shadowStrokeWidth,
                    cap = StrokeCap.Round
                )
                // Bottom vertical line shadow (drawn under main lines)
                drawLine(
                    color = shadowColor,
                    start = Offset(oneNinthWidth, midHeight),
                    end = Offset(oneNinthWidth, twoThirdsHeight),
                    strokeWidth = shadowStrokeWidth,
                    cap = StrokeCap.Round
                )

                // Middle horizontal line
                drawLine(
                    color = lineColor,
                    start = Offset(oneNinthWidth, midHeight),
                    end = Offset(size.width, midHeight),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Top vertical line
                drawLine(
                    color = lineColor,
                    start = Offset(oneNinthWidth, oneThirdHeight),
                    end = Offset(oneNinthWidth, midHeight),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Bottom vertical line
                drawLine(
                    color = lineColor,
                    start = Offset(oneNinthWidth, midHeight),
                    end = Offset(oneNinthWidth, twoThirdsHeight),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            var metricUnits = "m"
            var metricDistance = eightNinthsCanvasMeters
            if (eightNinthsCanvasMeters > 1000) {
                // Switch from meters to kilometers as unit
                metricUnits = "km"
                metricDistance /= 1000
            }

            var imperialUnits = "ft"
            var imperialDistance = toFeet(eightNinthsCanvasMeters.toDouble())
            if (imperialDistance > 5280) {
                // Switch from ft to miles as unit
                imperialUnits = "mi"
                imperialDistance = toMiles(imperialDistance)
            }

            ScaleText(
                modifier = Modifier.align(End),
                textColor = textColor,
                shadowColor = shadowColor,
                text = "${imperialDistance.toInt()} $imperialUnits"
            )
            ScaleText(
                modifier = Modifier.align(End),
                textColor = textColor,
                shadowColor = shadowColor,
                text = "$metricDistance $metricUnits"
            )
        }
    }
}

@Composable
private fun ScaleText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = DarkGray,
    shadowColor: Color = Color.White,
) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = textColor,
        textAlign = TextAlign.End,
        modifier = modifier,
        style = MaterialTheme.typography.h4.copy(
            shadow = Shadow(
                color = shadowColor,
                offset = Offset(2f, 2f),
                blurRadius = 1f
            )
        )
    )
}

/**
 * Converts the provide value in meters to the corresponding value in feet
 * @param meters value in meters to convert to feet
 * @return the provided meters value converted to feet
 */
fun toFeet(meters: Double): Double {
    return meters * 1000.0 / 25.4 / 12.0
}

/**
 * Converts the provide value in feet to the corresponding value in miles
 * @param feet value in feet to convert to miles
 * @return the provided feet value converted to miles
 */
fun toMiles(feet: Double): Double {
    return feet / 5280
}