package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import com.stevedenheyer.starwarsdestinydeckbuilder.R

enum class DieIcon(val code: String, val inlineTag: String, val resourceId: Int) {
    BLANK("-", "blank", R.drawable.swd01_blank_symbol_w),
    DISCARD("Dc", "discard", R.drawable.swd01_discard_symbol_w),
    DISRUPT("Dr", "disrupt", R.drawable.swd01_disrupt_symbol_w),
    FOCUS("F", "focus", R.drawable.swd01_focus_symbol_w),
    INDIRECT("ID", "indirect", R.drawable.swd01_indirect_damage_symbol_w),
    MELEE("MD", "melee", R.drawable.swd01_melee_damage_symbol_w),
    RANGED("RD", "ranged", R.drawable.swd01_ranged_damage_symbol_w),
    RESOURCE("R", "resource", R.drawable.swd01_resource_symbol_o),
    SHIELD("Sh", "shield", R.drawable.swd01_shield_symbol_w),
    SPECIAL("Sp", "special", R.drawable.swd01_special_symbol_w),
}
