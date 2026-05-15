package com.example.akshara_deepatutor.ui.screens

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.ui.navigation.Screen
import com.example.akshara_deepatutor.ui.viewmodels.QuizViewModel
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavHostController, 
    chapterId: Int = -1,
    subjectName: String = "none",
    windowWidthSizeClass: WindowWidthSizeClass,
) {
    val context = LocalContext.current
    val viewModel: QuizViewModel = viewModel(
        factory = QuizViewModel.provideFactory(context.applicationContext as Application),
    )

    val questions by viewModel.questions.collectAsState()
    
    LaunchedEffect(chapterId, subjectName) {
        viewModel.loadQuestions(chapterId, subjectName)
    }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswers by remember { mutableStateOf(List(questions.size) { -1 }) }
    var timeInSeconds by remember { mutableIntStateOf(300) } // 5 minutes

    val selectedOption = selectedAnswers[currentQuestionIndex]

    // Timer Logic
    LaunchedEffect(Unit) {
        while (timeInSeconds > 0) {
            delay(1000)
            timeInSeconds--
        }
        // Auto-finish quiz when time runs out
        if ((timeInSeconds == 0) && questions.isNotEmpty()) {
            val score = selectedAnswers.indices.count { i -> 
                selectedAnswers[i] == questions[i].correctAnswer 
            } * (100 / questions.size)
            
            viewModel.finishQuiz(score, chapterId)
            navController.navigate(Screen.Result.createRoute(score))
        }
    }

    // Progress Logic
    val progress = (currentQuestionIndex + 1).toFloat() / questions.size
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500),
        label = "Progress"
    )

    val isCompact = windowWidthSizeClass == WindowWidthSizeClass.Compact

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (chapterId != -1) "Chapter Quiz" else "$subjectName Final Quiz", 
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TimerChip(timeInSeconds)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Progress Bar Section
            QuizProgressHeader(
                current = currentQuestionIndex + 1,
                total = questions.size,
                progress = animatedProgress
            )

            // Center Question Section with Transitions
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                AnimatedContent(
                    targetState = currentQuestionIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally { it } + fadeIn()) togetherWith
                                    (slideOutHorizontally { -it } + fadeOut())
                        } else {
                            (slideInHorizontally { -it } + fadeIn()) togetherWith
                                    (slideOutHorizontally { it } + fadeOut())
                        }.using(SizeTransform(clip = false))
                    },
                    label = "QuestionTransition"
                ) { index ->
                    val question = questions[index]
                    
                    if (isCompact) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            QuestionCard(question.question)
                            Spacer(modifier = Modifier.height(24.dp))
                            OptionsList(question.options, selectedAnswers[index]) { optIndex ->
                                val newList = selectedAnswers.toMutableList()
                                newList[index] = optIndex
                                selectedAnswers = newList
                            }
                        }
                    } else {
                        // Tablet/Expanded: Side-by-Side Question and Options
                        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                QuestionCard(question.question)
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                OptionsList(question.options, selectedAnswers[index]) { optIndex ->
                                    val newList = selectedAnswers.toMutableList()
                                    newList[index] = optIndex
                                    selectedAnswers = newList
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation Section
            QuizNavigationButtons(
                isFirst = currentQuestionIndex == 0,
                isLast = (currentQuestionIndex == (questions.size - 1)),
                isOptionSelected = selectedOption != -1,
                onPrevious = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                onNext = { if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++ },
            ) {
                if (questions.isNotEmpty()) {
                    val score = selectedAnswers.indices.count { i ->
                        selectedAnswers[i] == questions[i].correctAnswer
                    } * (100 / questions.size)

                    viewModel.finishQuiz(score, chapterId)
                    navController.navigate(Screen.Result.createRoute(score))
                }
            }
        }
    }
}

@Composable
fun OptionsList(options: List<String>, selectedOption: Int, onOptionSelected: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEachIndexed { optIndex, option ->
            OptionCard(
                text = option,
                isSelected = selectedOption == optIndex,
            ) { onOptionSelected(optIndex) }
        }
    }
}

@Composable
fun TimerChip(seconds: Int) {
    val minutes = seconds / 60
    val secs = seconds % 60
    val timerText = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
    
    Row(
        modifier = Modifier
            .padding(end = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Timer, 
            contentDescription = null, 
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = timerText,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun QuizProgressHeader(current: Int, total: Int, progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Question $current of $total",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
fun QuestionCard(questionText: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = questionText,
            modifier = Modifier.padding(24.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 28.sp
        )
    }
}

@Composable
fun OptionCard(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(300),
        label = "BorderColor"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "BgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(300),
        label = "TextColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
fun QuizNavigationButtons(
    isFirst: Boolean,
    isLast: Boolean,
    isOptionSelected: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (!isFirst) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("Previous", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
        }

        Button(
            onClick = { if (isLast) onFinish() else onNext() },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            enabled = isOptionSelected,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            )
        ) {
            Text(
                text = if (isLast) "Finish" else "Next",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}