package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.LocalFactionColorScheme


@Composable
fun DieGroup(modifier: Modifier = Modifier, dieCodes: List<String>, isCompactScreen: Boolean = true) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        dieCodes.forEach {
            Die(modifier = modifier.weight(1F, fill = true), dieCode = it, isCompactScreen)
        }
    }
}

@Composable
fun Die(modifier: Modifier = Modifier, dieCode: String, isCompactScreen: Boolean) {

    val dieResourceMap = DieIcon.entries.associate {
       Pair(it.code, it.resourceId)
    }

    val dieRegexString = buildString {
        append("(\\+\\d+)|\\d+|i|X")
        DieIcon.entries.forEach {
            append("|(" + it.code + ")")
            }
        }

    val dieRegex = Regex(dieRegexString)

    val dieStrings = dieRegex.findAll(dieCode).map { it.groupValues.first() }.toList()

  //  Log.d("SWD", "die strings: ${dieStrings}")



  /*  var dieIcon: Int? = null

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
*/
    BoxWithConstraints(
        modifier
           // .wrapContentSize(align = Alignment.Center)
    ) {
        val fontSize = this.maxHeight.value.sp
        val style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))

        var color = MaterialTheme.colorScheme.onSurface

        if (dieStrings.isEmpty()) {
            Box(Modifier.fillMaxSize())
        } else {
            if (dieStrings.last().isNumber()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(Modifier.weight(1F), verticalAlignment = Alignment.CenterVertically) {

                        if (dieStrings.first().startsWith("+")) color = Color.Blue

                        var indexOfIcon = 1
                        if (dieStrings.first().isNumber())
                            Text(
                                text = dieStrings.first(),
                                fontSize = fontSize / 2,
                                style = style,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        else indexOfIcon = 0

                        val dieResource =
                            try {
                                dieResourceMap[dieStrings[indexOfIcon]] ?: R.drawable.swd01_blank_symbol_w }
                            catch (e:IndexOutOfBoundsException) {
                                R.drawable.swd01_blank_symbol_w
                            }

                        Image(
                            painter = painterResource(id = dieResource),
                            contentDescription = "Die Reference",
                            contentScale = ContentScale.FillHeight,
                            colorFilter = ColorFilter.tint(color),
                            modifier = Modifier.fillMaxHeight()
                        )
                    }

                    val dieResource =
                        if (dieStrings.contains("i")) R.drawable.swd01_indirect_damage_symbol_w
                        else R.drawable.swd01_resource_symbol_o

                    color =
                        if (dieStrings.contains("i")) Color.Red else LocalFactionColorScheme.current.factionYellow

                    val cost = dieStrings.last { it.isNumber() }

                    Row(Modifier.weight(1F), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = cost,
                            fontSize = fontSize / 2,
                            style = style,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Image(
                            painter = painterResource(id = dieResource),
                            contentDescription = "Die Reference",
                            contentScale = ContentScale.FillHeight,
                            colorFilter = ColorFilter.tint(color),
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            } else {
                Row {

                    val dieResource =
                        (if (dieStrings.first().isNumber()) dieResourceMap[dieStrings[1]]
                        else dieResourceMap[dieStrings.first()]) ?: R.drawable.swd01_blank_symbol_w

                    when (val s = dieStrings.first()) {
                        "-" -> color = Color.Red
                        else -> if (s.startsWith("+")) color = Color.Blue
                    }

                    if (dieStrings.first().isNumber())
                        Text(
                            text = dieStrings.first(),
                            fontSize = fontSize,
                            style = style,
                            color = color
                        )
                    Image(
                        painter = painterResource(
                            id =
                            dieResource
                        ),
                        contentDescription = "Die Reference",
                        contentScale = ContentScale.FillHeight,
                        colorFilter = ColorFilter.tint(color),
                        modifier = Modifier.fillMaxHeight()
                    )
                    //Text(text = "/$cost", fontSize = fontSize * 0.7, style = style.merge(TextStyle(baselineShift = BaselineShift(-0.3F))))
                }
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
        dieCode = "Sp1",
        false
    )
}

@Preview
@Composable
fun DieGroupPreview() {
    val dieGroup = listOf("XRD", "+1MD", "2RD1", "1MDi1", "-")
    DieGroup(modifier = Modifier.background(Color.Black)
        .height(30.dp)
        .fillMaxWidth()
        .width(1200.dp)
        , dieCodes = dieGroup, isCompactScreen = false)
}

@Preview
@Composable
fun Regex() {

    val dieCode = "+1Dc3"

    val dieRegexString = buildString {
        append("(\\+\\d+)|\\d+|i")
        DieIcon.entries.forEach {
            append("|(" + it.code + ")")
        }
    }
    val dieRegex = Regex(dieRegexString)

    val dieStrings = dieRegex.findAll(dieCode).map { it.groupValues.first() }.toList()
    Box(Modifier.fillMaxSize())
    Text(dieRegexString)
    Text(
        dieStrings.toString(),
        modifier = Modifier.fillMaxSize(),
        color = Color.White

    )
}

fun String.isNumber(): Boolean {
    return this.matches(Regex("""(\+\d+)|\d+|X"""))
}