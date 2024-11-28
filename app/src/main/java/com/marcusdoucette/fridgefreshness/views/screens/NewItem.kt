package com.marcusdoucette.fridgefreshness.views.screens


import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.marcusdoucette.fridgefreshness.FFAppView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.marcusdoucette.fridgefreshness.FridgeItemData
import com.marcusdoucette.fridgefreshness.R
import com.marcusdoucette.fridgefreshness.views.models.NewItemViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.marcusdoucette.fridgefreshness.MainActivity.Companion.DEFAULT_IMAGE


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItemView(currentView:FFAppView, switchView: (FFAppView,Int?) -> Unit, submitItem:(FridgeItemData)->Unit, vm: NewItemViewModel = viewModel(), modifier: Modifier = Modifier) {
//    var text by remember { mutableStateOf("") }
//    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    // image icon
    val uis by vm.uiState.collectAsStateWithLifecycle()
    val default_bitmap = BitmapFactory.decodeResource(LocalContext.current.getResources(), DEFAULT_IMAGE)
    when (currentView){
        FFAppView.NEW_ITEM-> Defaults(onSubmit = {
            vm.getValidatedState(default_bitmap)?.let { submitItem(it) } //?: do something to show submission is invalid
            switchView(FFAppView.ITEM_LIST,null)
        }, name=uis.name ?: "",onNameChange={vm.changeName(it)},
            selectedDate=uis.next_check ?: LocalDate.now(),onDateChange={vm.setDate(it)},
            switchView=switchView,modifier=modifier)
        FFAppView.CAMERA-> CameraView(switchView,modifier)
        else -> {}
    }




//    OutlinedButton(onClick = {
//        switchView(FFAppView.MAIN)
//    }, modifier = modifier
//    ) {
//        Text("Back",fontSize=25.sp)
//    }
//    CameraPreviewScreen()
}

@Composable
fun Defaults(onSubmit:()->Unit,name:String,onNameChange:(String)->Unit,selectedDate:LocalDate,onDateChange:(LocalDate)->Unit,switchView:(FFAppView,Int?)->Unit,modifier:Modifier=Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            modifier = Modifier
                .weight(1F)
                .clip(CircleShape)
                .clickable { switchView(FFAppView.CAMERA,null) },
            contentDescription = "Food Image"
        )
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            // name
            TextField(
                value = name,
                onValueChange = onNameChange,
                singleLine = true,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
            )
            // date to next check
            NextDateField(
                selectedDate = selectedDate,
                onDateSelected = onDateChange,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier=Modifier.weight(1f))
            Row{
                Button(
                    modifier=Modifier.weight(1.0f),
                    onClick={
                        switchView(FFAppView.ITEM_LIST,null)
                }){
                    Text("Back")
                }
                Button(
                    modifier=Modifier.weight(1.0f),
                    onClick = onSubmit
                ){
                    Text("Submit")
                }

            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextDateField(selectedDate:LocalDate,onDateSelected:(LocalDate)->Unit, modifier: Modifier = Modifier) {
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        onValueChange = { },
        label = { Text("Date to Check") },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        },
        modifier = modifier
    )

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = onDateSelected,
            onDismiss = { showDatePicker = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    Instant.ofEpochMilli(it).atZone(
                    ZoneId.systemDefault()).toLocalDate()
                }?.let { onDateSelected(it) }
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}



