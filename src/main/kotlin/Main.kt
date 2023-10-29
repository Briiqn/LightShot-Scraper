
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


fun main(args : Array<String>) {
println(generateLinks(100000))
}

fun generateLinks(count: Long): List<String> {
    val charSet = charArrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
        'r', 's', 't', 'u', 'x', 'w', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )
    val charCount = 6
        //limit to 2 to avoid ip ban
    val executor = Executors.newFixedThreadPool(2)
    val urls = mutableListOf<String>()
    for (index in 0 until count) {
        Thread.sleep((100..450).random().toLong())
        val randomChars = CharArray(charCount) { charSet.random() }
        val imageUrl = "https://prnt.sc/${randomChars.joinToString("")}"
        executor.execute {
            downloadImageFromUrl(imageUrl, "images/${String(randomChars)}.png")
            synchronized(urls) {
                urls.add(imageUrl)
            }
        }
    }
    executor.shutdown()
    while (!executor.isTerminated) {
        Thread.sleep(2500)
    }
    return urls.toList()
}




fun downloadImageFromUrl(imageUrl: String, savePath: String) {
    val charSet = charArrayOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
        'r', 's', 't', 'u', 'x', 'w', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    )
    val randomChars = CharArray(16) { charSet.random() }

    val connection = URL(imageUrl).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("User-Agent", String(randomChars))

    try{
    BufferedInputStream(connection.inputStream).use { inputStream ->
        val responseText = inputStream.bufferedReader().use { it.readText() }
        val imgurUrlRegex = Regex("""https?://i\.imgur\.com/\w+\.\w+""")
        imgurUrlRegex.find(responseText)?.value?.let { imgurUrl ->
            BufferedInputStream(URL(imgurUrl).openStream()).use { imgurInputStream ->
                FileOutputStream(File(savePath)).use { outputStream ->
                    if(imgurInputStream.readAllBytes().size!=503|| imgurInputStream.readAllBytes().isNotEmpty()) {
                        imgurInputStream.copyTo(outputStream)
                    }else{
                        println("Dead Image Url $imgurUrl")
                    }
                }
            }
        }
    }
    }catch (ex:IOException){
        println("something haram happen wait 11 seconds")
        Thread.sleep(11000)
    }
    connection.disconnect()
}
