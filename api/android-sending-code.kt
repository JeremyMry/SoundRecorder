val file = File("path/to/file.wav")
val inputStream = FileInputStream(file)
val bytes = inputStream.readBytes()
val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

val jsonObject = JSONObject()
jsonObject.put("audio", base64)
val jsonString = jsonObject.toString()

val url = URL("http://example.com/upload")
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
if (responseCode == HttpURLConnection.HTTP_OK) {
    // Handle successful response
} else {
    // Handle error response
}

connection.disconnect()