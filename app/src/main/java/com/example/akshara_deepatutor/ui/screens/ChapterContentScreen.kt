package com.example.akshara_deepatutor.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.akshara_deepatutor.data.repository.ChapterContentRepository
import com.example.akshara_deepatutor.ui.navigation.Screen
import com.example.akshara_deepatutor.ui.theme.*
import com.example.akshara_deepatutor.ui.viewmodels.ChapterViewModel
import kotlinx.coroutines.launch

data class ContentSlide(
    val title: String,
    val description: String,
    val detail: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterContentScreen(navController: NavHostController, chapterId: Int) {
    val context = LocalContext.current
    val viewModel: ChapterViewModel = viewModel(
        factory = ChapterViewModel.provideFactory(context.applicationContext as Application)
    )
    val chapters by viewModel.chapters.collectAsState()
    val chapter = chapters.find { it.id == chapterId }
    val scope = rememberCoroutineScope()

    // Real content for chapters using the Repository
    val slides = remember(chapter) {
        ChapterContentRepository.getContentForChapter(chapter?.name ?: "")
    }

    val pagerState = rememberPagerState { slides.size }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(chapter?.name ?: "Learning", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            // Progress Bar at the top of content
            LinearProgressIndicator(
                progress = { (pagerState.currentPage + 1).toFloat() / slides.size },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = BluePrimary,
                trackColor = BlueTertiary.copy(alpha = 0.3f)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = true
            ) { page ->
                SlideContent(slides[page])
            }

            // Bottom Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(slides.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (pagerState.currentPage == index) BluePrimary else Color.LightGray)
                        )
                    }
                }

                // Next/Start Quiz Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < (slides.size - 1)) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            navController.navigate(Screen.Quiz.createRoute(chapterId))
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier.height(50.dp)
                ) {
                    if (pagerState.currentPage < (slides.size - 1)) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Quiz")
                    }
                }
            }
        }
    }
}

@Composable
fun SlideContent(slide: ContentSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = BlueTertiary.copy(alpha = 0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = slide.title.first().toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = slide.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = slide.description,
            style = MaterialTheme.typography.titleMedium,
            color = BluePrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = slide.detail,
                modifier = Modifier.padding(24.dp),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 28.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
