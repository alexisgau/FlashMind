package com.example.flashmind.presentation.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.flashmind.R
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.model.UserData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToLessonContent: (lessonId: Int, lessonTitle: String) -> Unit,
    onNavigateToAddCategory: () -> Unit,
    onNavigateToAccountSettings: (String) -> Unit,
    onNavigateToAddLesson: (categoryId: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categoryToDelete by viewModel.categoryToDelete.collectAsStateWithLifecycle()
    val lessonToDelete by viewModel.lessonToDelete.collectAsStateWithLifecycle()
    val lessonToEdit by viewModel.lessonToEdit.collectAsStateWithLifecycle()
    val showNameInput by viewModel.showNameInput.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            val userData = (uiState as? HomeUiState.Success)?.userData ?: UserData.Init
            TopBar(
                modifier = Modifier.padding(top = 20.dp, start = 16.dp),
                userData = userData,
                navigateToAccountSettings = onNavigateToAccountSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCategory,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.home_add_category_cd)
                )
            }
        }
    ) { innerPadding ->

        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${stringResource(id = R.string.error_prefix)} ${state.message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        HomeCard()
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = stringResource(id = R.string.home_my_categories_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.categories.isEmpty()) {
                        item {
                            EmptyCategoryList()
                        }
                    } else {
                        state.categories.forEach { category ->
                            val isExpanded = state.expandedCategoryIds.contains(category.id)
                            val lessons = state.lessonsMap[category.id]
                            val lessonCount = state.lessonCounts[category.id] ?: 0

                            item(key = "cat_${category.id}") {
                                ExpandableCategoryItem(
                                    category = category,
                                    isExpanded = isExpanded,
                                    lessonCount = lessonCount,
                                    onToggleExpand = { viewModel.toggleCategoryExpansion(category.id) },
                                    onDeleteRequest = { viewModel.requestCategoryDeletion(category) },
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 6.dp
                                    ),
                                    content = {
                                        Column(Modifier.fillMaxWidth()) {
                                            if (lessons == null) {
                                                Box(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 20.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(
                                                            24.dp
                                                        )
                                                    )
                                                }
                                            } else if (lessons.isEmpty()) {
                                                Text(
                                                    stringResource(id = R.string.home_no_lessons_in_category),
                                                    modifier = Modifier.padding(
                                                        start = 32.dp,
                                                        top = 8.dp,
                                                        bottom = 16.dp
                                                    ),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            } else {
                                                lessons.forEach { lesson ->
                                                    LessonItem(
                                                        lesson = lesson,
                                                        onClick = {
                                                            onNavigateToLessonContent(
                                                                lesson.id,
                                                                lesson.tittle
                                                            )
                                                        },
                                                        onDeleteClick = {
                                                            viewModel.requestLessonDeletion(
                                                                lesson
                                                            )
                                                        },
                                                        onEditClick = {
                                                            viewModel.requestLessonEdit(
                                                                lesson
                                                            )
                                                        },
                                                        modifier = Modifier.padding(
                                                            start = 16.dp,
                                                            end = 16.dp,
                                                            top = 4.dp,
                                                            bottom = 4.dp
                                                        )
                                                    )
                                                    Divider(
                                                        modifier = Modifier.padding(
                                                            horizontal = 16.dp
                                                        )
                                                    )
                                                }
                                            }

                                            TextButton(
                                                onClick = { onNavigateToAddLesson(category.id) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Add,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(stringResource(id = R.string.home_add_lesson_button))
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelDeletion() },
            title = { Text(stringResource(id = R.string.dialog_delete_category_title)) },
            text = {
                Text(
                    stringResource(
                        id = R.string.dialog_delete_category_message,
                        category.name
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteCategory() }) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDeletion() }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    lessonToDelete?.let { lesson ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelDeletion() },
            title = { Text(stringResource(id = R.string.dialog_delete_lesson_title)) },
            text = {
                Text(
                    stringResource(
                        id = R.string.dialog_delete_lesson_message,
                        lesson.tittle
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteLesson() }) {
                    Text(stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDeletion() }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }
    if (showNameInput) {
        NameInputDialog(
            onConfirm = { name ->
                viewModel.updateUserName(name)
            })
    }

    lessonToEdit?.let { lesson ->
        EditLessonDialog(
            lesson = lesson,
            onDismiss = viewModel::cancelEditOrDelete,
            onConfirm = viewModel::confirmLessonEdit
        )
    }

    }



@Composable
fun ExpandableCategoryItem(
    category: Category,
    isExpanded: Boolean,
    lessonCount: Int,
    onToggleExpand: () -> Unit,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand),
            shape = if (isExpanded) {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            } else {
                RoundedCornerShape(16.dp)
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.folder_icon),
                    contentDescription = stringResource(id = R.string.home_category_item_cd),
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = stringResource(
                            id = R.string.home_category_item_lessons_count,
                            lessonCount
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onDeleteRequest) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.home_category_delete_cd),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    painter = if (isExpanded) painterResource(R.drawable.arrow_circle_up_icon) else painterResource(
                        R.drawable.arrow_circle_down_icon
                    ),
                    contentDescription = if (isExpanded) stringResource(id = R.string.home_category_collapse_cd) else stringResource(
                        id = R.string.home_category_expand_cd
                    ),
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun LessonItem(
    lesson: Lesson,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.lesson_icon),
            contentDescription = stringResource(id = R.string.home_lesson_item_cd),
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.tittle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(id = R.string.home_lesson_item_prompt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onEditClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(id = R.string.home_lesson_edit_cd),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(id = R.string.home_lesson_delete_cd),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
    }
}


@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    userData: UserData,
    navigateToAccountSettings: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (userData) {
            is UserData.Success -> {
                val greetingText = if (userData.name.isNotEmpty()) {
                    stringResource(id = R.string.home_greeting, userData.name)
                } else {
                    stringResource(id = R.string.home_welcome_generic)
                }

                Text(
                    text = greetingText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (userData.imageUrl.isNotEmpty()) {
                    IconButton(onClick = { navigateToAccountSettings(userData.imageUrl) }) {
                        AsyncImage(
                            model = userData.imageUrl,
                            contentDescription = stringResource(id = R.string.account_profile_picture_cd),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }

                } else {
                    IconButton(onClick = { navigateToAccountSettings(userData.imageUrl) }) {
                        Icon(
                            painter = painterResource(R.drawable.default_profile_ic),
                            contentDescription = stringResource(id = R.string.account_default_profile_cd),
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }

                }
            }

            is UserData.Error, UserData.Init -> {
                Text(
                    text = stringResource(id = R.string.home_greeting_default),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = stringResource(id = R.string.account_default_profile_cd),
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .border(2.dp, Color.Gray, CircleShape)
                )
            }
        }
    }
}


@Composable
fun HomeCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "FlashMind",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.home_main_card_title),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Image(
                painter = painterResource(id = R.drawable.mastan),
                contentDescription = stringResource(id = R.string.home_main_card_image_cd),
                modifier = Modifier
                    .width(150.dp)
                    .height(500.dp)
            )
        }
    }
}

@Composable
fun EmptyCategoryList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.folder_plus),
            contentDescription = stringResource(id = R.string.home_empty_categories_title),
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.home_empty_categories_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.home_empty_categories_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyCategoryListPreview() {
    MaterialTheme {
        EmptyCategoryList()
    }
}

@Composable
fun EditLessonDialog(
    lesson: Lesson,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember(lesson.tittle) { mutableStateOf(lesson.tittle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.dialog_edit_lesson_title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(id = R.string.dialog_lesson_name_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank() && text != lesson.tittle
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

    @Composable
    fun NameInputDialog(
        onConfirm: (String) -> Unit,
        onDismiss: () -> Unit = {}
    ) {
        var name by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(R.string.hello),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(stringResource(R.string.how_should_we_call_you))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { newValue ->
                            if (newValue.length <= 16) {
                                name = newValue
                            }
                        },
                        label = { Text(stringResource(R.string.your_name))},
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(name) },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.continue_button))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }