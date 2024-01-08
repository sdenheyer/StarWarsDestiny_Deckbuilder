package com.example.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun die(modifier: Modifier, dieCode: String) {
    var value = ""
    var dieIcon: Int? = null
    var cost = ""

    if (dieCode != "") {
        value = if (dieCode.substring(0, 0) == "+") dieCode.substring(0, 1) else dieCode.substring(0, 0)
        }

    for (entry in DieIcon.entries) {
        if (dieCode.contains(entry.code)) {
            dieIcon = entry.resourceId
        }
    }

    cost = dieCode.last().digitToIntOrNull().toString()

    Log.d("SWD", "Value: $value, Icon: ${dieIcon.toString()}, Cost: $cost")

    Row(modifier) {

    }
}