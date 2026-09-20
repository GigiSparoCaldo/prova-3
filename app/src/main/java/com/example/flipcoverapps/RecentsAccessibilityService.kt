package com.example.flipcoverapps

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Servizio "muto": non elabora eventi, esiste solo per poter chiamare
 * performGlobalAction(GLOBAL_ACTION_RECENTS), l'azione che il sistema usa
 * quando premi il tasto/gesto "App recenti" — apre la vera schermata
 * panoramica di Android, con le anteprime live, non una ricostruzione.
 */
class RecentsAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Non ci serve reagire agli eventi.
    }

    override fun onInterrupt() {
        // Nessuna azione necessaria.
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) instance = null
    }

    companion object {
        private var instance: RecentsAccessibilityService? = null

        /** true se il servizio è attivo e pronto a ricevere il comando */
        fun isRunning(): Boolean = instance != null

        /** Apre la panoramica di sistema delle app recenti. Ritorna false se il servizio non è attivo. */
        fun openSystemRecents(): Boolean {
            val service = instance ?: return false
            return service.performGlobalAction(GLOBAL_ACTION_RECENTS)
        }
    }
}
