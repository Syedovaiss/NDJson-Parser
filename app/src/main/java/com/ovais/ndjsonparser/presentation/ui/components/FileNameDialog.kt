package com.ovais.ndjsonparser.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ovais.ndjsonparser.R
import com.ovais.ndjsonparser.domain.model.FileFormat

@Composable
fun FileNameDialog(
    format: FileFormat,
    defaultFileName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var fileName by remember { mutableStateOf(defaultFileName) }
    val fileExtension = format.name.lowercase()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.dialog_save_file_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.dialog_save_file_message, format.name),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { newValue ->
                        // Remove extension if user types it, we'll add it automatically
                        val withoutExt = if (newValue.endsWith(".$fileExtension")) {
                            newValue.substringBeforeLast(".")
                        } else {
                            newValue
                        }
                        // Remove invalid characters for file names
                        fileName = withoutExt.filter { 
                            it.isLetterOrDigit() || it == '_' || it == '-' || it == ' '
                        }
                    },
                    label = { Text(stringResource(R.string.dialog_file_name_label)) },
                    placeholder = { Text(stringResource(R.string.dialog_file_name_placeholder)) },
                    suffix = { Text(".$fileExtension") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalFileName = if (fileName.isBlank()) {
                        defaultFileName
                    } else {
                        "$fileName.$fileExtension"
                    }
                    onConfirm(finalFileName)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.dialog_cancel))
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    )
}

