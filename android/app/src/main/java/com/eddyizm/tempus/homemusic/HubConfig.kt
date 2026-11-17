package com.eddyizm.tempus.homemusic

import android.content.SharedPreferences
import com.eddyizm.tempus.App
import com.eddyizm.tempus.BuildConfig

/**
 * HomeMusic Hub settings. The Hub signs in with the same Navidrome account as the player, so the
 * only things to configure are its addresses: a public one (through the VPS gateway) and an
 * optional home one, preferred while it answers.
 */
object HubConfig {
    const val PREF_HUB_URL = "homemusic_hub_url"
    const val PREF_HUB_LOCAL_URL = "homemusic_hub_local_url"
    const val PREF_CHECK_UPDATE = "homemusic_check_update"
    const val PREF_GOOGLE = "homemusic_google"
    const val PREF_ADMIN = "homemusic_admin"
    const val PREF_LEAVE = "homemusic_leave"
    const val PREF_TRAFFIC_STATS = "homemusic_traffic_stats"

    /** Tempus's own «Выйти» in «Общие»: hidden, «Выйти» of the HomeMusic block takes its place. */
    const val PREF_TEMPUS_LOGOUT = "logout"
    /** Tempus's «Буфер предзагрузки»: hidden, the player buffers the playing track whole (`QueueLoading`). */
    const val PREF_TEMPUS_PRELOAD_BUFFER = "song_preload_buffer"
    const val PREF_CATEGORY = "homemusic_category"

    @JvmStatic
    fun isEnabled(): Boolean = BuildConfig.HOMEMUSIC

    private fun prefs(): SharedPreferences = App.getInstance().preferences

    @JvmStatic
    fun remoteUrl(): String =
        normalize(prefs().getString(PREF_HUB_URL, null)) ?: normalize(BuildConfig.HUB_URL) ?: ""

    /** Null when no home address is set. An emptied field means "none", unset means the default. */
    @JvmStatic
    fun localUrl(): String? {
        val stored = prefs().getString(PREF_HUB_LOCAL_URL, null)
        return normalize(stored ?: BuildConfig.HUB_LOCAL_URL)
    }

    @JvmStatic
    fun normalize(url: String?): String? {
        var u = url?.trim().orEmpty()
        if (u.isEmpty()) return null
        if (!u.startsWith("http://", true) && !u.startsWith("https://", true)) u = "https://$u"
        // The PWA lives at the root, so a pasted "/#/search" or trailing slash is dropped.
        u = u.substringBefore('#').trimEnd('/')
        return u
    }
}
