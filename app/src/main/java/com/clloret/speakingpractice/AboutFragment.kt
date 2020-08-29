package com.clloret.speakingpractice

import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutFragment
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.danielstone.materialaboutlibrary.util.OpenSourceLicense
import java.util.*


class AboutFragment : MaterialAboutFragment() {
    companion object {
        private const val AUTHOR_EMAIL = "clloret.app+speaking-practice@gmail.com"
        private const val AUTHOR_NAME = "Carlos Lloret Playán"
        private const val GITHUB_URL = "https://github.com/clloret/speaking-practice"
    }

    override fun onResume() {
        super.onResume()

        trackScreen()
    }

    override fun getMaterialAboutList(activityContext: Context): MaterialAboutList {
        return MaterialAboutList.Builder()
            .addCard(buildApp(activityContext))
            .addCard(buildAuthor(activityContext))
            .addCard(buildLicense(activityContext))
            .build()
    }


    private fun buildApp(context: Context): MaterialAboutCard? {
        val titleItem: MaterialAboutTitleItem = ConvenienceBuilder
            .createAppTitleItem(context)
        val drawableInfo = ContextCompat.getDrawable(context, R.drawable.ic_info_24dp)
        val versionItem: MaterialAboutActionItem = ConvenienceBuilder.createVersionActionItem(
            context,
            drawableInfo, getString(R.string.about_version), false
        )
        val cardBuilder: MaterialAboutCard.Builder = MaterialAboutCard.Builder()
        return cardBuilder
            .addItem(titleItem)
            .addItem(versionItem)
            .build()
    }

    private fun buildAuthor(context: Context): MaterialAboutCard? {
        val nameItem: MaterialAboutActionItem = MaterialAboutActionItem.Builder()
            .text(AUTHOR_NAME)
            .subText(R.string.about_country)
            .icon(R.drawable.ic_person_24dp)
            .build()
        val drawableGithub =
            ContextCompat.getDrawable(context, R.drawable.ic_github_24dp)
        val githubItem: MaterialAboutActionItem = ConvenienceBuilder
            .createWebsiteActionItem(
                context,
                drawableGithub,
                getString(R.string.about_view_source_code),
                false,
                Uri.parse(GITHUB_URL)
            )
        val drawableEmail = ContextCompat.getDrawable(context, R.drawable.ic_email_24dp)
        val emailItem: MaterialAboutActionItem = ConvenienceBuilder
            .createEmailItem(
                context,
                drawableEmail,
                getString(R.string.about_send_email),
                true,
                AUTHOR_EMAIL,
                getString(R.string.about_my_question)
            )
        val cardBuilder: MaterialAboutCard.Builder = MaterialAboutCard.Builder()
        return cardBuilder
            .title(R.string.about_author)
            .addItem(nameItem)
            .addItem(githubItem)
            .addItem(emailItem)
            .build()
    }

    private fun buildLicense(context: Context): MaterialAboutCard? {
        val drawableCopyright =
            ContextCompat.getDrawable(context, R.drawable.ic_copyright_24dp)
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR].toString()
        return ConvenienceBuilder
            .createLicenseCard(
                context, drawableCopyright, getString(R.string.about_license), year,
                AUTHOR_NAME, OpenSourceLicense.GNU_GPL_3
            )
    }

}