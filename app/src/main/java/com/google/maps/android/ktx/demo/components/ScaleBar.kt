package com.google.maps.android.ktx.demo.components

import android.graphics.Point
import android.util.Log
import androidx.compose.foundation.Canvas
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
import com.google.maps.android.ktx.cameraProjectionEvents
import com.google.maps.android.ktx.utils.sphericalDistance
import kotlinx.coroutines.ExperimentalCoroutinesApi

val DarkGray = Color(0xFF3a3c3b)

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ScaleBar(
    googleMap: GoogleMap,
    textColor: Color = DarkGray,
    lineColor: Color = DarkGray,
    shadowColor: Color = Color.White
) {
    val projection: Projection by googleMap.cameraProjectionEvents().collectAsState(googleMap.projection)

    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 50.dp)
            .padding(5.dp)
    ) {
        var twoThirdsCanvasInMeters by remember {
            mutableStateOf(0)
        }
        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                // Get width of canvas in meters
                val upperLeftLatLng = projection.fromScreenLocation(Point(0, 0))
                val upperRightLatLng = projection.fromScreenLocation(Point(0, size.width.toInt()))
                val canvasWidthMeters = upperLeftLatLng.sphericalDistance(upperRightLatLng)
                Log.d("Distance", "Canvas width (meters): $canvasWidthMeters")

                twoThirdsCanvasInMeters = ((canvasWidthMeters * 2 / 3).toInt())

                val oneNinthWidth = size.width / 9
                val oneThirdWidth = size.width / 3
                val midHeight = size.height / 2
                val oneFifthHeight = size.height / 5
                val fourFifthsHeight = size.height * 4 / 5
                val strokeWidth = 4f

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
                    start = Offset(oneThirdWidth, oneFifthHeight),
                    end = Offset(oneThirdWidth, midHeight),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Bottom vertical line
                drawLine(
                    color = lineColor,
                    start = Offset(oneThirdWidth, midHeight),
                    end = Offset(oneThirdWidth, fourFifthsHeight),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        )
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            ScaleText(
                modifier = Modifier.align(End),
                textColor = textColor,
                shadowColor = shadowColor,
                text = "${toFeet(twoThirdsCanvasInMeters.toDouble()).toInt()} ft")
            ScaleText(
                modifier = Modifier.align(End),
                textColor = textColor,
                shadowColor = shadowColor,
                text = "$twoThirdsCanvasInMeters m")
        }
    }
}

@Composable
private fun ScaleText(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = DarkGray,
    shadowColor: Color = Color.White
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
                blurRadius = 2f
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