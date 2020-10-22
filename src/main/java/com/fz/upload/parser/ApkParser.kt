package com.fz.upload.parser

import com.fz.upload.models.AppInfoModel
import com.fz.upload.models.Parameter
import com.fz.upload.parser.exception.PackageParseException
import com.fz.upload.utils.ParseUtil
import com.fz.upload.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.dongliu.apk.parser.ApkFile
import net.dongliu.apk.parser.bean.AdaptiveIcon
import net.dongliu.apk.parser.bean.Icon
import net.dongliu.apk.parser.bean.IconFace
import org.apache.commons.lang3.StringUtils
import java.io.File

/**
 * android 解析Apk
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2020/1/16 15:20
 */
internal class ApkParser : IParser() {
    @Throws(PackageParseException::class)
    override suspend fun onParser(parameter: Parameter?): AppInfoModel {
        val androidParentPath = PARENT_FILE + File.separator
        val androidParentFile = File(classPathFile, androidParentPath)
        if (!androidParentFile.exists()) {
            androidParentFile.mkdirs()
        }
        if (parameter == null) {
            throw NullPointerException("参数为空。")
        }
        val appPath = parameter.file
        val app = AppInfoModel()
        if (StringUtils.isEmpty(app.buildType)) {
            app.buildType = "Develop"
        } else {
            app.buildType = parameter.buildType
        }
        val path = appPath.path
        if (Utils.isEmpty(path)) {
            throw NullPointerException("参数为空。")
        }
        app.platform = "Android"
        println("PluginRemote--- >$path")
       return ApkFile(File(path)).use {apkParser ->
            val apkMeta = apkParser.apkMeta
            val iconFaces = apkParser.allIcons
            val parseIcon = ParseIcon(iconFaces).invoke()
            val iconPath = parseIcon.iconPath
            val iconData = parseIcon.iconData
            if (iconPath != null) {
                println("PluginIcon--- >$iconPath")
            }
            println("PluginVersionCode--- >" + apkMeta.versionCode)
            println("PluginVersionName--- >" + apkMeta.versionName)
            app.name = apkMeta.name
            app.projectName = apkMeta.name
            app.bundleId = apkMeta.packageName
            app.versionCode = ParseUtil.toString(apkMeta.versionCode)
            app.versionName = apkMeta.versionName
            if (iconPath != null && iconPath.isNotEmpty()) {
                saveLauncherIcon(iconData, app)
            }
            if (appPath != null) {
                uploadFile(appPath, app)
            }
            if (StringUtils.isEmpty(app.buildType)) {
                app.buildType = "Develop"
            }
            return@use app
        }
//        return try {
//
//
//
//        } catch (e: Exception) {
//            throw PackageParseException("Unfortunately, an error has occurred while processing parse android app " +
//                    "file", e)
//        } finally {
//            try(apkParser?.close()){
//
//            }
//        }
    }

    private inner class ParseIcon(private val iconFaces: List<IconFace>?) {
        var iconPath: String? = null
            private set
        var iconData: ByteArray? = null
            private set

        operator fun invoke(): ParseIcon {
            if (iconFaces != null && iconFaces.isNotEmpty()) {
                var density = 0
                for (iconFace in iconFaces) {
                    val iconPathTm = iconFace.path
                    if (iconPathTm.endsWith(".png") || iconPathTm.endsWith(".PNG")
                            || iconPathTm.endsWith(".jpg") || iconPathTm.endsWith(".jpeg")) {
                        println("PluginIcon--- >" + iconFace.path)
                        if (iconFace is Icon) {
                            if (density < iconFace.density) {
                                density = iconFace.density
                                iconPath = iconFace.path
                                iconData = iconFace.data
                            }
                        } else if (iconFace is AdaptiveIcon) {
                            val foregroundIcon = iconFace.foreground
                            if (density < foregroundIcon.density) {
                                density = foregroundIcon.density
                                iconPath = foregroundIcon.path
                                iconData = foregroundIcon.data
                            }
                        }
                    }
                }
            }
            return this
        }

    }

    companion object {
        const val PARENT_FILE = "android"
    }
}