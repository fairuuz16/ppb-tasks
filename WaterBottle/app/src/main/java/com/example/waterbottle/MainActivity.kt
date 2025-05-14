package com.example.waterbottle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waterbottle.ui.theme.WaterBottleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WaterBottleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var usedWaterAmount by remember {
                        mutableIntStateOf(0)
                    }
                    val totalWaterAmount = remember {
                        2000
                    }
                    var isBottleFull by remember {
                        mutableStateOf(false)
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            WatterBottle(
                                totalWaterAmount = totalWaterAmount,
                                unit = "ml",
                                usedWaterAmount = usedWaterAmount
                            )

                            if (isBottleFull) {
                                FullBottleAlert()
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Total Amount is : $totalWaterAmount ml",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xff279EFF)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Card(
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .padding(bottom = 24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.LightGray.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (usedWaterAmount + 200 <= totalWaterAmount) {
                                            usedWaterAmount += 200
                                            isBottleFull = (usedWaterAmount == totalWaterAmount)
                                        } else {
                                            usedWaterAmount = totalWaterAmount
                                            isBottleFull = true
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xff279EFF)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Fill",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Button(
                                    onClick = {
                                        usedWaterAmount = 0
                                        isBottleFull = false
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Drink All",
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullBottleAlert() {
    Card(
        modifier = Modifier
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1ABC9C).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Text(
            text = "Bottle Full. Time to Drink!",
            modifier = Modifier.padding(16.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}