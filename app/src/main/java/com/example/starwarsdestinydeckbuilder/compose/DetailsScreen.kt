package com.example.starwarsdestinydeckbuilder.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.starwarsdestinydeckbuilder.viewmodel.DetailViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailsScreen(isCompactScreen: Boolean,
                    modifier:Modifier = Modifier,
                  detailViewModel: DetailViewModel = hiltViewModel()) {

    val card by detailViewModel.card.collectAsStateWithLifecycle(initialValue = null)

    Row(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            Text("${card?.name ?: ""} - ${card?.subtitle ?: ""}")
            Text("${card?.affiliation}. ${card?.faction}. ${card?.rarity}")
            Text("${card?.subtypes?.joinToString(separator = ". ")} Points: ${card?.points} Health: ${card?.health}")
            Text("${card?.die1} ${card?.die2} ${card?.die3} ${card?.die4} ${card?.die5} ${card?.die6}")
            Text(card?.text ?: "")
            Text(card?.illustrator ?: "")
            Text("${card?.setName} #${card?.position}")
            if (!card?.reprints.isNullOrEmpty()) {
                Text("Reprinted in:")
                card?.reprints!!.forEach {
                    Text("${it.setName} #${it.position}")
                }
            }
            if (!card?.parellelDice.isNullOrEmpty()) {
                Text("Used as parallel die by:")
                card?.parellelDice!!.forEach {
                    Text("${it.setName} #${it.position}")
                }
            }
            Text(card?.formats.toString())
        }
        GlideImage(model = card?.imagesrc, contentDescription = "", modifier = Modifier.weight(1f))
    }

}