include(":materialcarrotwear")
include(":app")

rootProject.name = "Material Carrot"
pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
        maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
include(":materialcarrotrepository")
include(":materialcarrotutils")
