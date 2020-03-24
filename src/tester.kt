package ofirmgr.kotlin.xml.dom

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory


var bestTime = Double.MAX_VALUE
var bestName = ""
val isWindows = System.getProperty("os.name").toLowerCase().contains("win")
fun main() {
    var profileFolder:String = ""
    if(isWindows) {
        //TODO: add windows folder for xml files
    }
    else {
        profileFolder = "/opt/cisco/anyconnect/profile"
    }

    Files.walk(Paths.get(profileFolder)).use { paths ->
        paths
            .filter {
                Files.isRegularFile(it)
            }
            .filter { it.fileName?.toFile()?.extension == "xml" }
            .forEach {
                println("FILE: $it")
                val xlmFile = it.toFile()
                findFastestVPN(xlmFile)
            }
    }



    println("--------------------------")
    println("bestName: $bestName")
    println("bestTime: $bestTime")
}

private fun findFastestVPN(xlmFile: File) {
    val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xlmFile)
    xmlDoc.documentElement.normalize()

    val hostEntryList: NodeList = xmlDoc.getElementsByTagName("HostEntry")
    (0 until hostEntryList.length).forEach { i ->
        val hostEntryNode: Node = hostEntryList.item(i)
        val name = (hostEntryNode as Element).getElementsByTagName("HostName").item(0).textContent
        println("name: $name")
        if (name == "Certificate Check")
            return@forEach
        val host = hostEntryNode.getElementsByTagName("HostAddress").item(0)?.textContent
        println("host: $host")

        if (host != null) {
            val pingRes = ping(host)
            val pingTime = pingRes?.substringAfter("time=")?.substringBefore("ms")
            println("pingTime: $pingTime")
            if (pingTime?.toDoubleOrNull() == null)
                return@forEach
            if (pingTime.toDouble() < bestTime) {
                bestTime = pingTime.toDouble()
                bestName = name
            }
        }
    }
}


fun ping(host: String): String? {

    val processBuilder = ProcessBuilder("ping", if (isWindows) "-n" else "-c", "1", host)
    val proc = processBuilder.start()
    proc.waitFor(1000, TimeUnit.MILLISECONDS)

    return formatData(proc.inputStream)
}

fun formatData(inputStream: InputStream): String? {
    val reader = BufferedReader(InputStreamReader(inputStream))
    val sb = StringBuilder()
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        sb.append(line).append("\n")
    }
    return sb.toString()
}
