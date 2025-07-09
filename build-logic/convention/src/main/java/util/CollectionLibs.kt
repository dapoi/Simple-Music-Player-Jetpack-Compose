package util

import util.ConstantLibs.coreModules
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

object CollectionLibs {
    fun Project.composeDependencies() {
        dependencies {
            val bom = libs.androidx.compose.bom.get()
            implementation(platform(bom))
            androidTestImplementation(platform(bom))
            implementation(libs.androidx.activity.compose.get())
            implementation(libs.androidx.appcompat.get())
            implementation(libs.androidx.compose.material3.get())
            implementation(libs.androidx.compose.ui.tooling.preview.get())
            debugImplementation(libs.androidx.compose.ui.tooling.debug.get())
            implementation(libs.androidx.core.ktx.get())
            implementation(libs.coil.compose.get())
            implementation(libs.coil.network.get())
            implementation(libs.coil.video.get())
            implementation(libs.media3.exoplayer.get())
            implementation(libs.media3.ui.get())
            implementation(libs.media3.session.get())
            implementation(libs.timber.get())
        }
    }

    fun Project.dataDependencies() {
        dependencies {
            implementation(project(coreModules[1]))
            implementation(libs.dataStorePreferences.get())
            implementation(libs.okhttp.interceptor.get())
            implementation(libs.retrofit.lib.get())
            implementation(libs.retrofit.converter.get())
            implementation(libs.timber.get())
            debugImplementation(libs.chucker.debug.get())
            releaseImplementation(libs.chucker.release.get())
        }
    }
}