package com.fz.upload.parser

import com.fz.upload.http.HttpClient.Companion.httpClient
import com.fz.upload.models.AppInfoModel
import com.fz.upload.models.Parameter
import com.fz.upload.parser.exception.PluginException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.MessageFormat
import java.util.concurrent.CompletableFuture

/**
 * App 包解析器
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2020/1/16 15:21
 */
public abstract class IParser {
    @get:Throws(FileNotFoundException::class)
    protected val classPathFile: File
        protected get() {
            val path = File("").absolutePath;
            println("classpath:$path")
            var file = File(path)
            println("path:" + file.absolutePath)
            if (!file.exists()) file = File("")
            return file
        }

    /**
     * 创建临时文件
     *
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    protected fun createTempFile(data: ByteArray?, fileName: String): File {
        if (data == null || data.isEmpty()) {
            throw NullPointerException("No data available.")
        }
        if (StringUtils.isEmpty(fileName)) {
            throw NullPointerException("Don't know the file name.")
        }
        val path = classPathFile
        println("path:" + path.absolutePath)
        val tempFolder = "temp" + File.separator
        val parentFile = File(path.absolutePath, tempFolder)
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        println("parentFile:" + parentFile.absolutePath)
        val tempFile = File(parentFile, fileName)
        if (tempFile.exists()) {
            tempFile.delete()
        }
        FileUtils.writeByteArrayToFile(tempFile, data)
        return tempFile
    }

    /**
     * 上传launcher 图标
     */
    @Throws(IOException::class)
    protected fun saveLauncherIcon(iconData: ByteArray?, model: AppInfoModel) {
        val file = createTempFile(iconData, "ic_launcher.png")
        model.iconPath = file.absolutePath
    }

    /**
     * 上传app文件
     */
    @Throws(Exception::class)
    protected suspend fun uploadFile(file: File, model: AppInfoModel) {
        if (StringUtils.isEmpty(model.iconPath)) {
            throw NullPointerException("Not found launcher icon file path.")
        }
        model.fileSize = file.length()
        model.filePath = file.absolutePath
        model.filePathType = AppInfoModel.FileType.LOG_SERVICE
        httpClient.sendPostJson(model)
    }

    /**
     * App 包解析
     *
     * @param parameter
     * @author dingpeihua
     * @date 2020/1/16 16:00
     * @version 1.0
     */
    @Throws(PluginException::class, IOException::class)
    suspend abstract fun onParser(parameter: Parameter?): AppInfoModel?

    fun onParserProcess(parameter: Parameter): CompletableFuture<AppInfoModel?> =
            GlobalScope.future { onParser(parameter) }

    companion object {
        /**
         * 根据文件后缀创建解析器
         *
         * @param name
         * @return
         * @throws IllegalAccessException
         */
        @JvmStatic
        @Throws(IllegalAccessException::class)
        fun createParser(name: String?): IParser {
            if ("apk".equals(name, ignoreCase = true)) {
                return ApkParser()
            } else if ("ipa".equals(name, ignoreCase = true)) {
                return IpaParser()
            }
            throw IllegalAccessException(MessageFormat.format("The file type \"{0}\" Unsupported", name))
        }
    }
}