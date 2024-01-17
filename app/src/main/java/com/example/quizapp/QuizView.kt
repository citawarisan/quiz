<<<<<<< Updated upstream:app/src/main/java/com/example/quizapp/QuizView.kt
package com.example.quizapp

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavHostController
import com.example.quizapp.sign_in.UserData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread
import com.google.firebase.database.database


@Composable
fun QuizView(
    userData: UserData?,
    navController: NavHostController,
    modifier: Modifier = Modifier) {
    var data by remember { mutableStateOf(JSONObject()) }
    var correct by remember { mutableStateOf(0) }
    var total by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    val url = "https://opentdb.com/api.php?amount=1"
    fun getQuestion() {
        thread {
            do {
                try {
                    data = JSONObject(URL(url).readText())
                } catch (e: Exception) {
                    Thread.sleep(3_000)
                }
            } while (data.length() == 0)
        }

    }


    fun htmlString(of: String): String {
        return HtmlCompat.fromHtml(of, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    fun capitalizedValue(data: JSONObject, key: String): String {
        return htmlString(data[key] as String).replaceFirstChar { it.uppercase() }
    }

    getQuestion()

    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
//        item { Text(data.toString()) }
        if (data.length() == 0) {
            Spacer(modifier = modifier.weight(1.0f))
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
            Text("Getting Next Question...")
        } else if (data["response_code"] == 0) {
//            parse background story
            val question = data.getJSONArray("results").getJSONObject(0)
            Text(
                "Difficulty: ${capitalizedValue(question, "difficulty")}",
                fontSize = 24.sp,
            )
            Text("Category: ${capitalizedValue(question, "category")}")
            Spacer(modifier = modifier.weight(0.5f))
            Text(
                htmlString(question.getString("question")),
                fontSize = 24.sp,
                lineHeight = 32.sp,
            )
            Spacer(modifier = modifier.weight(1.0f))

//            parse answers
            val wrongAnswers = question.getJSONArray("incorrect_answers")
            val answers: ArrayList<String> = arrayListOf()
            val correctAnswer = question.getString("correct_answer")
            answers.add(htmlString(correctAnswer))
            for (i in 0 until wrongAnswers.length()) {
                answers.add(htmlString(wrongAnswers.getString(i)))
            }
            answers.shuffle()

//            generate answer buttons
            LazyColumn {
                items(answers) { answer ->
                    Button(
                        onClick = {
                            if (answer == correctAnswer) {
                                correct++
                            }
                            total++
                            data = JSONObject()
                            getQuestion()
                            Log.d("to", "getting")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(answer) }
                }
            }
        }
        Spacer(modifier = modifier.weight(1.0f))
        Row(horizontalArrangement = Arrangement.Center) {
            Text("Score: $correct / $total", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    isFinished = true
                    // Navigate to the final score screen
                    navController.navigate("finalScore?score=${correct}")
                },
            ) {
                Text("Finish")
            }
        }
    }

}
=======
package com.citawarisan.quiz

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import androidx.preference.PreferenceManager
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread
import kotlin.math.log

@Composable
fun QuizView(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor = sharedPreferences.edit()

    var data by remember { mutableStateOf(JSONObject()) }
    var score by remember { mutableIntStateOf(0) }
    var current by remember { mutableIntStateOf(0) }
    var screwThis by remember { mutableStateOf(false) }

    fun getQuestion() {
        thread {
            do {
                try {
                    val url = sharedPreferences.getString("url", null)!!
                    data = JSONObject(URL(url).readText())
                } catch (e: Exception) {
                    Thread.sleep(3_000)
                }
            } while (data.length() == 0 || data.getInt("response_code") != 0)
            editor.putString("questions", data.toString())
            editor.putInt("onQuestion", 0)
            editor.putInt("score", 0)
            editor.apply()
            Log.d("TEST", "getQuestion: valid question collected")
        }
    }

    fun score(): Int {
        return sharedPreferences.getInt("score", 0)
    }

    fun onQuestion(): Int {
        return sharedPreferences.getInt("onQuestion", -1)
    }

    fun htmlString(of: String): String {
        return HtmlCompat.fromHtml(of, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    fun capitalizedValue(data: JSONObject, key: String): String {
        return htmlString(data[key] as String).replaceFirstChar { it.uppercase() }
    }

    if (screwThis) {
        Log.d("TAG", "QuizView: screw this")
        Toast.makeText(
            context,
            "Total Score: ${score()} / ${data.getJSONArray("results").length()}",
            Toast.LENGTH_SHORT
        ).show()
        editor.remove("onQuestion")
        editor.remove("url")
        editor.apply()
        navController.navigate("menu")
        return
    }

    val response = sharedPreferences.getString("questions", null)
    val onQuestion = onQuestion()
    if (response == null || response == "{}" || onQuestion == -1) {
        getQuestion()
        Log.d("TEST", "QuizView: set new data")
    } else if (data.toString() == "{}") {
        data = JSONObject(response)
        Log.d("TEST", "QuizView: retrieved storage data")
    }

    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (data.length() == 0) {
//            Spacer(modifier = modifier.weight(1.0f))
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
            Text("Getting Next Question...")
        } else if (data.getInt("response_code") == 0) {
            val questions = data.getJSONArray("results")
            val length = questions.length()

//            parse question
            val question = questions.getJSONObject(current)
            Text(
                "Difficulty: ${capitalizedValue(question, "difficulty")}",
                fontSize = 24.sp,
            )
            Text("Category: ${capitalizedValue(question, "category")}")
            Spacer(modifier = modifier.weight(0.5f))
            Text(
                htmlString(question.getString("question")),
                fontSize = 24.sp,
                lineHeight = 32.sp,
            )
            Spacer(modifier = modifier.weight(1.0f))

//            parse answers
            val answers: ArrayList<String> = arrayListOf()
            val wrongAnswers = question.getJSONArray("incorrect_answers")
            for (i in 0..<wrongAnswers.length()) {
                answers.add(htmlString(wrongAnswers.getString(i)))
            }
            val correctAnswer = htmlString(question.getString("correct_answer"))
            answers.add(correctAnswer)
            answers.shuffle()

//            generate answer buttons
            LazyColumn {
                items(answers) { answer ->
                    Button(
                        onClick = {
                            if (answer == correctAnswer) {
                                score++
                            }
                            Toast.makeText(
                                context,
                                "Answer: $correctAnswer",
                                Toast.LENGTH_SHORT
                            ).show()
                            editor.putInt("onQuestion", ++current)
                            editor.apply()
                            Log.d("TAG", "QuizView: ${data.getJSONArray("results").length()} WHAT THE FYCK IS WRONG WITH YOU")
                            if (data.getJSONArray("results").length() == current) {
                                screwThis = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(answer) }
                }
            }

            Spacer(modifier = modifier.weight(1.0f))
            Row(horizontalArrangement = Arrangement.Center) {
                Text("Score: $score / $length", fontSize = 24.sp)
            }
        }
    }
}
>>>>>>> Stashed changes:app/src/main/java/com/citawarisan/quiz/QuizView.kt
