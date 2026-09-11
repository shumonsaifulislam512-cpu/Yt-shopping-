package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.SampleProducts
import com.example.model.Order
import com.example.model.Product
import com.example.ui.screens.AiStylistContent
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderConfirmationScreen
import com.example.ui.screens.OrdersListScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.TasteProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LuminaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LuminaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuminaApp(viewModel: LuminaViewModel = viewModel()) {
    val navController = rememberNavController()
    var showAiStylistSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val confirmedOrder by viewModel.confirmedOrder.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home Screen
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { product ->
                        viewModel.selectProduct(product)
                        navController.navigate("product_detail/${product.id}")
                    },
                    onNavigateToCart = {
                        navController.navigate("cart")
                    },
                    onOpenAiStylist = {
                        showAiStylistSheet = true
                    },
                    onOpenTasteProfile = {
                        navController.navigate("taste_profile")
                    }
                )
            }

            // Product Detail Screen
            composable(
                route = "product_detail/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId")
                val product = SampleProducts.getById(productId ?: "") ?: SampleProducts.allProducts.first()

                ProductDetailScreen(
                    product = product,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate("cart") },
                    onNavigateToCheckout = { navController.navigate("checkout") }
                )
            }

            // Cart Screen
            composable("cart") {
                CartScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToCheckout = { navController.navigate("checkout") }
                )
            }

            // Checkout Screen
            composable("checkout") {
                CheckoutScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOrderSuccess = { order ->
                        navController.navigate("order_confirmation/${order.id}") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                )
            }

            // Order Confirmation Screen
            composable(
                route = "order_confirmation/{orderId}",
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId")
                val order = orders.find { it.id == orderId } ?: confirmedOrder ?: Order()

                OrderConfirmationScreen(
                    order = order,
                    onContinueShopping = {
                        viewModel.resetPaymentState()
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onViewOrderHistory = {
                        navController.navigate("orders_list")
                    }
                )
            }

            // Orders List Screen
            composable("orders_list") {
                OrdersListScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSelectOrder = { order ->
                        navController.navigate("order_confirmation/${order.id}")
                    }
                )
            }

            // Taste Profile Screen
            composable("taste_profile") {
                TasteProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // AI Stylist Bottom Sheet
        if (showAiStylistSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAiStylistSheet = false },
                sheetState = sheetState
            ) {
                AiStylistContent(
                    viewModel = viewModel,
                    onNavigateToProduct = { product ->
                        viewModel.selectProduct(product)
                        navController.navigate("product_detail/${product.id}")
                    },
                    onDismiss = { showAiStylistSheet = false }
                )
            }
        }
    }
}
