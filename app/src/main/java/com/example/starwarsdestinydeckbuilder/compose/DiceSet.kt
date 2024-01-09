package com.example.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starwarsdestinydeckbuilder.R
import java.lang.NumberFormatException

enum class DieIcon(val code: String, val resourceId: Int) {
    BLANK("-", R.drawable.swd01_blank_symbol_w),
    DISCARD("Dc", R.drawable.swd01_discard_symbol_w),
    DISRUPT("Dr", R.drawable.swd01_disrupt_symbol_w),
    FOCUS("F", R.drawable.swd01_focus_symbol_w),
    INDIRECT("ID", R.drawable.swd01_indirect_damage_symbol_w),
    MELEE("MD", R.drawable.swd01_melee_damage_symbol_w),
    RANGED("RD", R.drawable.swd01_ranged_damage_symbol_w),
    RESOURCE("R", R.drawable.swd01_resource_symbol_o),
    SHIELD("Sh", R.drawable.swd01_shield_symbol_w),
    SPECIAL("Sp", R.drawable.swd01_special_symbol_w),
}

@Composable
fun DieGroup(modifier: Modifier, dieCodes:List<String>) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        dieCodes.forEach {
            Die(modifier = modifier.padding(horizontal = 2.dp), dieCode = it)
        }
    }
}

@Composable
fun Die(modifier: Modifier, dieCode: String) {
    var value:String
    var dieIcon: Int? = null
    var cost:String

    value = if (dieCode.substring(0, 1) == "+") dieCode.substring(0, 2) else dieCode.substring(0, 1)
    value = if (value.toIntOrNull() == null) "" else value

    for (entry in DieIcon.entries) {
        if (dieCode.contains(entry.code)) {
            dieIcon = entry.resourceId
            break
        }
    }

    cost = dieCode.last().digitToIntOrNull().toString()

    if (cost.isNotBlank() && cost != "null") {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier.weight(1F, fill = false), verticalAlignment = Alignment.Top) {
            if (value.isNotBlank() && value != "-")
                Text(text = value, fontSize = 16.sp)
            if (dieIcon != null)
                Icon(painter = painterResource(id = dieIcon), contentDescription = "Die Reference")
        }

            Row(modifier.weight(1F), verticalAlignment = Alignment.CenterVertically) {
                Text(text = cost, fontSize = 16.sp)
                Icon(
                    painter = painterResource(id = DieIcon.RESOURCE.resourceId),
                    contentDescription = "Die Reference",
                    tint = Color.Unspecified
                )
            }
        }
    } else {
        Row(modifier.wrapContentSize(align = Alignment.Center)) {
            if (value.isNotBlank() && value != "-")
                Text(text = value, fontSize = 24.sp)
            if (dieIcon != null)
                Icon(painter = painterResource(id = dieIcon), contentDescription = "Die Reference")
        }
    }
}

@Preview
@Composable
fun diePreview() {
    Die(modifier = Modifier
        .height(60.dp)
        .wrapContentSize(align = Alignment.Center), dieCode = "Sp")
}

@Preview
@Composable
fun dieGroupPreview() {
    val dieGroup = listOf("2RD", "+1MD", "2RD1", "1R", "-")
    DieGroup(modifier = Modifier.height(60.dp), dieCodes = dieGroup)
}