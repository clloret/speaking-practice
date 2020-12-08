package com.clloret.speakingpractice

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.AppDatabase_Migration_5_6
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DbMigrationTest {
    companion object {
        private const val TEST_DB = "migration-test"
    }

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate5To6() {
        helper.createDatabase(TEST_DB, 5).close()

        val db6 = helper.runMigrationsAndValidate(
            TEST_DB, 6, true,
            AppDatabase_Migration_5_6
        )

        helper.closeWhenFinished(db6)
    }
}
