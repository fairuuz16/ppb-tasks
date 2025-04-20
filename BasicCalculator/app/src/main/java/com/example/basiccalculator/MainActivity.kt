package com.example.basiccalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorApp()
        }
    }
}
@Composable
fun CalculatorApp() {
    var number1 by remember { mutableStateOf(TextFieldValue("")) }
    var number2 by remember { mutableStateOf(TextFieldValue("")) }
    var result by remember { mutableStateOf("Result: ") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextField(label = "Enter first number", value = number1) { number1 = it }
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(label = "Enter second number", value = number2) { number2 = it }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AddButton { result = calculate(number1.text, number2.text, "+") }
            SubButton { result = calculate(number1.text, number2.text, "-") }
            MulButton { result = calculate(number1.text, number2.text, "*") }
            DivButton { result = calculate(number1.text, number2.text, "/") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(result)
    }
}

@Composable
fun CustomTextField(label: String, value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AddButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("add")
    }
}

@Composable
fun SubButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("sub")
    }
}

@Composable
fun MulButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("mul")
    }
}

@Composable
fun DivButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("div")
    }
}

fun calculate(num1: String, num2: String, operator: String): String {
    val n1 = num1.toDoubleOrNull()
    val n2 = num2.toDoubleOrNull()
    if (n1 == null || n2 == null) {
        return "Invalid input"
    }
    val rawResult = when (operator) {
        "+" -> n1 + n2
        "-" -> n1 - n2
        "*" -> n1 * n2
        "/" -> if (n2 != 0.0) n1 / n2 else return "Cannot divide by zero"
        else -> return "Unknown operation"
    }

    val formattedResult = if (rawResult % 1.0 == 0.0) rawResult.toInt().toString() else rawResult.toString()
    return "Result: $formattedResult"
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CalculatorApp()
}