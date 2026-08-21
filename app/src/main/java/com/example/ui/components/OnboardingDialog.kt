package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.TvFocusGlow
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSurfaceVariant

@Composable
fun OnboardingDialog(
    onComplete: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(step) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(580.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF111827))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(TvPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (step) {
                    0 -> Icons.Default.Explore
                    1 -> Icons.Default.Category
                    else -> Icons.Default.TvOff
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TvPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            val title = when (step) {
                0 -> stringResource(R.string.onboarding_welcome_title)
                1 -> stringResource(R.string.onboarding_cats_title)
                else -> stringResource(R.string.onboarding_oled_title)
            }

            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            val desc = when (step) {
                0 -> stringResource(R.string.onboarding_welcome_desc)
                1 -> stringResource(R.string.onboarding_cats_desc)
                else -> stringResource(R.string.onboarding_oled_desc)
            }

            Text(
                text = desc,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Step dots indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == step) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (i == step) TvPrimary else Color.White.copy(alpha = 0.2f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (step > 0) {
                    var isBackFocused by remember { mutableStateOf(false) }
                    Button(
                        onClick = { step-- },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBackFocused) TvFocusGlow else TvSurfaceVariant
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .onFocusChanged { isBackFocused = it.isFocused }
                    ) {
                        Text(text = stringResource(R.string.back), color = if (isBackFocused) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                var isNextFocused by remember { mutableStateOf(false) }
                Button(
                    onClick = {
                        if (step < 2) {
                            step++
                        } else {
                            onComplete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TvPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { isNextFocused = it.isFocused }
                ) {
                    Text(
                        text = if (step < 2) stringResource(R.string.next) else stringResource(R.string.get_started),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
