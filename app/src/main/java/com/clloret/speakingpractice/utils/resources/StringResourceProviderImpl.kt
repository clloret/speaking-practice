package com.clloret.speakingpractice.utils.resources

import android.content.Context
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.resources.StringResourceProvider

class StringResourceProviderImpl(val context: Context) : StringResourceProvider {
    override fun getPrefCollectStatistics(): String {
        return context.getString(R.string.pref_collect_statistics)
    }
}