import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import org.w3c.dom.Node
import org.w3c.dom.NodeList

plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

val pomFile = rootProject.file("pom.xml")
val documentBuilderFactory = DocumentBuilderFactory.newInstance().apply {
    isNamespaceAware = true
}
val pomDocument = documentBuilderFactory.newDocumentBuilder().parse(pomFile)
val xpath = XPathFactory.newInstance().newXPath()

fun nodeText(node: Node, expression: String): String? {
    val value = xpath.evaluate(expression, node, XPathConstants.STRING) as String
    return value.trim().takeIf { it.isNotEmpty() }
}

fun nodeList(node: Node, expression: String): NodeList {
    return xpath.evaluate(expression, node, XPathConstants.NODESET) as NodeList
}

val pomGroupId = nodeText(pomDocument, "/*[local-name()='project']/*[local-name()='groupId']")
    ?: nodeText(pomDocument, "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='groupId']")
val pomArtifactId = nodeText(pomDocument, "/*[local-name()='project']/*[local-name()='artifactId']")
val pomVersion = nodeText(pomDocument, "/*[local-name()='project']/*[local-name()='version']")
    ?: nodeText(pomDocument, "/*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']")

group = pomGroupId ?: "org.apache.httpcomponents.client5"
version = pomVersion ?: "unspecified"

val pomProperties = mutableMapOf(
    "project.groupId" to group.toString(),
    "project.artifactId" to (pomArtifactId ?: rootProject.name),
    "project.version" to version.toString(),
)

val propertyNodes = nodeList(
    pomDocument,
    "/*[local-name()='project']/*[local-name()='properties']/*"
)
for (i in 0 until propertyNodes.length) {
    val property = propertyNodes.item(i)
    val key = property.localName ?: property.nodeName
    val value = property.textContent.trim()
    if (key.isNotBlank() && value.isNotBlank()) {
        pomProperties[key] = value
    }
}

extra["pom.brotli4j.version"] = pomProperties["brotli4j.version"]

fun resolvePomValue(rawValue: String): String {
    val pattern = "\\$\\{([^}]+)\\}".toRegex()
    var resolved = rawValue
    var guard = 0
    while (guard++ < 10) {
        val updated = pattern.replace(resolved) { match ->
            val key = match.groupValues[1]
            pomProperties[key] ?: match.value
        }
        if (updated == resolved) {
            break
        }
        resolved = updated
    }
    return resolved
}

val dependencyNodes = nodeList(
    pomDocument,
    "/*[local-name()='project']/*[local-name()='dependencyManagement']" +
        "/*[local-name()='dependencies']/*[local-name()='dependency']"
)

val bomImports = mutableListOf<String>()
val constraintCoordinates = mutableListOf<String>()
for (i in 0 until dependencyNodes.length) {
    val dependency = dependencyNodes.item(i)
    val groupId = nodeText(dependency, "./*[local-name()='groupId']")?.let(::resolvePomValue)
    val artifactId = nodeText(dependency, "./*[local-name()='artifactId']")?.let(::resolvePomValue)
    val versionValue = nodeText(dependency, "./*[local-name()='version']")?.let(::resolvePomValue)
    val scope = nodeText(dependency, "./*[local-name()='scope']")
    val type = nodeText(dependency, "./*[local-name()='type']")
    val classifier = nodeText(dependency, "./*[local-name()='classifier']")
    if (groupId == null || artifactId == null || versionValue == null) {
        continue
    }

    val coordinates = "$groupId:$artifactId:$versionValue"
    when {
        type == "pom" && scope == "import" -> bomImports.add(coordinates)
        !classifier.isNullOrBlank() -> constraintCoordinates.add("$coordinates:$classifier")
        else -> constraintCoordinates.add(coordinates)
    }
}

dependencies {
    bomImports.forEach { api(platform(it)) }
    constraints {
        constraintCoordinates.forEach { api(it) }
        api("com.aayushatharva.brotli4j:native-linux-x86_64:${pomProperties["brotli4j.version"]}")
        api("com.aayushatharva.brotli4j:native-linux-aarch64:${pomProperties["brotli4j.version"]}")
        api("com.aayushatharva.brotli4j:native-osx-x86_64:${pomProperties["brotli4j.version"]}")
        api("com.aayushatharva.brotli4j:native-osx-aarch64:${pomProperties["brotli4j.version"]}")
        api("com.aayushatharva.brotli4j:native-windows-x86_64:${pomProperties["brotli4j.version"]}")
        api("com.aayushatharva.brotli4j:native-windows-aarch64:${pomProperties["brotli4j.version"]}")
    }
}
