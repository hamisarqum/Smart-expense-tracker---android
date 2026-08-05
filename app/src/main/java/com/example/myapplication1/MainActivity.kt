package com.example.myapplication1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication1.navigation.Screen
import com.example.myapplication1.ui.components.BottomNavBar
import com.example.myapplication1.ui.screens.*
import com.example.myapplication1.ui.theme.MyApplication1Theme
import com.example.myapplication1.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val application = context.applicationContext as ExpenseTrackerApplication
            
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(
                    application.container.userPreferencesRepository,
                    application.container.transactionRepository,
                    application.container.budgetRepository
                )
            )
            
            val userPrefs by settingsViewModel.userPreferences.collectAsStateWithLifecycle()
            
            val useDarkTheme = when (userPrefs.themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            val transactionViewModel: TransactionViewModel = viewModel(
                factory = TransactionViewModelFactory(application.container.transactionRepository)
            )
            
            val budgetViewModel: BudgetViewModel = viewModel(
                factory = BudgetViewModelFactory(application.container.budgetRepository)
            )

            val reportsViewModel: ReportsViewModel = viewModel(
                factory = ReportsViewModelFactory(application.container.reportsRepository)
            )

            val exportViewModel: ExportViewModel = viewModel()

            // Notification permission request
            var hasNotificationPermission by remember {
                mutableStateOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    } else true
                )
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                hasNotificationPermission = isGranted
            }

            LaunchedEffect(userPrefs.budgetNotificationsEnabled) {
                if (userPrefs.budgetNotificationsEnabled && !hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            MyApplication1Theme(darkTheme = useDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                Scaffold(
                    topBar = {
                        val title = when {
                            currentRoute == Screen.Home.route -> stringResource(R.string.app_name)
                            currentRoute == Screen.Transactions.route -> stringResource(R.string.title_transactions)
                            currentRoute == Screen.Reports.route -> stringResource(R.string.title_reports)
                            currentRoute == Screen.Settings.route -> stringResource(R.string.title_settings)
                            currentRoute == Screen.Budget.route -> stringResource(R.string.title_budget)
                            currentRoute?.startsWith("add_transaction") == true -> {
                                val transactionId = navBackStackEntry?.arguments?.getLong("transactionId") ?: -1L
                                if (transactionId != -1L) stringResource(R.string.title_edit_transaction) else stringResource(R.string.title_add_transaction)
                            }
                            else -> stringResource(R.string.app_name)
                        }
                        TopAppBar(
                            title = { Text(title) },
                            navigationIcon = {
                                if (currentRoute?.startsWith("add_transaction") == true || currentRoute == Screen.Budget.route) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        if (currentRoute?.startsWith("add_transaction") != true && currentRoute != Screen.Budget.route) {
                            BottomNavBar(navController = navController)
                        }
                    },
                    floatingActionButton = {
                        if (currentRoute == Screen.Home.route || currentRoute == Screen.Transactions.route) {
                            FloatingActionButton(onClick = {
                                navController.navigate(Screen.AddTransaction.createRoute())
                            }) {
                                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.title_add_transaction))
                            }
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                viewModel = transactionViewModel,
                                budgetViewModel = budgetViewModel,
                                currencyCode = userPrefs.currencyCode,
                                userName = userPrefs.userName,
                                onViewBudget = { navController.navigate(Screen.Budget.route) }
                            )
                        }
                        composable(Screen.Transactions.route) {
                            TransactionsScreen(
                                viewModel = transactionViewModel,
                                currencyCode = userPrefs.currencyCode,
                                onEditTransaction = { id ->
                                    navController.navigate(Screen.AddTransaction.createRoute(id))
                                }
                            )
                        }
                        composable(Screen.Reports.route) {
                            ReportsScreen(
                                reportsViewModel = reportsViewModel,
                                settingsViewModel = settingsViewModel,
                                exportViewModel = exportViewModel
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                currentPrefs = userPrefs,
                                onShowSnackbar = { message ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message)
                                    }
                                }
                            )
                        }
                        composable(Screen.Budget.route) {
                            BudgetScreen(
                                viewModel = budgetViewModel,
                                currencyCode = userPrefs.currencyCode
                            )
                        }
                        composable(
                            route = Screen.AddTransaction.route,
                            arguments = listOf(navArgument("transactionId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            })
                        ) { backStackEntry ->
                            val transactionId = backStackEntry.arguments?.getLong("transactionId")
                            AddTransactionScreen(
                                viewModel = transactionViewModel,
                                transactionId = transactionId,
                                currencyCode = userPrefs.currencyCode,
                                onBack = { navController.popBackStack() },
                                onSaveSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Transaction saved successfully!")
                                    }
                                    if (!navController.popBackStack(Screen.Transactions.route, inclusive = false)) {
                                        navController.navigate(Screen.Transactions.route) {
                                            popUpTo(Screen.Home.route)
                                            launchSingleTop = true
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
