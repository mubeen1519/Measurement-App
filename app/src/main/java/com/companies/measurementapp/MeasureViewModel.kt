package com.companies.measurementapp

import BoxData
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class MeasureViewModel : ViewModel() {
    //box /////////////////////////////////////////////////////////////////////////////////////////////
    private val _isAddingBox = MutableLiveData<Boolean>()
    val isAddingBox: LiveData<Boolean> = _isAddingBox
    fun setIsAddingBox(isAdd: Boolean) {
        _isAddingBox.value = isAdd
    }

    private val _showSheet = MutableLiveData<Boolean>()
    val showSheet: LiveData<Boolean> = _showSheet
    fun setShowSheet(isShow: Boolean) {
        _showSheet.value = isShow
    }

    fun updateDraggableBox(id: Int, newText: String, newColor: Color) {
        val index = draggableBoxes.indexOfFirst { it.id == id }
        if (index != -1) {
            val updatedBox = draggableBoxes[index].copy(
                text = newText,
                color = newColor
            )
            draggableBoxes[index] = updatedBox
        }
    }

    val selectedBox = mutableStateOf<BoxData?>(null)
    val draggableBoxes = mutableStateListOf<BoxData>()

    private var nextId = 1




    fun setSelectedBox(boxData: BoxData?) {
        selectedBox.value = boxData
        setShowSheet(true)
    }

    fun removeDraggableBox(id: Int) {
        draggableBoxes.removeIf { it.id == id }
    }



    fun addNewBox() {
        val random = Random(System.currentTimeMillis())
        val randomX = random.nextFloat() * 500f
        val randomY = random.nextFloat() * 500f
        val boxPosition = mutableStateOf(Offset(randomX, randomY))
        val defaultText = ""
        val defaultColor = Color.Blue
        val newBox = BoxData(nextId++,boxPosition, defaultText, defaultColor)
        draggableBoxes.add(newBox)

        selectedBox.value = newBox
    }

    //arrow/////////////////////////////////////////////////////////////////////////////////////////


    private val _showArrowSheet = MutableStateFlow(false)
    val showArrowSheet: StateFlow<Boolean> = _showArrowSheet
    fun setShowArrowSheet(isShow: Boolean) {
        _showArrowSheet.value = isShow
    }


    private val _isDrawing = MutableLiveData<Boolean>()
    val isDrawing: LiveData<Boolean> = _isDrawing

    fun isDraw(isDraw: Boolean) {
        _isDrawing.value = isDraw
    }


   val lines = mutableStateListOf<Line>()

    var currentLine = mutableStateOf<Line?>(null)

    fun updateCurrentLine(updatedLine: Line) {
        currentLine.value = updatedLine
    }

    fun onFieldChange(newText: String){
        currentLine.value = currentLine.value?.copy(text = newText)
    }

    private var nextLineId = 1

    fun addIntoList(line: Line) {
        val lineWithId = line.copy(id = nextLineId++)
        lines.add(lineWithId)
    }

    fun editLineById(lineId : Int){
        val lineToEdit = lines.find { it.id == lineId }
        if(lineToEdit != null){
            currentLine.value = lineToEdit
            setShowArrowSheet(true)
        }
    }

    fun saveEditedLine(updatedText: String, updatedColor: Color) {
        val lineToUpdate = currentLine.value
        if (lineToUpdate != null) {
            val index = lines.indexOfFirst { it.id == lineToUpdate.id }
            if (index != -1) {
                val updatedLine = lineToUpdate.copy(text = updatedText, color = updatedColor)
                lines[index] = updatedLine
            }
        }
    }

    fun updateLineById(id: Int, updatedLine: Line) {
        val index = lines.indexOfFirst { it.id == id }
        if (index != -1) {
            lines[index] = updatedLine
        }
    }




    var isEditMode = mutableStateOf(false)


}