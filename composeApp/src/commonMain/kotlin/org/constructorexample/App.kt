package org.constructorexample

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import constructorexample.composeapp.generated.resources.Res
import constructorexample.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random

val blue = Color.Blue.copy(alpha = 0.5f)

class DashedVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = StringBuilder()
        val originalToTransformed = mutableListOf<Int>()
        val transformedToOriginal = mutableListOf<Int>()

        for ((originalIndex, i) in text.text.indices.withIndex()) {
            if (i > 0 && i % 1 == 0) {
                transformedText.append('-')
                transformedToOriginal.add(originalIndex)
            }
            transformedText.append(text.text[i])
            originalToTransformed.add(transformedText.length - 1)
            transformedToOriginal.add(originalIndex)
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                originalToTransformed.getOrElse(offset) { transformedText.length }

            override fun transformedToOriginal(offset: Int): Int =
                transformedToOriginal.getOrElse(offset) { text.text.length }
        }

        return TransformedText(AnnotatedString(transformedText.toString()), offsetMapping)
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.padding(16.dp))
            }

            items(200) { index ->
                when {
                    index % 2 == 0 -> {
                        CollapsibleBlock()
                    }

                    index % 3 == 0 -> {
                        WidgetGridBlock()
                    }

                    Random.nextBoolean() -> {
                        CTextInput(true)
                    }

                    else -> CTextInput()
                }
            }

            item {
                Spacer(Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun WidgetGridBlock() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = 100) {
            Cell()
        }
    }
}

@Composable
@Preview
fun Cell() {
    Row(
        modifier = Modifier
            .height(80.dp)
            .width(200.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text("Title")
            Text("Subtitle")
        }
        Image(
            painter = painterResource(Res.drawable.compose_multiplatform),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
        )
    }
}

@Composable
@Preview
fun CollapsibleBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color.LightGray, RoundedCornerShape(16.dp))
    ) {
        val isExpanded = rememberSaveable { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    interactionSource = null,
                    indication = null,
                ) { isExpanded.value = !isExpanded.value }
        ) {
            val text = if (isExpanded.value) {
                "Нажми на галочку, чтобы свернуть"
            } else {
                "Нажми на +, чтобы развернуть"
            }

            Text(text, modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (isExpanded.value) Icons.Default.Done else Icons.Default.Add,
                contentDescription = null
            )
        }

        AnimatedVisibility(
            visible = isExpanded.value
        ) {
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun CTextInput(
    isNumber: Boolean = false
) {
    val value = rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(if (isNumber) "С маской - черточками" else "Без маски")

        TextField(
            value = value.value,
            onValueChange = { value.value = it },
            shape = TextFieldDefaults.OutlinedTextFieldShape,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = blue,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, blue, RoundedCornerShape(8.dp)),
            maxLines = 1,
            visualTransformation = if (isNumber) DashedVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
        )
    }
}