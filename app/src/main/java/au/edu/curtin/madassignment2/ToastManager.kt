package au.edu.curtin.madassignment2

import android.content.Context
import android.widget.Toast

object ToastManager {
    fun showOnlineToast(context: Context) {
        Toast.makeText(
            context,
            "Back online - Search enabled",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun showOfflineToast(context: Context) {
        Toast.makeText(
            context,
            "No internet connection - Showing saved books only",
            Toast.LENGTH_LONG
        ).show()
    }

    fun showBookAdded(context: Context, title: String) {
        Toast.makeText(
            context,
            "Added: $title",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun showBookRemoved(context: Context, title: String) {
        Toast.makeText(
            context,
            "Removed: $title",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun showCoverRemoved(context: Context) {
        Toast.makeText(
            context,
            "Cover photo removed",
            Toast.LENGTH_SHORT
        ).show()
    }
}