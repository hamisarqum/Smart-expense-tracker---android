package com.example.myapplication1.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication1.R
import com.example.myapplication1.data.local.TransactionEntity
import com.example.myapplication1.model.Category
import com.example.myapplication1.model.TransactionType
import com.example.myapplication1.ui.theme.Dimensions
import com.example.myapplication1.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.util.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    transactionId: Long? = null,
    currencyCode: String = "GBP",
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val isEditMode = transactionId != null && transactionId != -1L
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by rememberSaveable { mutableStateOf<Category?>(null) }
    var notes by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val currencySymbol = try {
        Currency.getInstance(currencyCode).symbol
    } catch (e: Exception) {
        "£"
    }

    LaunchedEffect(transactionId) {
        if (isEditMode) {
            val entity = viewModel.getTransactionById(transactionId!!)
            entity?.let {
                title = it.title
                amount = it.amount.toString()
                type = TransactionType.valueOf(it.type)
                selectedCategory = Category.entries.find { cat -> cat.name == it.category }
                notes = it.notes
                date = it.date
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.PaddingMedium)
            .verticalScroll(rememberScrollState())
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = type == TransactionType.INCOME,
                onClick = { type = TransactionType.INCOME },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text(stringResource(R.string.filter_income))
            }
            SegmentedButton(
                selected = type == TransactionType.EXPENSE,
                onClick = { type = TransactionType.EXPENSE },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text(stringResource(R.string.filter_expense))
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = false
            },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            isError = titleError,
            supportingText = if (titleError) {
                { Text("Title is required") }
            } else null,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

        OutlinedTextField(
            value = amount,
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull() != null) {
                    amount = it
                    amountError = false
                }
            },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            isError = amountError,
            supportingText = if (amountError) {
                { Text("Amount must be greater than zero") }
            } else null,
            prefix = { Text(currencySymbol) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory?.displayName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                isError = categoryError,
                supportingText = if (categoryError) {
                    { Text("Please select a category") }
                } else null
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                Category.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                            categoryError = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.SpacingMedium))

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (Optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(Dimensions.SpacingLarge))

        Button(
            onClick = {
                val isTitleValid = title.isNotBlank()
                val isAmountValid = amount.toDoubleOrNull()?.let { it > 0 } ?: false
                val isCategoryValid = selectedCategory != null

                titleError = !isTitleValid
                amountError = !isAmountValid
                categoryError = !isCategoryValid

                if (isTitleValid && isAmountValid && isCategoryValid && !isSaving) {
                    isSaving = true
                    val transaction = TransactionEntity(
                        id = if (isEditMode) transactionId!! else 0L,
                        title = title.trim(),
                        amount = amount.toDouble(),
                        type = type.name,
                        category = selectedCategory!!.name,
                        date = date,
                        notes = notes.trim(),
                        createdAt = if (isEditMode) date else System.currentTimeMillis()
                    )
                    
                    scope.launch {
                        if (isEditMode) {
                            viewModel.updateTransaction(transaction)
                        } else {
                            viewModel.insertTransaction(transaction)
                        }
                        onSaveSuccess()
                    }
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth().height(Dimensions.ButtonHeight)
        ) {
            Text(if (isEditMode) stringResource(R.string.edit) else stringResource(R.string.save))
        }

        TextButton(
            onClick = onBack,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.cancel))
        }
    }
}
