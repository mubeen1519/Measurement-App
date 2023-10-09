package com.companies.measurementapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarExample(onClick: (index: Int) -> Unit, selectedIconIndex: Int) {

    val icons = listOf(
        R.drawable.pfeil,
        R.drawable.kurve,
        R.drawable.winkel,
        R.drawable.text,
        R.drawable.bilder,
        R.drawable.hinzufugen,
        R.drawable.werkzeuge,
        R.drawable.sicht,
    )

    val names = listOf(
        "Pfeil", "Kurve", "Winkel", "Text", "bilder", "Hinzufugen", "Werkzeuge", "Sicht"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalArrangement = Arrangement.Bottom
    ) {
        // First Row with 5 icons and names
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (index in 0 until 5) {
                IconWithName(
                    icon = icons[index], name = names[index], selected = index == selectedIconIndex
                ) {
                    onClick(index)
                }
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 35.dp, end = 35.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until 3) {
                IconWithName(
                    icon = icons[index + 5], name = names[index + 5], selected = false
                ) {

                }
            }
        }
    }


}

@Composable
fun IconWithName(icon: Int, name: String, selected: Boolean, onClick: () -> Unit) {
    val selectedColor = if (selected) Color.Blue else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(34.dp)
                .clickable { onClick() },
            tint = selectedColor
        )
        Text(text = name, color = selectedColor)

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismiss: () -> Unit,
    viewModel: MeasureViewModel
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val selectedBox = viewModel.selectedBox.value
    val textValue = remember { mutableStateOf(selectedBox?.text ?: "") }
    val colorValue = remember { mutableStateOf(selectedBox?.color ?: Color.Blue) }
    ModalBottomSheet(onDismissRequest = {
        onDismiss()
        selectedBox?.let {
            viewModel.updateDraggableBox(it.id, textValue.value, colorValue.value)
        }
    },
        sheetState = modalBottomSheetState,
        windowInsets = WindowInsets(bottom = 140.dp),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = textValue.value,
                    onValueChange = {
                        textValue.value = it
                        selectedBox?.let {data ->
                            viewModel.updateDraggableBox(data.id, textValue.value, colorValue.value)
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,

                        ),
                    keyboardActions = KeyboardActions(onDone = {
                        this.defaultKeyboardAction(ImeAction.Done)
                        viewModel.setShowSheet(false)
                        selectedBox?.let {
                            viewModel.updateDraggableBox(it.id, textValue.value, colorValue.value)
                        }

                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ColorSelection.values().forEach{ colorSelection ->
                        Box(modifier = Modifier
                            .size(25.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(colorSelection.colors)
                            .padding(10.dp)
                            .clickable {
                                selectedBox.let { new ->
                                    colorValue.value = colorSelection.colors
                                    viewModel.updateDraggableBox(new!!.id,textValue.value,colorValue.value)
                                }
                            })
                    }
                }
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet2(
    onDismiss: () -> Unit, viewModel: MeasureViewModel
) {
    val modalBottomSheetState = rememberModalBottomSheetState()


    var currentLine by viewModel.currentLine

    ModalBottomSheet(onDismissRequest = {
        onDismiss()
        currentLine?.let { editedLine ->
            viewModel.saveEditedLine(editedLine.text, editedLine.color)
            if (!viewModel.lines.contains(editedLine)) {
                viewModel.addIntoList(editedLine)
            }
            viewModel.updateCurrentLine(editedLine)
        }
        currentLine = null
    },
        sheetState = modalBottomSheetState,
        windowInsets = WindowInsets(bottom = 140.dp),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = currentLine?.text ?: "",
                    onValueChange = viewModel::onFieldChange,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done, keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        this.defaultKeyboardAction(ImeAction.Done)
                        viewModel.setShowArrowSheet(false)
                        currentLine?.let { editedLine ->
                            viewModel.saveEditedLine(editedLine.text, editedLine.color)
                            if (!viewModel.lines.contains(editedLine)) {
                                viewModel.addIntoList(editedLine)
                            }
                        }

                        currentLine = null
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ColorSelection.values().forEach {  colorSelection ->
                        Box(modifier = Modifier
                            .size(25.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(colorSelection.colors)
                            .padding(10.dp)
                            .clickable {
                                currentLine =
                                    currentLine?.copy(color = colorSelection.colors)
                            }
                        )
                    }
                }
            }
        }
    )
}


