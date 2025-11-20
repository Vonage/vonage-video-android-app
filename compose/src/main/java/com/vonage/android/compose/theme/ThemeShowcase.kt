package com.vonage.android.compose.theme.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme

/**
 * Example showcasing the Vonage custom theme usage
 */
@Composable
fun ThemeShowcase() {
    VonageVideoTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = VonageVideoTheme.colors.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TypographyShowcase()
                ColorShowcase()
                ShapeShowcase()
                ButtonShowcase()
            }
        }
    }
}

@Composable
private fun TypographyShowcase() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = VonageVideoTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = VonageVideoTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Typography Showcase",
                style = VonageVideoTheme.typography.heading2,
                color = VonageVideoTheme.colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Headline",
                style = VonageVideoTheme.typography.headline,
                color = VonageVideoTheme.colors.textSecondary
            )
            
            Text(
                text = "Heading 1",
                style = VonageVideoTheme.typography.heading1,
                color = VonageVideoTheme.colors.textSecondary
            )
            
            Text(
                text = "Body Extended",
                style = VonageVideoTheme.typography.bodyExtended,
                color = VonageVideoTheme.colors.textSecondary
            )
            
            Text(
                text = "Body Extended Semibold",
                style = VonageVideoTheme.typography.bodyExtendedSemibold,
                color = VonageVideoTheme.colors.textSecondary
            )
            
            Text(
                text = "Body Base",
                style = VonageVideoTheme.typography.bodyBase,
                color = VonageVideoTheme.colors.textSecondary
            )
            
            Text(
                text = "Caption",
                style = VonageVideoTheme.typography.caption,
                color = VonageVideoTheme.colors.textTertiary
            )
        }
    }
}

@Composable
private fun ColorShowcase() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = VonageVideoTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = VonageVideoTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Color Showcase",
                style = VonageVideoTheme.typography.heading2,
                color = VonageVideoTheme.colors.textPrimary
            )
            
            ColorSwatch("Primary", VonageVideoTheme.colors.primary, VonageVideoTheme.colors.onPrimary)
            ColorSwatch("Secondary", VonageVideoTheme.colors.secondary, VonageVideoTheme.colors.onSecondary)
            ColorSwatch("Tertiary", VonageVideoTheme.colors.tertiary, VonageVideoTheme.colors.onTertiary)
            ColorSwatch("Error", VonageVideoTheme.colors.error, VonageVideoTheme.colors.onError)
            ColorSwatch("Warning", VonageVideoTheme.colors.warning, VonageVideoTheme.colors.onWarning)
            ColorSwatch("Success", VonageVideoTheme.colors.success, VonageVideoTheme.colors.onSuccess)
        }
    }
}

@Composable
private fun ColorSwatch(
    label: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(backgroundColor, VonageVideoTheme.shapes.small)
            .border(1.dp, VonageVideoTheme.colors.border, VonageVideoTheme.shapes.small)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = VonageVideoTheme.typography.bodyBaseSemibold,
            color = textColor,
            modifier = Modifier.padding(vertical = 12.dp)
        )
    }
}

@Composable
private fun ShapeShowcase() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = VonageVideoTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = VonageVideoTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Shape Showcase",
                style = VonageVideoTheme.typography.heading2,
                color = VonageVideoTheme.colors.textPrimary
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShapeExample("None", VonageVideoTheme.shapes.none)
                ShapeExample("XS", VonageVideoTheme.shapes.extraSmall)
                ShapeExample("S", VonageVideoTheme.shapes.small)
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShapeExample("M", VonageVideoTheme.shapes.medium)
                ShapeExample("L", VonageVideoTheme.shapes.large)
                ShapeExample("XL", VonageVideoTheme.shapes.extraLarge)
            }
        }
    }
}

@Composable
private fun ShapeExample(label: String, shape: androidx.compose.ui.graphics.Shape) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .background(VonageVideoTheme.colors.primary, shape)
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = VonageVideoTheme.typography.caption,
            color = VonageVideoTheme.colors.onPrimary
        )
    }
}

@Composable
private fun ButtonShowcase() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = VonageVideoTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = VonageVideoTheme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Button Showcase",
                style = VonageVideoTheme.typography.heading2,
                color = VonageVideoTheme.colors.textPrimary
            )
            
            // Primary Button
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VonageVideoTheme.colors.primary,
                    contentColor = VonageVideoTheme.colors.onPrimary
                ),
                shape = VonageVideoTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Primary Button", style = VonageVideoTheme.typography.bodyBaseSemibold)
            }
            
            // Error Button
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VonageVideoTheme.colors.error,
                    contentColor = VonageVideoTheme.colors.onError
                ),
                shape = VonageVideoTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Error Button", style = VonageVideoTheme.typography.bodyBaseSemibold)
            }
            
            // Success Button
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VonageVideoTheme.colors.success,
                    contentColor = VonageVideoTheme.colors.onSuccess
                ),
                shape = VonageVideoTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Success Button", style = VonageVideoTheme.typography.bodyBaseSemibold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemeShowcasePreview() {
    ThemeShowcase()
}
