pluginManagement {

    /**
     * The pluginManagement.repositories block configures the
     * repositories Gradle uses to search or download the Gradle plugins and
     * their transitive dependencies. Gradle pre-configures support for remote
     * repositories such as JCenter, Maven Central, and Ivy. You can also use
     * local repositories or define your own remote repositories. The code below
     * defines the Gradle Plugin Portal, Google's Maven repository,
     * and the Maven Central Repository as the repositories Gradle should use to look for its
     * dependencies.
     *
     * pluginManagement.repositories 块配置了Gradle用于搜索或下载Gradle插件及其传递依赖项的仓库。
     * Gradle预先配置了对远程仓库（如JCenter、Maven Central和Ivy）的支持。 您还可以使用本地仓库或定义自己的远程仓库。
     * 下面的代码将Gradle插件门户，Google的Maven存储库和Maven中央存储库定义为Gradle应该用来查找其依赖项的仓库。
     *
     */

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {

    /**
     * The dependencyResolutionManagement.repositories
     * block is where you configure the repositories and dependencies used by
     * all modules in your project, such as libraries that you are using to
     * create your application. However, you should configure module-specific
     * dependencies in each module-level build.gradle.bak file. For new projects,
     * Android Studio includes Google's Maven repository and the Maven Central
     * Repository by default, but it does not configure any dependencies (unless
     * you select a template that requires some).
     *
     * dependencyResolutionManagement.repositories 块是您配置项目中所有模块使用的仓库和依赖项的地方，例如用于创建应用程序的库。
     * 但是，您应该在每个模块级别的 build.gradle.bak 文件中配置特定于模块的依赖项。
     * 对于新项目，Android Studio 默认包含 Google 的 Maven 仓库和 Maven Central 仓库，
     * 但它不会配置任何依赖项（除非选择需要某些依赖项的模板）。
     */

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "suclear"
include(":app")
