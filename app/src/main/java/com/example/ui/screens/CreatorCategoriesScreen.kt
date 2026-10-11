package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class CreatorCategoryInfo(
    val name: String,
    val icon: ImageVector,
    val description: String
)

val ALL_CREATOR_CATEGORIES = listOf(
    CreatorCategoryInfo("Art", Icons.Filled.Brush, "Paintings, sketches, digital art & crafts"),
    CreatorCategoryInfo("AI", Icons.Filled.AutoAwesome, "Generative intelligence, prompts & machine learning"),
    CreatorCategoryInfo("Software Development", Icons.Filled.Code, "Engineering apps, cloud architecture & open source"),
    CreatorCategoryInfo("Coding", Icons.Filled.Terminal, "Programming languages, algorithms & tutorials"),
    CreatorCategoryInfo("Technology", Icons.Filled.Laptop, "Innovations, hardware & digital future"),
    CreatorCategoryInfo("Gadgets", Icons.Filled.Devices, "Smartphones, wearables & tech reviews"),
    CreatorCategoryInfo("Education", Icons.Filled.School, "Academic insights, lessons & self-growth"),
    CreatorCategoryInfo("Gaming", Icons.Filled.SportsEsports, "Live gameplay, esports & game guides"),
    CreatorCategoryInfo("Photography", Icons.Filled.CameraAlt, "Portraits, landscapes & camera tips"),
    CreatorCategoryInfo("Video Creation", Icons.Filled.Videocam, "Reels, cinematography & storytelling"),
    CreatorCategoryInfo("Entertainment", Icons.Filled.TheaterComedy, "Comedy, music, memes & culture"),
    CreatorCategoryInfo("Lifestyle", Icons.Filled.Spa, "Daily routines, wellness & inspiration"),
    CreatorCategoryInfo("Food", Icons.Filled.Restaurant, "Culinary recipes, street food & reviews"),
    CreatorCategoryInfo("Travel", Icons.Filled.Flight, "Exploration, road trips & world destinations"),
    CreatorCategoryInfo("Sports", Icons.Filled.SportsCricket, "Cricket, football, athletics & tournaments"),
    CreatorCategoryInfo("Fitness", Icons.Filled.FitnessCenter, "Workouts, strength & healthy living"),
    CreatorCategoryInfo("Business", Icons.Filled.TrendingUp, "Startups, freelancing & leadership"),
    CreatorCategoryInfo("Science", Icons.Filled.Science, "Space, physics, biology & discoveries"),
    CreatorCategoryInfo("Nature", Icons.Filled.Park, "Wildlife, outdoors & environment"),
    CreatorCategoryInfo("DIY", Icons.Filled.Build, "Handmade projects, repairs & lifehacks"),
    CreatorCategoryInfo("Community", Icons.Filled.Groups, "Nagod creators, meetups & local culture"),
    CreatorCategoryInfo("Social Impact", Icons.Filled.VolunteerActivism, "Civic action, youth empowerment & awareness"),
    CreatorCategoryInfo("Other", Icons.Filled.Category, "Original ideas & eclectic creator interests")
)

/**
 * Requirement 6: Creator Categories Onboarding & Selection Screen
 * Multi-select searchable categories screen.
 * Saves preferences to the user's account and enhances discovery.
 */
@Composable
fun CreatorCategoriesScreen(
    initialSelectedCategories: List<String> = emptyList(),
    onSaveCategories: (List<String>) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedCategories = remember { mutableStateListOf<String>().apply { addAll(initialSelectedCategories) } }

    val filteredCategories = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            ALL_CREATOR_CATEGORIES
        } else {
            ALL_CREATOR_CATEGORIES.filter {
                it.name.contains(searchQuery.trim(), ignoreCase = true) ||
                it.description.contains(searchQuery.trim(), ignoreCase = true)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("categories_skip_button")
                    ) {
                        Text("Skip for Now", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { onSaveCategories(selectedCategories.toList()) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StarPrimary),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(48.dp)
                            .testTag("categories_continue_button")
                    ) {
                        Text(
                            text = if (selectedCategories.isEmpty()) "Continue" else "Save (${selectedCategories.size})",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("creator_categories_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Choose Your Passions",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Select what you love creating or exploring on AKASH SOCIAL",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = StarPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${selectedCategories.size} Selected",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarPrimaryLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search categories (e.g. AI, Coding, Art)...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StarPrimaryLight,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("categories_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Grid of Category Chips
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("categories_grid")
            ) {
                items(filteredCategories, key = { it.name }) { category ->
                    val isSelected = selectedCategories.contains(category.name)

                    CategoryCard(
                        category = category,
                        isSelected = isSelected,
                        onToggle = {
                            if (isSelected) {
                                selectedCategories.remove(category.name)
                            } else {
                                selectedCategories.add(category.name)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CreatorCategoryInfo,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) StarPrimary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) StarPrimaryLight else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("category_item_${category.name}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            Brush.linearGradient(listOf(StarPrimary, ReachCyan))
                        } else {
                            Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant))
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category.description,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    lineHeight = 13.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Selected",
                    tint = StarPrimaryLight,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
