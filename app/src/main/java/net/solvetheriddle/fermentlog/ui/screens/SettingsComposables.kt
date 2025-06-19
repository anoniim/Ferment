package net.solvetheriddle.fermentlog.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun PreferenceCategory(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun Preference(
    title: String,
    summary: String? = null,
    @DrawableRes icon: Int? = null,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null, // decorative
                    modifier = Modifier.padding(end = 16.dp)
                        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                if (summary != null) {
                    Text(text = summary, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
