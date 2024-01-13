package com.citawarisan.quiz

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
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

@Composable
fun QuizView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var data by remember { mutableStateOf(JSONObject()) }
    var correct by remember { mutableIntStateOf(0) }
    var total by remember { mutableIntStateOf(0) }

//    https://stackoverflow.com/questions/46177133/
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
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
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
            for (i in 0..<wrongAnswers.length()) {
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
                            Toast.makeText(context, "Answer: $correctAnswer", Toast.LENGTH_SHORT)
                                .show()
                            getQuestion()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(answer) }
                }
            }
        }
        Spacer(modifier = modifier.weight(1.0f))
        Row(horizontalArrangement = Arrangement.Center) {
            Text("Score: $correct / $total", fontSize = 24.sp)
        }
    }

}
