package com.fz.upload.parser

import com.fz.upload.models.AppInfoModel
import com.fz.upload.models.Parameter

class OtherParser : IParser() {
    override suspend fun onParser(parameter: Parameter?): AppInfoModel? {
        val appPath = parameter!!.file
        val app = AppInfoModel()
        app.platform = "other"
        app.fileName = appPath.name
        app.filePath = appPath.absolutePath
        onlyUploadFile(appPath, app)
        return app
    }
}