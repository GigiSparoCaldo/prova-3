package com.example.flipcoverapps

import android.graphics.drawable.Drawable

data class RecentApp(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val lastTimeUsed: Long
)
