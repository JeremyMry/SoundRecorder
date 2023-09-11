package com.kotlin.soundrecorder
import android.util.Base64.encodeToString
import android.util.Base64
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset


class SendAudioFile {

    fun sendAudioFile(f2: File): String {

        val inputStream = FileInputStream(f2)
        val bytes = inputStream.readBytes()
        val base64 = encodeToString(bytes, Base64.DEFAULT)

        val jsonObject = JSONObject()
        jsonObject.put("audio", base64)
        val jsonString = jsonObject.toString()

        val url = URL("http://10.0.2.2:8080/test")
        val connection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val outputStream = connection.outputStream

        outputStream.write(jsonString.toByteArray(Charset.defaultCharset()))
        outputStream.flush()
        outputStream.close()

        val responseCode = connection.responseCode
        return if (responseCode == HttpURLConnection.HTTP_OK) {

            /*val returnContent = BufferedReader(
                InputStreamReader(
                    connection.inputStream
                )
            )

            connection.disconnect()

            returnContent.toString()*/

            connection.disconnect()

            "ok"

        } else {

            connection.disconnect()

            "error"
        }

        /*
        val url = URL("")
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "POST"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        val out = OutputStreamWriter(connection.outputStream)
        out.write(f2.toString());
        out.close();
        val `in` = BufferedReader(
            InputStreamReader(
                connection.inputStream
            )
        )
        while (`in`.readLine() != null) {

            println(`in`)

        }
        `in`.close()
        */
    }
}