package com.citawarisan.quiz

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.preference.PreferenceManager

@Composable
fun MenuView(navController: NavController, modifier: Modifier = Modifier) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val editor = sharedPreferences.edit()

    fun setUrl(amount: Int, category: String, difficulty: String, type: String) {
        var url = "https://opentdb.com/api.php?amount=$amount"
        if (category != "any") {
            url += "&category=$category"
        }
        if (difficulty != "any") {
            url += "&difficulty=$difficulty"
        }
        if (type != "any") {
            url += "&difficulty=$type"
        }
        editor.putString("url", url)
        editor.apply()
    }

    if (sharedPreferences.contains("url")) {
        navController.navigate("quiz")
        Log.d("TAG", "MenuView: skipped menu")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            onClick = {
                setUrl(10, "any", "any", "any")
                navController.navigate("quiz")
            },
            modifier = modifier.fillMaxWidth(),
        ) {
            Text("Start Quiz")
        }
    }
}