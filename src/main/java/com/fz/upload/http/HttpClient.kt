package com.fz.upload.http

import com.fz.upload.models.AppInfoModel
import com.fz.upload.parser.exception.PluginException
import com.fz.upload.parser.exception.UploadFileException
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.commons.lang3.StringUtils
import retrofit2.Callback
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class HttpClient {
    companion object {
        val httpClient = HttpClient()
    }

    @Throws(PluginException::class)
    suspend fun sendPostJson(appBean: AppInfoModel): String {
        return try {
            if (StringUtils.isEmpty(appBean.iconPath)) {
                throw UploadFileException("Icon file path is empty.")
            }
            if (StringUtils.isEmpty(appBean.filePath)) {
                throw UploadFileException("the app file path is empty.")
            }
            val requestParams = RequestParams()
                    .put("bundleId", appBean.bundleId)
                    .put("appName", appBean.name)
                    .put("versionName", appBean.versionName)
                    .put("versionCode", appBean.versionCode)
                    .put("buildType", appBean.buildType)
                    .put("platform", appBean.platform)
            val iconResponse = ApiManager.api().uploadFile(requestParams
                    .put("file", File(appBean.iconPath), "image/*")
                    .createFileRequestBody())
            val fileResponse = ApiManager.api().uploadFile(requestParams
                    .put("file", File(appBean.filePath), "application/octet-stream")
                    .createFileRequestBody())
            val iconHttpResponse = iconResponse.await();
            val fileHttpResponse = fileResponse.await();
            if (!iconHttpResponse.isSuccess) {
                throw RuntimeException("Upload launcher icon failure.")
            }
            appBean.iconPath = iconHttpResponse.data;
            print("iconPath:" + appBean.iconPath)
            if (!fileHttpResponse.isSuccess) {
                throw PluginException("Upload app file failure.")
            }
            appBean.filePath = fileHttpResponse.data
            appBean.fileName = fileHttpResponse.data
            appBean.downloadUrl = fileHttpResponse.data
            print("appFilePath:" + appBean.filePath)
            val appInfoResponse = ApiManager.api().uploadInfo(appBean)
            val appInfoHttpResponse = appInfoResponse.await();
            if (!appInfoHttpResponse.isSuccess) {
                throw PluginException("Upload app data failure.")
            }
            print("HttpResponse:$appInfoHttpResponse")
            return appInfoHttpResponse.data
        } catch (e: Exception) {
            throw PluginException(e)
        }
    }
    @Throws(PluginException::class)
    suspend fun onlyUploadFile(appBean: AppInfoModel): String {
        if (StringUtils.isEmpty(appBean.filePath)) {
            throw UploadFileException("the app file path is empty.")
        }
        val requestParams = RequestParams()
                .put("platform", appBean.platform)
        val fileResponse = ApiManager.api().onlyUploadFile(requestParams
                .put("file", File(appBean.filePath), "application/octet-stream")
                .createFileRequestBody())
        val fileHttpResponse = fileResponse.await()
        if (!fileHttpResponse.isSuccess) {
            throw PluginException("Upload app file failure.")
        }
        appBean.filePath = fileHttpResponse.data
        print("appFilePath:" + appBean.filePath)
        return fileHttpResponse.data
    }
}

suspend fun <T> retrofit2.Call<T>.await(): T {
    return suspendCancellableCoroutine {
        it.invokeOnCancellation { it1 ->
            println("request cancel")
            it1?.printStackTrace()
            cancel()
        }
        enqueue(object : Callback<T> {
            override fun onFailure(call: retrofit2.Call<T>, t: Throwable) {
                it.resumeWithException(t)
            }

            override fun onResponse(call: retrofit2.Call<T>, response: retrofit2.Response<T>) {
                if (response.isSuccessful) {
                    it.resume(response.body()!!)
                } else {
                    it.resumeWithException(Throwable(response.toString()))
                }
            }
        })
    }
}