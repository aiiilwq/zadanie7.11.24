package com.example.googlemapsexample.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import com.example.googlemapsexample.data.LocationData
import com.example.googlemapsexample.utils.LocationUtils
import com.example.googlemapsexample.viewmodel.LocationViewModel

@Composable
fun MainScreen(
    context: Context,
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    navHostController: NavHostController
) {
    val pickedLocation by viewModel.pickedLocationData.collectAsState()
    val pickedLocationAddress by viewModel.pickedLocationAddress.collectAsState()
    val userLocationData by viewModel.userLocationData.collectAsState()
    val userLocationAddress by viewModel.userLocationAddress.collectAsState()

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permission ->
            if (permission[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permission[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                locationUtils.getLocation()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity, Manifest.permission.ACCESS_FINE_LOCATION
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    Toast.makeText(context, "Отказано в доступе", Toast.LENGTH_LONG).show()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF5D9B9B))  // Фон с цветом #5D9B9B
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MinimalistLocationCard(
            title = "Моя локация",
            locationData = userLocationData,
            locationAddress = userLocationAddress
        )

        Spacer(Modifier.height(8.dp))

        MinimalistLocationCard(
            title = "Выбранная локация",
            locationData = pickedLocation,
            locationAddress = pickedLocationAddress
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                navHostController.navigate(route = Graph.MAP_SCREEN)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Перейти к карте",
                color = Color(0xFF5D9B9B),  // Используем #5D9B9B для текста
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MinimalistLocationCard(title: String, locationData: LocationData?, locationAddress: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5D9B9B)  // Используем #5D9B9B для заголовка
        )
        if (locationData != null) {
            Text(
                text = if (locationAddress.isNotBlank()) locationAddress
                else "Широта: ${locationData.latitude}, Долгота: ${locationData.longitude}",
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        } else {
            Text("Локация не найдена", color = Color.Gray, fontSize = 14.sp)
        }
    }
}
