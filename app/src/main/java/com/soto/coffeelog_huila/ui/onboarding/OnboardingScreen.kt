package com.soto.coffeelog_huila.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.soto.coffeelog_huila.R
import com.soto.coffeelog_huila.ui.Screens
import com.soto.coffeelog_huila.ui.theme.BackgroundCrema
import com.soto.coffeelog_huila.ui.theme.CoffeeDark
import kotlinx.coroutines.launch

data class OnboardingPage(val image: Int, val title: String, val description: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {

    // Definicion de las 3 pantallas
    val pages = listOf(
        OnboardingPage(R.drawable.img_onboarding_1, "Bienvenido a CoffeeLog", "Tu herramienta offline para registrar, catar y conocer el alma de tu café."),
        OnboardingPage(R.drawable.img_onboarding_2, "Cata como un experto", "Descubre tu perfil de taza con gráficos interactivos."),
        OnboardingPage(R.drawable.img_onboarding_3, "Vende al precio justo", "Genera fichas técnicas en PDF sin internet.")
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(BackgroundCrema).systemBarsPadding()
    ) {
        // Carrusel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { position ->
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = pages[position].image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = pages[position].title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = CoffeeDark,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = pages[position].description,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) CoffeeDark else Color.LightGray
                    Box(
                        modifier = Modifier.padding(4.dp).clip(CircleShape).background(color).size(10.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage == pages.size - 1) {
                        navController.navigate(Screens.Login.route) {
                            popUpTo(Screens.Onboarding.route) { inclusive = true }
                        }
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeDark)
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.size - 1) "Comenzar ➔" else "Siguiente ➔",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}