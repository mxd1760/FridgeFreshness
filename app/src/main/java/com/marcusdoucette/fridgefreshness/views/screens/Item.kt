package com.marcusdoucette.fridgefreshness.views.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.sp
import com.marcusdoucette.fridgefreshness.FFAppView
import com.marcusdoucette.fridgefreshness.FridgeItemData

@Composable
fun SingleItemView(switchView:(FFAppView, Int?)->Unit, itemData:FridgeItemData,removeItem:(FridgeItemData)->Unit,modifier: Modifier =Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier=modifier.fillMaxSize()
    ){
        Image(
            painter = BitmapPainter(itemData.image.asImageBitmap()),
            contentDescription = "Image of ${itemData.name}")
        Text(itemData.name,
            fontSize=25.sp)
        Text(
            itemData.next_reccomended_check.toString(),
            fontSize=15.sp)
        Spacer(modifier=Modifier.weight(1.0f))
        Row(modifier=modifier.fillMaxWidth()){
            OutlinedButton(
                modifier=Modifier.weight(1.0f),
                onClick = {
                    switchView(FFAppView.ITEM_LIST,null)
            }){
                Text("back")
            }
            OutlinedButton(
                modifier=Modifier.weight(0.5f),
                onClick = {
                    //TODO probably should have popup dialog here
                    removeItem(itemData)
                    switchView(FFAppView.ITEM_LIST,null)
            }){
                Text("delete")
            }
        }
    }
}