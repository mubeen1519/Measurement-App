package com.companies.measurementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.companies.measurementapp.ui.theme.MeasurementAppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MeasureViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeasurementAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MeasurementImage(viewModel)
                }
            }
        }
    }
}

@Composable
fun MeasurementImage(viewModel: MeasureViewModel) {
    val showSheet by viewModel.showSheet.observeAsState(initial = false)
    val arrowSheet by viewModel.showArrowSheet.collectAsState(false)
    var selectedIconIndex by remember { mutableStateOf(-1) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.room),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .background(Color.Black),
            contentScale = ContentScale.Crop
        )
        if (showSheet) {
            BottomSheet(onDismiss = {
                viewModel.setShowSheet(false)
            }, viewModel)
        }

       

        if (arrowSheet) {
            BottomSheet2(
                onDismiss = { viewModel.setShowArrowSheet(false) },
                viewModel = viewModel,
            )
        }
        
        DrawingClass(viewModel = viewModel)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
        ) {
            BottomBarExample(
                onClick = { index ->
                    selectedIconIndex = index
                    when (index) {
                        3 -> {
                            viewModel.setShowSheet(true)
                            viewModel.setIsAddingBox(true)
                            viewModel.addNewBox()
                            viewModel.isEditMode.value = true
                        }

                        0 -> {
                            viewModel.isDraw(true)
                        }
                    }
                },
                selectedIconIndex = selectedIconIndex
            )
        }
    }
}

