package com.clloret.speakingpractice

import android.content.Context
import com.danielstone.materialaboutlibrary.MaterialAboutFragment
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList


class AboutFragment : MaterialAboutFragment() {
    override fun getMaterialAboutList(activityContext: Context): MaterialAboutList {
        val card = MaterialAboutCard.Builder()
            .title("Author")
            .build()

        return MaterialAboutList.Builder()
            .addCard(card)
            .build() // This creates an empty screen, add cards with .addCard()
    }
}