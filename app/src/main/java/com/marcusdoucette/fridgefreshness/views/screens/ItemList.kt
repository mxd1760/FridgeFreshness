package com.marcusdoucette.fridgefreshness.views.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcusdoucette.fridgefreshness.FFAppView
import com.marcusdoucette.fridgefreshness.data.FridgeItemData
import com.marcusdoucette.fridgefreshness.MainActivity
import com.marcusdoucette.fridgefreshness.MainActivity.Companion.ACCEPTABLE_DAYS
import com.marcusdoucette.fridgefreshness.SetPortraitOrientationOnly
import java.time.LocalDate


@Composable
fun MainView(switchView:(FFAppView,Int?)->Unit,
             fridgeData:List<FridgeItemData>,
             modifier: Modifier = Modifier){
    SetPortraitOrientationOnly()
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimeHeader()
        ItemList(fridgeData,modifier = Modifier.weight(1f),switchView=switchView)
        ButtonController(addButtonCallback={switchView(FFAppView.NEW_ITEM,null)})
    }
}

@Composable
fun TimeHeader(){
    Text(
        text = LocalDate.now().toString(),
        fontSize=25.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun ItemList(fridgeData:List<FridgeItemData>,modifier:Modifier = Modifier,switchView:(FFAppView,Int?)->Unit){
    LazyColumn(
        modifier = modifier
    ){
        items(fridgeData.size) { it-> // todo sort on next check date
            FridgeItem(fridgeData[it],itemClickCallback={
                switchView(FFAppView.ITEM,it)
            })
        }
    }
}

@Composable
fun FridgeItem(itemData:FridgeItemData,itemClickCallback:()->Unit){
    Row(
        modifier=Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = itemClickCallback)
            .background(when {
                LocalDate.now()<itemData.next_reccomended_check -> Color.Green
                LocalDate.now().plusDays(-ACCEPTABLE_DAYS.toLong())<itemData.next_reccomended_check-> Color.Yellow
                else -> Color.Red
            }),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Image(painter = BitmapPainter(itemData.image.asImageBitmap()), contentDescription = "Image of ${itemData.name}")
        Text(itemData.name,fontSize=25.sp,modifier=Modifier.fillMaxHeight().wrapContentHeight(align = Alignment.CenterVertically))
        Text(itemData.next_reccomended_check.toString(),fontSize=15.sp)
    }
}

fun open_item(itemData:FridgeItemData){
    Log.d(MainActivity.APP_NAME,"Open Item ${itemData}")
}


@Composable
fun ButtonController(addButtonCallback:()->Unit,modifier:Modifier=Modifier){
    OutlinedButton(onClick = addButtonCallback){
        Text("+", fontSize = 35.sp)
    }
}