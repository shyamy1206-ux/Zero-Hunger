package com.zerohunger.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zerohunger.app.data.AppRepository
import com.zerohunger.app.data.FoodItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(private val repository: AppRepository) : ViewModel() {
    val syncError = repository.syncErrorMessage.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    val availableFood = repository.availableFood.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val waitingQueue = repository.waitingQueue.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val actionHistory = repository.actionHistory.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun distributeFood(beneficiaryId: Long, foodItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.distributeFood(beneficiaryId, foodItemId, quantity)
        }
    }

    fun undoLastAction() {
        viewModelScope.launch {
            repository.undoLastAction()
        }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.loadSampleData()
        }
    }

    fun toggleSafetyApproval(foodItemId: Long, currentApproval: Boolean) {
        viewModelScope.launch {
            repository.toggleSafetyApproval(foodItemId, !currentApproval)
        }
    }

    fun exportCsvReport(context: android.content.Context) {
        viewModelScope.launch {
            val history = actionHistory.value
            if (history.isEmpty()) return@launch

            val sb = StringBuilder()
            sb.appendLine("Action Type,Description,Timestamp,Can Undo")
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            for (item in history) {
                val date = dateFormat.format(java.util.Date(item.timestamp))
                val desc = item.description.replace(",", ";")
                sb.appendLine("${item.actionType},$desc,$date,${item.canUndo}")
            }

            try {
                val fileName = "ZeroHunger_Report_${System.currentTimeMillis()}.csv"
                val dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                val file = java.io.File(dir, fileName)
                file.writeText(sb.toString())
                android.widget.Toast.makeText(context, "CSV saved to Downloads/$fileName", android.widget.Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Export failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    fun exportPdfReport(context: android.content.Context) {
        viewModelScope.launch {
            val history = actionHistory.value
            if (history.isEmpty()) return@launch

            try {
                val pdfDocument = android.graphics.pdf.PdfDocument()
                val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 14f
                }
                
                canvas.drawText("Zero Hunger Food Bank - Audit Log", 40f, 50f, paint.apply { textSize = 20f; isFakeBoldText = true })
                
                paint.textSize = 12f
                paint.isFakeBoldText = false
                var yPosition = 100f
                val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())

                for (item in history.take(30)) { // Limit to 30 for 1 page simplicity
                    if (yPosition > 800f) break
                    val date = dateFormat.format(java.util.Date(item.timestamp))
                    canvas.drawText("[${item.actionType}] $date", 40f, yPosition, paint)
                    canvas.drawText(item.description, 40f, yPosition + 15f, paint)
                    yPosition += 40f
                }

                pdfDocument.finishPage(page)

                val fileName = "ZeroHunger_Report_${System.currentTimeMillis()}.pdf"
                val dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                val file = java.io.File(dir, fileName)
                pdfDocument.writeTo(java.io.FileOutputStream(file))
                pdfDocument.close()

                android.widget.Toast.makeText(context, "PDF saved to Downloads/$fileName", android.widget.Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "PDF Export failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
}

class AdminViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
