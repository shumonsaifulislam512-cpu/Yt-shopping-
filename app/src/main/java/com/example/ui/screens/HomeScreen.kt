package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Product
import com.example.ui.components.LuminaTopBar
import com.example.ui.components.PersonalizationMatchBadge
import com.example.ui.components.ProductGridCard
import com.example.ui.components.RecommendationHeroCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AmberGlow
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.LuminaViewModel

@Composable
fun HomeScreen(
    viewModel: LuminaViewModel,
    onNavigateToProduct: (Product) -> Unit,
    onNavigateToCart: () -> Unit,
    onOpenAiStylist: () -> Unit,
    onOpenTasteProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasteProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val curatedRecommendations by viewModel.curatedRecommendations.collectAsStateWithLifecycle()
    val becauseYouExplored by viewModel.becauseYouExplored.collectAsStateWithLifecycle()

    val categories = listOf("All", "Audio & Tech", "Wearables", "Home & Living", "Wellness & Beauty")

    // Filter products
    val filteredProducts = viewModel.allProducts.filter { product ->
        val matchesCategory = selectedCategory == "All" || product.category == selectedCategory
        val matchesQuery = searchQuery.isBlank() ||
                product.title.contains(searchQuery, ignoreCase = true) ||
                product.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesQuery
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Top Bar
        LuminaTopBar(
            tasteProfile = tasteProfile,
            cartItemCount = cartItems.sumOf { it.quantity },
            onOpenCart = onNavigateToCart,
            onOpenAiStylist = onOpenAiStylist,
            onOpenTasteSettings = onOpenTasteProfile
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search products, styles, or specs...", fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .testTag("search_field")
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Personalized Recommendation Hero Banner
            if (searchQuery.isBlank()) {
                item {
                    val topPick = curatedRecommendations.firstOrNull()
                    if (topPick != null) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = AmberAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Curated Just For You",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }

                                Text(
                                    text = "Tailored to your taste",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable { onOpenTasteProfile() }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            RecommendationHeroCard(
                                match = topPick,
                                onProductClick = onNavigateToProduct,
                                onAddToCart = { viewModel.addToCart(it) }
                            )
                        }
                    }
                }

                // Carousels: Top Matches based on Taste
                item {
                    Column(modifier = Modifier.padding(top = 14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Personalized Matches (${tasteProfile.budgetTier.label})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = "Customize",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onOpenTasteProfile() }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(curatedRecommendations.drop(1)) { match ->
                                Box(modifier = Modifier.width(180.dp)) {
                                    ProductGridCard(
                                        product = match.product,
                                        isWishlisted = wishlistIds.contains(match.product.id),
                                        matchScore = match.matchScore,
                                        onProductClick = onNavigateToProduct,
                                        onToggleWishlist = { viewModel.toggleWishlist(it) },
                                        onAddToCart = { viewModel.addToCart(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Category Filter Pills
            item {
                Column(modifier = Modifier.padding(top = 18.dp)) {
                    Text(
                        text = "Explore by Category",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { viewModel.selectCategory(cat) }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // "Because You Explored..." section if available
            val viewedProduct = becauseYouExplored.first
            val relatedItems = becauseYouExplored.second
            if (searchQuery.isBlank() && viewedProduct != null && relatedItems.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(top = 20.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Because you explored ",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = viewedProduct.title.take(20) + "...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(relatedItems) { match ->
                                Box(modifier = Modifier.width(180.dp)) {
                                    ProductGridCard(
                                        product = match.product,
                                        isWishlisted = wishlistIds.contains(match.product.id),
                                        matchScore = match.matchScore,
                                        onProductClick = onNavigateToProduct,
                                        onToggleWishlist = { viewModel.toggleWishlist(it) },
                                        onAddToCart = { viewModel.addToCart(it) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Main Catalog Grid (2 columns)
            item {
                Text(
                    text = if (searchQuery.isNotBlank()) "Search Results (${filteredProducts.size})" else "All Products",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 8.dp)
                )
            }

            val productPairs = filteredProducts.chunked(2)
            items(productPairs) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProductGridCard(
                            product = pair[0],
                            isWishlisted = wishlistIds.contains(pair[0].id),
                            onProductClick = onNavigateToProduct,
                            onToggleWishlist = { viewModel.toggleWishlist(it) },
                            onAddToCart = { viewModel.addToCart(it) }
                        )
                    }

                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductGridCard(
                                product = pair[1],
                                isWishlisted = wishlistIds.contains(pair[1].id),
                                onProductClick = onNavigateToProduct,
                                onToggleWishlist = { viewModel.toggleWishlist(it) },
                                onAddToCart = { viewModel.addToCart(it) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
