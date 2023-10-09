import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.companies.measurementapp.MeasureViewModel
import kotlin.math.roundToInt


@Composable
fun DraggableBox(
    position: Offset,
    boxData: BoxData,
    viewModel: MeasureViewModel
) {

    val isDragging = remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(position.x) }
    var offsetY by remember {
        mutableStateOf(position.y)
    }


    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    offsetX.roundToInt(),
                    offsetY.roundToInt()
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging.value = true
                    },
                    onDragEnd = {
                        isDragging.value = false
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y

                }
            }
            .background(boxData.color.copy(alpha = 0.6f))
            .width(120.dp)
            .alpha(0.7f)
            .padding(10.dp)
            .clickable {
                viewModel.setSelectedBox(boxData)
            }


    ) {
        Text(
            text = boxData.text,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(end = 8.dp, top = 16.dp)
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier
                .align(
                    Alignment.TopEnd
                )
                .clickable {
                    viewModel.removeDraggableBox(boxData.id)
                }
                .padding(start = 10.dp, bottom = 20.dp),
            tint = Color.White
        )
    }
}


data class BoxData(
    val id: Int,
    val boxPosition: MutableState<Offset>,
    val text: String,
    val color: Color
)








