package com.example.cgpaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.UUID

// Data models remain the same
data class SubjectEntry(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val grade: String = "",
    val credits: String = ""
)

data class Semester(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val subjects: List<SubjectEntry> = listOf(SubjectEntry())
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    AktuCgpaApp()
                }
            }
        }
    }
}

// Helper function to calculate SGPA, returns Triple(sgpa, totalCredits, errorMessage)
private fun calculateSgpa(subjects: List<SubjectEntry>, gradeMap: Map<String, Double>): Triple<Double, Double, String?> {
    var totalCreditPoints = 0.0
    var totalCredits = 0.0
    subjects.forEach { s ->
        val credits = s.credits.trim().toDoubleOrNull()
        if (credits == null || credits < 0) return Triple(0.0, 0.0, "Invalid Credits")
        val gradePoint = gradeMap[s.grade.trim().uppercase()]
        if (gradePoint == null) return Triple(0.0, 0.0, "Unknown Grade: '${s.grade}'")

        totalCreditPoints += gradePoint * credits
        totalCredits += credits
    }
    val sgpa = if (totalCredits > 0) totalCreditPoints / totalCredits else 0.0
    return Triple(sgpa, totalCredits, null)
}


@Composable
fun AktuCgpaApp() {
    var gradeMap by remember {
        mutableStateOf(
            linkedMapOf(
                "A+" to 10.0, "A" to 9.0, "B+" to 8.0, "B" to 7.0,
                "C" to 6.0, "D" to 5.0, "E" to 4.0, "F" to 0.0
            )
        )
    }

    var semesters by remember {
        mutableStateOf(listOf(Semester(title = "Semester 1")))
    }

    var includePercentage by remember { mutableStateOf(false) }

    // Optimization: Calculations are wrapped in `remember` to run only when inputs change.
    val calculationResult by remember(semesters, gradeMap) {
        derivedStateOf {
            val semesterResults = semesters.map { sem ->
                val (sgpa, credits, error) = calculateSgpa(sem.subjects, gradeMap)
                Triple(sgpa, credits, error)
            }

            val validSemesters = semesterResults.filter { it.third == null }
            val totalWeightedSgpa = validSemesters.sumOf { it.first * it.second }
            val totalCredits = validSemesters.sumOf { it.second }
            val cgpa = if (totalCredits > 0) totalWeightedSgpa / totalCredits else 0.0

            cgpa to semesterResults
        }
    }

    val cgpa = calculationResult.first
    val semesterResults = calculationResult.second
    val percentage = if (includePercentage) (cgpa - 0.75) * 10.0 else null

    // Refactor: Use a single LazyColumn for the entire screen.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("AKTU SGPA / CGPA Calculator", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
        }

        item {
            GradeScaleEditor(
                gradeMap = gradeMap,
                onMapChange = { gradeMap = it }
            )
        }

        item {
            Text("Semesters", style = MaterialTheme.typography.titleMedium)
        }

        // Use itemsIndexed for semesters
        itemsIndexed(items = semesters, key = { _, sem -> sem.id }) { index, semester ->
            SemesterCard(
                semester = semester,
                semesterResult = semesterResults[index],
                onSemesterChange = { updatedSemester ->
                    semesters = semesters.map { if (it.id == updatedSemester.id) updatedSemester else it }
                },
                onRemoveSemester = {
                    if (semesters.size > 1) {
                        semesters = semesters.filterNot { it.id == semester.id }
                    }
                }
            )
        }

        item {
            Button(
                onClick = {
                    val nextNum = semesters.size + 1
                    semesters = semesters + Semester(title = "Semester $nextNum")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Semester")
            }
        }

        item {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = includePercentage, onCheckedChange = { includePercentage = it })
                Column {
                    Text("Show percentage (CGPA − 0.75) × 10")
                    Text("Verify with current AKTU ordinance", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Overall CGPA: ${"%.2f".format(cgpa)}", style = MaterialTheme.typography.titleLarge)
            if (percentage != null) {
                Text(
                    "Equivalent Percentage: ${"%.2f".format(percentage.coerceIn(0.0, 100.0))}%",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun GradeScaleEditor(
    gradeMap: LinkedHashMap<String, Double>,
    onMapChange: (LinkedHashMap<String, Double>) -> Unit
) {
    Column {
        Text("Grade Scale (Editable)", style = MaterialTheme.typography.titleMedium)
        gradeMap.forEach { (grade, points) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = grade,
                    onValueChange = { newGrade ->
                        val key = newGrade.trim().uppercase()
                        if (key.isNotBlank()) {
                            val newMap = LinkedHashMap<String, Double>()
                            gradeMap.forEach { (g, p) ->
                                if (g == grade) newMap[key] = p else newMap[g] = p
                            }
                            onMapChange(newMap)
                        }
                    },
                    label = { Text("Grade") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = points.toString(),
                    onValueChange = { newPoints ->
                        newPoints.toDoubleOrNull()?.let {
                            onMapChange(LinkedHashMap(gradeMap).apply { this[grade] = it })
                        }
                    },
                    label = { Text("Points") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                if (gradeMap.size > 1) {
                    TextButton(onClick = { onMapChange(LinkedHashMap(gradeMap).apply { remove(grade) }) }) {
                        Text("Del")
                    }
                }
            }
        }
        TextButton(onClick = {
            onMapChange(LinkedHashMap(gradeMap).apply { this["NEW"] = 0.0 })
        }) {
            Text("Add Grade Mapping")
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun SemesterCard(
    semester: Semester,
    semesterResult: Triple<Double, Double, String?>,
    onSemesterChange: (Semester) -> Unit,
    onRemoveSemester: () -> Unit
) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = semester.title,
                    onValueChange = { onSemesterChange(semester.copy(title = it)) },
                    label = { Text("Semester Title") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onRemoveSemester) { Text("Remove") }
            }

            semester.subjects.forEach { subject ->
                SubjectRow(
                    entry = subject,
                    onChange = { updatedSubject ->
                        val newSubjects = semester.subjects.map {
                            if (it.id == updatedSubject.id) updatedSubject else it
                        }
                        onSemesterChange(semester.copy(subjects = newSubjects))
                    },
                    onRemove = {
                        if (semester.subjects.size > 1) {
                            val newSubjects = semester.subjects.filterNot { it.id == subject.id }
                            onSemesterChange(semester.copy(subjects = newSubjects))
                        }
                    }
                )
            }

            Button(
                onClick = {
                    onSemesterChange(semester.copy(subjects = semester.subjects + SubjectEntry()))
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Add Subject")
            }

            val (sgpa, totalCredits, error) = semesterResult
            if (error != null) {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            } else {
                Text(
                    "SGPA: ${"%.2f".format(sgpa)} | Credits: ${"%.1f".format(totalCredits)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun SubjectRow(
    entry: SubjectEntry,
    onChange: (SubjectEntry) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = entry.grade,
            onValueChange = { onChange(entry.copy(grade = it)) },
            label = { Text("Grade") },
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = entry.credits,
            onValueChange = { onChange(entry.copy(credits = it)) },
            label = { Text("Credits") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        // Note: Subject Name was removed for brevity to fit Grade/Credits on one line cleanly.
        // It can be added back with a vertical Column arrangement if needed.
        TextButton(onClick = onRemove) { Text("Del") }
    }
}