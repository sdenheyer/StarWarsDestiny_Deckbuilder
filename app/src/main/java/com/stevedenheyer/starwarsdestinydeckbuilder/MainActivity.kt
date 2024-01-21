package com.stevedenheyer.starwarsdestinydeckbuilder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.CardListScreen
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.DetailsScreen
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.StarWarsDestinyDeckbuilderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3WindowSizeClassApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StarWarsDestinyDeckbuilderTheme {
                Surface {
                    val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
                    DestinyApp(modifier = Modifier, widthSizeClass = widthSizeClass)
                }
            }
        }
    }
}

@Composable
fun DestinyApp(modifier: Modifier = Modifier,
    widthSizeClass: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()) {

    val isCompactScreen = widthSizeClass == WindowWidthSizeClass.Compact

   NavHost(modifier = modifier, navController = navController, startDestination = "card_list") {
       composable(route = "card_list") {
           CardListScreen(isCompactScreen, modifier = modifier) { code -> navController.navigate("card_detail/${code}")}
       }

       composable(route = "card_detail/{code}", arguments = listOf(navArgument("code") {
           type = NavType.StringType
       })) {
            DetailsScreen(isCompactScreen, modifier = modifier)
       }
   }
}
