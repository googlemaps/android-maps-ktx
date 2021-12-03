package com.google.maps.android.ktx.demo.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.google.maps.android.ktx.cameraMoveEvents
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
    val move = googleMap.cameraMoveEvents().collectAsState(initial = Unit)

    val projection = googleMap.projection

    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 50.dp)
            .padding(5.dp)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {
                val midWidth = size.width / 2
                val midHeight = size.height / 2
                val oneFifthHeight = size.height / 5
                val fourFifthsHeight = size.height * 4 / 5
                val strokeWidth = 4f

                // Middle horizontal line
                drawLine(
                    color = lineColor,
                    start = Offset(0f, midHeight),
                    end = Offset(size.width - 1, midHeight),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Top vertical line
                drawLine(
                    color = lineColor,
                    start = Offset(midWidth, oneFifthHeight),
                    end = Offset(midWidth, midHeight),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                // Bottom vertical line
                drawLine(
                    color = lineColor,
                    start = Offset(midWidth / 2, midHeight),
                    end = Offset(midWidth / 2, fourFifthsHeight),
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
                text = "1000 ft")
            ScaleText(
                modifier = Modifier.align(End),
                textColor = textColor,
                shadowColor = shadowColor,
                text = "500 m")
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