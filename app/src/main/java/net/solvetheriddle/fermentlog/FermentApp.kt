package net.solvetheriddle.fermentlog

import android.app.Application
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.initialize

class FermentApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(context = this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
    }
}