package com.example.networkmanager.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 *  Helper method to set visibility of a [View]
 */
fun View.show(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

/**
 *  Helper method to show snack bar with [message]
 */
fun View.showSnackBar(message: String) {
    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
}