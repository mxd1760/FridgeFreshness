package com.marcusdoucette.fridgefreshness

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.marcusdoucette.fridgefreshness.ui.theme.FridgeFreshnessTheme
import com.marcusdoucette.fridgefreshness.views.screens.MainView
import com.marcusdoucette.fridgefreshness.views.screens.NewItemView
import com.marcusdoucette.fridgefreshness.views.screens.SingleItemView
import java.time.LocalDate

enum class Freshness {
    GOOD_TO_GO,
    GETTING_OLD,
    DEFINATELY_BAD
}

enum class FFAppView {
    ITEM_LIST,
    NEW_ITEM,
    CAMERA,
    ITEM,
}

data class FridgeItemData (
    val image: Bitmap,
    val name: String,
    //var last_checked:LocalDate,//TODO belongs in subclass for check after days
    //var freshness:Freshness,
    var next_reccomended_check: LocalDate, // TODO belongs in subclass for given date
):Comparable<FridgeItemData>{
    override fun compareTo(other:FridgeItemData):Int{
        return next_reccomended_check.compareTo(other.next_reccomended_check)
    }
}


class MainActivity : ComponentActivity() {

    companion object {
        val APP_NAME = "Fridge Freshness"
        val ACCEPTABLE_DAYS = 10
        val DEFAULT_IMAGE:Int = R.drawable.diet
    }

    val MOCK_DATA: ArrayList<FridgeItemData> = arrayListOf()


    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Implement camera related code
            } else {
                // camera permission denied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val image = BitmapFactory.decodeResource(resources,DEFAULT_IMAGE )
        MOCK_DATA.addAll( // TODO Remove Me and replace with real data
            arrayListOf(
                FridgeItemData(
                    image,
                    "Banana",
                    //LocalDate.of(2024, 1, 1),
                    //Freshness.GOOD_TO_GO,
                    LocalDate.of(2024, 12, 5),
                ),
                FridgeItemData(
                    image,
                    "Tomato",
                    //LocalDate.of(2024, 2, 2),
                    //Freshness.GETTING_OLD,
                    LocalDate.of(2024, 12, 10)
                ),
                FridgeItemData(
                    image,
                    "Strawberry",
                    //LocalDate.of(2024, 3, 3),
                    //Freshness.DEFINATELY_BAD,
                    LocalDate.of(2024, 12, 15)
                ),
                FridgeItemData(
                    image,
                    "Chicken",
                    //LocalDate.of(2024, 4, 4),
                    //Freshness.GOOD_TO_GO,
                    LocalDate.of(2024, 12, 20)
                ),
                FridgeItemData(
                    image,
                    "Yogurt",
                    //LocalDate.of(2024, 5, 5),
                    //Freshness.GETTING_OLD,
                    LocalDate.of(2024, 12, 25)
                ),
                FridgeItemData(
                    image,
                    "Leftovers",
                    //LocalDate.of(2024, 6, 6),
                    //Freshness.DEFINATELY_BAD,
                    LocalDate.of(2024, 12, 30)
                ),
                FridgeItemData(
                    image,
                    "Mayonase",
                    //LocalDate.of(2024, 7, 7),
                    //Freshness.GOOD_TO_GO,
                    LocalDate.of(2024, 12, 6)
                ),
                FridgeItemData(
                    image,
                    "Gineger ale",
                    //LocalDate.of(2024, 8, 8),
                    //Freshness.GETTING_OLD,
                    LocalDate.of(2024, 12, 11)
                ),
                FridgeItemData(
                    image,
                    "Pickles",
                    //LocalDate.of(2024, 9, 9),
                    //Freshness.DEFINATELY_BAD,
                    LocalDate.of(2024, 12, 16)
                ),
                FridgeItemData(
                    image,
                    "Cheese",
                    //LocalDate.of(2024, 10, 10),
                    //Freshness.GOOD_TO_GO,
                    LocalDate.of(2024, 12, 21)
                ),
                FridgeItemData(
                    image,
                    "Salami",
                    //LocalDate.of(2024, 11, 11),
                    //Freshness.GOOD_TO_GO,
                    LocalDate.of(2024, 12, 26)
                ),
                FridgeItemData(
                    image,
                    "Ham",
                    //LocalDate.of(2024, 12, 12),
                    //Freshness.GOOD_TO_GO,
                    LocalDate.of(2024, 12, 7)
                )
            )
        )
        //Log.d(APP_NAME,"Data before sort: $MOCK_DATA")
        MOCK_DATA.sort()
        //Log.d(APP_NAME,"Data after sort: $MOCK_DATA")
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                // camera permission already granted
            }

            else -> {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }
        var selected_item = 0
        setContent {
            FridgeFreshnessTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var current_view: FFAppView by remember { mutableStateOf(FFAppView.ITEM_LIST) }
                    fun switch_view(newView: FFAppView,option1:Int?) {
                        when (newView){
                            FFAppView.ITEM_LIST, FFAppView.NEW_ITEM, FFAppView.CAMERA -> {
                                current_view = newView
                            }
                            FFAppView.ITEM -> {
                                option1?.let{
                                    current_view = newView
                                    selected_item = option1
                                }
                            }
                        }

                        Log.d(MainActivity.APP_NAME, current_view.toString())
                    }
                    key(current_view) {
                        when (current_view) {
                            FFAppView.ITEM_LIST -> MainView(
                                switchView = ::switch_view,
                                fridgeData = MOCK_DATA,
                                modifier = Modifier.padding(innerPadding)
                            )

                            FFAppView.NEW_ITEM, FFAppView.CAMERA -> NewItemView(
                                current_view,
                                switchView = ::switch_view,
                                submitItem =
                                {
                                    MOCK_DATA.add(it)
                                    MOCK_DATA.sort()
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                            FFAppView.ITEM -> SingleItemView(
                                switchView=::switch_view,
                                itemData=MOCK_DATA[selected_item],
                                removeItem=MOCK_DATA::remove,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SetPortraitOrientationOnly() {
    val context = LocalContext.current
    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}




