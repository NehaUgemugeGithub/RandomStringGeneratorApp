package com.challenge.randomstringgenerator.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.challenge.randomstringgenerator.R
import com.challenge.randomstringgenerator.model.RandomStringRepository
import com.challenge.randomstringgeneratorapp.viewmodel.RandomStringViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = RandomStringRepository(this)
        val viewModel = RandomStringViewModel(repository)

        setContent {
            RandomStringScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun RandomStringScreen(viewModel: RandomStringViewModel) {
    val randomStrings by viewModel.randomStrings.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    var maxLength by remember { mutableStateOf("10") }
   Column {
       SimpleTopAppBar()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = maxLength,
            onValueChange = { maxLength = it },
            label = { Text(stringResource(R.string.max_length)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Row(modifier = Modifier
            .padding(vertical = 16.dp)
            .align(Alignment.CenterHorizontally)) {
            Button(onClick = { viewModel.fetchRandomString(maxLength.toIntOrNull() ?: 0) }) {
                Text(stringResource(R.string.generate_random_string))
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }

        errorMessage?.let { Text("Error: $it", color = Color.Red) }

        LazyColumn {
            itemsIndexed(randomStrings) { index, randomString ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.LightGray)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("String: ${randomString.value}")
                        Text("Length: ${randomString.length}")
                        Text("Created: ${randomString.created}")
                    }
                    IconButton(onClick = { viewModel.deleteString(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.deleteAllStrings() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Delete All Strings")
        }
    }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopAppBar() {
    TopAppBar(
        title = { Text(
            stringResource(R.string.random_string_generator_app),
            modifier = Modifier.fillMaxWidth(), // Make title take up full width
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 18.sp, // Set the font size
                fontWeight = FontWeight.Bold, // Set the font weight to bold
                fontFamily = FontFamily.SansSerif, // Optional, set a custom font family
                color = Color.Black // Set text color
            )
        ) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = Color(0XFFBB86FC)  // Set the TopAppBar background color to red
        )
    )
}