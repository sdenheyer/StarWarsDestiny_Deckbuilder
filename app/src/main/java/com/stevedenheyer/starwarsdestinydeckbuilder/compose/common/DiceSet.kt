package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DieGroup(modifier: Modifier, dieCodes: List<String>, isCompactScreen: Boolean = true) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        dieCodes.forEach {
            Die(modifier = Modifier.weight(1F, fill = true), dieCode = it, isCompactScreen)
        }
    }
}

@Composable
fun Die(modifier: Modifier = Modifier, dieCode: String, isCompactScreen: Boolean) {
    var dieIcon: Int? = null

    var value: String =
        if (dieCode.substring(0, 1) == "+") dieCode.substring(0, 2) else dieCode.substring(0, 1)
    value = if (value.toIntOrNull() == null) "" else value

    for (entry in DieIcon.entries) {
        if (dieCode.contains(entry.code)) {
            dieIcon = entry.resourceId
            break
        }
    }

    val cost: String = dieCode.last().digitToIntOrNull().toString()

    BoxWithConstraints(
        modifier
           // .wrapContentSize(align = Alignment.Center)
    ) {
        val fontSize = this.maxHeight.value.sp
        val style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))

    if (cost.isNotBlank() && cost != "null") {
        if (isCompactScreen) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(Modifier.weight(1F), verticalAlignment = Alignment.CenterVertically) {
                    if (value.isNotBlank() && value != "-")
                        Text(text = value, fontSize = fontSize / 2, style = style)
                    if (dieIcon != null)
                        Image(
                            painter = painterResource(id = dieIcon),
                            contentDescription = "Die Reference",
                            contentScale = ContentScale.FillHeight,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                            modifier = Modifier.fillMaxHeight()
                        )
                }

                Row(Modifier.weight(1F), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = cost, fontSize = fontSize / 2, style = style)
                    Image(
                        painter = painterResource(id = DieIcon.RESOURCE.resourceId),
                        contentDescription = "Die Reference",
                        contentScale = ContentScale.FillHeight,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
        } else {
            Row {
                if (value.isNotBlank() && value != "-")
                    Text(text = value, fontSize = fontSize, style = style)
                if (dieIcon != null)
                    Image(
                        painter = painterResource(id = dieIcon),
                        contentDescription = "Die Reference",
                        contentScale = ContentScale.FillHeight,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface) ,
                        modifier = Modifier.fillMaxHeight()
                    )
                Text(text = "/$cost", fontSize = fontSize * 0.7, style = style.merge(TextStyle(baselineShift = BaselineShift(-0.3F))))
            }
        }
    } else {
        Row {
            if (value.isNotBlank() && value != "-")
                Text(
                    text = value,
                    fontSize = fontSize,
                    style = style
                )
            if (dieIcon != null)
                Image(
                    painter = painterResource(id = dieIcon),
                    contentDescription = "Die Reference",
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.fillMaxHeight()
                )
        }
    }
    }
}

@Preview
@Composable
fun DiePreview() {
    Die(
        modifier = Modifier.background(color = Color.Gray)
            .height(30.dp)
        ,
        dieCode = "2RD1",
        true
    )
}

@Preview
@Composable
fun DieGroupPreview() {
    val dieGroup = listOf("2RD", "+1MD", "2RD1", "1R", "-")
    DieGroup(modifier = Modifier.background(Color.Black)
        .height(30.dp)
        .fillMaxWidth()
        .width(1200.dp)
        , dieCodes = dieGroup, isCompactScreen = false)
}