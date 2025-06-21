package net.solvetheriddle.fermentlog.di

import net.solvetheriddle.fermentlog.data.Db
import net.solvetheriddle.fermentlog.ui.screens.batches.ActiveBatchesViewModel
import net.solvetheriddle.fermentlog.ui.screens.batches.AddBatchViewModel
import net.solvetheriddle.fermentlog.ui.screens.settings.ingredients.IngredientsViewModel
import net.solvetheriddle.fermentlog.ui.screens.settings.vessels.VesselsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Db }
    viewModel { VesselsViewModel(get()) }
    viewModel { IngredientsViewModel(get()) }
    viewModel { AddBatchViewModel(get()) }
    viewModel { ActiveBatchesViewModel(get()) }
}

