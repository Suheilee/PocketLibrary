package au.edu.curtin.madassignment2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun ThemeShowcase() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pocket Library Theme Showcase",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        // Typography showcase
        TypographyShowcase()

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        // Color scheme showcase
        ColorSchemeShowcase()

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        // Component showcase
        ComponentShowcase()
    }
}

@Composable
private fun TypographyShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Typography",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Display Large",
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = "Headline Small",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Title Large",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Title Medium",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Body Large - This is a longer text to showcase body typography",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = "Body Medium - Regular text content for descriptions",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Body Small - Smaller text for captions",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Label Large",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun ColorSchemeShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Color Scheme",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ColorBox("Primary", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            ColorBox("Secondary", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
            ColorBox("Tertiary", MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ColorBox("Error", MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.onError)
            ColorBox("Surface", MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.onSurface)
            ColorBox("Background", MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
private fun RowScope.ColorBox(name: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(80.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ComponentShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Components",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        // Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {}) {
                Text("Primary Button")
            }
            OutlinedButton(onClick = {}) {
                Text("Outlined Button")
            }
            TextButton(onClick = {}) {
                Text("Text Button")
            }
        }

        // Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Card Title",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This is a sample card showcasing the theme colors and typography.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                onClick = {},
                label = { Text("Selected") },
                selected = true
            )
            FilterChip(
                onClick = {},
                label = { Text("Unselected") },
                selected = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeShowcasePreview() {
    PocketLibraryTheme(darkTheme = false) {
        ThemeShowcase()
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeShowcaseDarkPreview() {
    PocketLibraryTheme(darkTheme = true) {
        ThemeShowcase()
    }
}