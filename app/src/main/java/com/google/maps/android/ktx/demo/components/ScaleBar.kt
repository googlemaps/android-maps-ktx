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
                // Middle horizontal line
                drawLine(
                    color = DarkGray,
                    start = Offset(0f, midHeight),
                    end = Offset(size.width - 1, midHeight),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                // Top vertical line
                drawLine(
                    color = DarkGray,
                    start = Offset(midWidth, 0f),
                    end = Offset(midWidth, size.height / 2),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
                // Bottom vertical line
                drawLine(
                    color = DarkGray,
                    start = Offset(midWidth / 2, size.height / 2),
                    end = Offset(midWidth / 2, size.height - 1),
                    strokeWidth = 4f,
                    cap = StrokeCap.Round
                )
            }
        )
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            ScaleText(modifier = Modifier.align(End), text = "1000 ft")
            Spacer(modifier = Modifier.padding(1.dp))
            ScaleText(modifier = Modifier.align(End), text = "500 m")
        }
    }
}

@Composable
fun ScaleText(
    modifier: Modifier = Modifier,
    text: String) {
    Text(
        text = text,
        fontSize = 10.sp,
        color = DarkGray,
        textAlign = TextAlign.End,
        modifier = modifier,
        style = MaterialTheme.typography.h4.copy(
            shadow = Shadow(
                color = Color.White,
                offset = Offset(4f, 4f),
                blurRadius = 8f
            )
        )
    )
}