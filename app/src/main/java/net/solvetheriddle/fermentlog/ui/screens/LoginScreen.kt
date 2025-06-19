package net.solvetheriddle.fermentlog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.solvetheriddle.fermentlog.auth.AuthenticationManager
import net.solvetheriddle.fermentlog.ui.theme.FermentTheme

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    authenticationManager: AuthenticationManager? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }

    if (authenticationManager != null) {
        val currentUser by authenticationManager.currentUser.collectAsState()
        val error by authenticationManager.error.collectAsState()

        LaunchedEffect(currentUser) {
            if (currentUser != null) {
                onLoggedIn()
            }
        }

        LaunchedEffect(error) {
            error?.let {
                snackbarHostState.showSnackbar(it)
                authenticationManager.clearError()
            }
        }
    }

    val handleSignIn: () -> Unit = {
        authenticationManager?.signIn()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(it),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.kombucha_bg),
                contentDescription = "Kombucha background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Start Button
                Button(
                    onClick = handleSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = "Start making kombucha",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    FermentTheme {
        Surface {
            LoginScreen(
                onLoggedIn = {},
                authenticationManager = null
            )
        }
    }
}
