package com.example.akshara_deepatutor.ui.screens

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.akshara_deepatutor.R
import com.example.akshara_deepatutor.ui.navigation.Screen
import com.example.akshara_deepatutor.ui.theme.*
import com.example.akshara_deepatutor.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    windowWidthSizeClass: WindowWidthSizeClass,
) {
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(context.applicationContext as Application),
    )
    val lastSession by viewModel.lastLearningSession.collectAsState()
    
    val isCompact = windowWidthSizeClass == WindowWidthSizeClass.Compact
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .shadow(4.dp, CircleShape),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.my_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Akshara-Deepa Tutor",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = BluePrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Reminders.route) }) {
                        BadgedBox(badge = { Badge { Text("2") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = BluePrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { 
            if (isCompact) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFF0F7FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Greeting Section
                Text(
                    text = "Hello, Learner! 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )
                Text(
                    text = "Ready to learn something new today?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Continue Learning Section
                lastSession?.let { session ->
                    ContinueLearningCard(
                        session = session,
                        onClick = {
                            navController.navigate(Screen.Chapter.createRoute(session.subjectId, session.subjectName))
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 3. New Messages / Daily Motivation Section
                MessagesCard()

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Motivational Quote Card
                QuoteCard()

                Spacer(modifier = Modifier.height(32.dp))

                // 5. Single Big "All Subjects" Card
                AllSubjectsCard { navController.navigate(Screen.Subject.route) }

                Spacer(modifier = Modifier.height(40.dp))
                
                // 5. Decorative Bottom Illustration Placeholder
                BottomIllustration()
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ContinueLearningCard(session: com.example.akshara_deepatutor.data.LastLearningSession, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF9A8B), Color(0xFFFF6A88), Color(0xFFFF99AC))
                    )
                )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CONTINUE LEARNING",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = session.chapterName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = session.subjectName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { session.progress },
                        modifier = Modifier.size(44.dp),
                        color = Color.White,
                        strokeWidth = 4.dp,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "${(session.progress * 100).toInt()}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MessagesCard() {
    val messages = listOf(
        "Practice Science today for better progress.",
        "You completed 2 chapters today. Great job!",
        "Don't forget your Maths quiz.",
        "Consistency is the key to success. Keep learning!",
        "Struggling with Physics? Try a quick quiz!",
        "Your English vocabulary is improving!"
    )
    
    var currentMessageIndex by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        while(true) {
            kotlinx.coroutines.delay(5000)
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color(0xFFF3E5F5),
                            Color(0xFFFCE4EC)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.6f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF6A11CB),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Study Insights",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF6A11CB),
                        fontWeight = FontWeight.Bold
                    )
                    
                    AnimatedContent(
                        targetState = messages[currentMessageIndex],
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(600)) + slideInVertically { it }) togetherWith
                            (fadeOut(animationSpec = tween(600)) + slideOutVertically { -it })
                        },
                        label = "MessageAnimation"
                    ) { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = BlueSecondary.copy(alpha = 0.3f),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "The beautiful thing about learning is that no one can take it away from you.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "— B.B. King",
                    style = MaterialTheme.typography.labelMedium,
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Illustration placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Simplified illustration using icons
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.School, 
                        contentDescription = null, 
                        tint = BluePrimary, 
                        modifier = Modifier.size(40.dp)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook, 
                        contentDescription = null, 
                        tint = Color(0xFFFFA000), 
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AllSubjectsCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Subject Icon
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(20.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "All Subjects",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Explore and learn all your subjects",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                }
                
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = Color(0xFF2575FC),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomIllustration() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Since I don't have the exact illustration, I'll use a combination of icons and shapes
        // to represent the books, pencil, and backpack in the image.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            // Background leaf/element effect
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD).copy(alpha = 0.5f))
            )
            
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                // Representation of books
                Icon(
                    Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = BlueSecondary,
                    modifier = Modifier.size(120.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Representation of backpack
                Icon(
                    Icons.Default.Backpack,
                    contentDescription = null,
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(80.dp)
                )
            }
            
            // Floating pencil icon
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(40.dp)
                    .offset(x = (-60).dp, y = 40.dp)
            )
        }
        
        Text(
            text = "Knowledge is Power",
            style = MaterialTheme.typography.titleMedium,
            color = BluePrimary.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 12.dp
    ) {
        val navItems = listOf(
            Triple(Screen.Home.route, "Home", Icons.Default.Home),
            Triple(Screen.Profile.route, "Profile", Icons.Default.PersonOutline)
        )

        navItems.forEach { (route, label, icon) ->
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BluePrimary,
                    selectedTextColor = BluePrimary,
                    indicatorColor = BlueTertiary
                )
            )
        }
    }
}
