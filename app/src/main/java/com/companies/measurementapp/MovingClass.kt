package com.companies.measurementapp

import DraggableBox
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier

@Composable
fun DrawingClass(viewModel: MeasureViewModel) {
    val isAdding by viewModel.isAddingBox.observeAsState(initial = false)
    val isDrawing by viewModel.isDrawing.observeAsState(initial = false)

    Box(modifier = Modifier.fillMaxSize()) {
        if (isDrawing) {
            ArrowDrawing(viewModel = viewModel)
        }

        if (isAdding) {
            viewModel.draggableBoxes.forEach {
                DraggableBox(
                    position = it.boxPosition.value,
                    boxData = it,
                    viewModel = viewModel,
                )
            }
        }

    }
}