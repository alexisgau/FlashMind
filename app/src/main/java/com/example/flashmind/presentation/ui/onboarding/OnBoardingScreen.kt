package com.example.flashmind.presentation.ui.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashmind.R
import kotlinx.coroutines.launch


data class OnboardingPage(
    @StringRes val title: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val imageContentDescription: Int,
    @StringRes val bodyText: Int,
    @StringRes val buttonText: Int,
)

@Composable
fun getOnboardingPages(): List<OnboardingPage> {
    return listOf(
        OnboardingPage(
            title = R.string.onboarding_welcome_title,
            imageResId = R.drawable.mastan_onboarding1,
            imageContentDescription = R.string.onboarding_welcome_image_cd,
            bodyText = R.string.onboarding_welcome_description,
            buttonText = R.string.next
        ),
        OnboardingPage(
            title = R.string.onboarding_generation_title,
            imageResId = R.drawable.onboarding_2,
            imageContentDescription = R.string.onboarding_generation_image_cd,
            bodyText = R.string.onboarding_generation_description,
            buttonText = R.string.next
        ),
        OnboardingPage(
            title = R.string.onboarding_organization_title,
            imageResId = R.drawable.onboarding_3,
            imageContentDescription = R.string.onboarding_organization_image_cd,
            bodyText = R.string.onboarding_organization_description,
            buttonText = R.string.next
        ),
        OnboardingPage(
            title = R.string.onboarding_ready_title,
            imageResId = R.drawable.onboarding_4,
            imageContentDescription = R.string.onboarding_ready_image_cd,
            bodyText = R.string.onboarding_ready_description,
            buttonText = R.string.onboarding_start_button
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingFlowScreen(
    onFinishOnboarding: () -> Unit,
) {
    val onboardingPages = getOnboardingPages()
    val totalPages = onboardingPages.size
    val pagerState = rememberPagerState(pageCount = { totalPages })
    val coroutineScope = rememberCoroutineScope()

    val currentPage = onboardingPages[pagerState.currentPage]

    Column(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            val page = onboardingPages[pageIndex]
            OnboardingScreenContent(
                page = page,
                isLastPage = pageIndex == totalPages - 1,
                onNextClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage < totalPages - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinishOnboarding()
                        }
                    }
                },
                onSkipClick = onFinishOnboarding
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1AA7C1))
                .padding(bottom = 32.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PageIndicator(
                numberOfPages = totalPages,
                selectedPage = pagerState.currentPage,
                onDotClick = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (pagerState.currentPage < totalPages - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onFinishOnboarding()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1AA7C1)
                )
            ) {
                Text(
                    text = stringResource(id = currentPage.buttonText),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreenContent(
    page: OnboardingPage,
    isLastPage: Boolean,
    onNextClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = Color(0xFF1AA7C1)
    val contentColor = Color.White

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                actions = {
                    TextButton(onClick = onSkipClick) {
                        Text(
                            text = stringResource(id = R.string.onboarding_skip),
                            color = contentColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = stringResource(id = page.title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Image(
                painter = painterResource(id = page.imageResId),
                contentDescription = stringResource(id = page.imageContentDescription),
                modifier = Modifier
                    .height(400.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

            Text(
                text = stringResource(id = page.bodyText),
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun PageIndicator(
    numberOfPages: Int,
    modifier: Modifier = Modifier,
    selectedPage: Int = 0,
    selectedColor: Color = Color.White,
    defaultColor: Color = Color.White.copy(alpha = 0.5f),
    dotSize: Dp = 8.dp,
    dotSpacing: Dp = 12.dp,
    onDotClick: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        modifier = modifier
    ) {
        repeat(numberOfPages) { index ->
            val color = if (index == selectedPage) selectedColor else defaultColor
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onDotClick(index) }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OnboardingFlowScreenPreview() {
    MaterialTheme {
        OnboardingFlowScreen(onFinishOnboarding = {})
    }
}