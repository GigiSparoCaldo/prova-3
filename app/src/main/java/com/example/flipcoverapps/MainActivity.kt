package com.example.flipcoverapps

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flipcoverapps.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RecentAppsAdapter

    private val refreshHandler = Handler(Looper.getMainLooper())
    private val refreshIntervalMs = 3000L

    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadRecentApps()
            refreshHandler.postDelayed(this, refreshIntervalMs)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecentAppsAdapter(emptyList()) { app ->
            launchApp(app.packageName)
        }
        binding.recyclerApps.layoutManager = LinearLayoutManager(this)
        binding.recyclerApps.adapter = adapter

        binding.btnGrantAccess.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        binding.btnOpenRecents.setOnClickListener {
            val opened = RecentsAccessibilityService.openSystemRecents()
            if (!opened) {
                Toast.makeText(this, R.string.enable_accessibility_hint, Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionBanner()
        refreshHandler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable)
    }

    private fun updatePermissionBanner() {
        binding.permissionBanner.visibility =
            if (hasUsageAccess()) android.view.View.GONE else android.view.View.VISIBLE
    }

    private fun hasUsageAccess(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun loadRecentApps() {
        if (!hasUsageAccess()) {
            updatePermissionBanner()
            return
        }

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = System.currentTimeMillis()
        val start = end - (1000L * 60 * 60 * 24) // ultime 24 ore

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, start, end
        ) ?: emptyList()

        val pm = packageManager

        val recentApps = stats
            .filter { it.lastTimeUsed > 0 && it.packageName != packageName }
            .sortedByDescending { it.lastTimeUsed }
            .distinctBy { it.packageName }
            .mapNotNull { usageStat ->
                val launchIntent = pm.getLaunchIntentForPackage(usageStat.packageName)
                if (launchIntent == null) {
                    null
                } else {
                    try {
                        val appInfo = pm.getApplicationInfo(usageStat.packageName, 0)
                        RecentApp(
                            packageName = usageStat.packageName,
                            label = pm.getApplicationLabel(appInfo).toString(),
                            icon = pm.getApplicationIcon(appInfo),
                            lastTimeUsed = usageStat.lastTimeUsed
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                }
            }
            .take(20)

        adapter.updateItems(recentApps)
        binding.emptyText.visibility =
            if (recentApps.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
