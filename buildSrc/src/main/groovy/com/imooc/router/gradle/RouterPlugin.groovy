package com.imooc.router.gradle

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class RouterPlugin implements Plugin<Project> {
    //实现apply方法，注入插件的逻辑
    @Override
    void apply(Project project) {
        //注册Transform
        if(project.plugins.hasPlugin(AppPlugin)){
            AppExtension appExtension=project.extensions.getByType(AppExtension)
            Transform transform=new RouterMappingTransform()
            appExtension.registerTransform(transform)
        }
        /**
         *  kapt {*         arguments {*             arg("root_project_dir", rootProject.projectDir.absolutePath)
         *}*}*/
        //1.自动帮助用户传递路径参数到注解处理器中
        if (project.extensions.findByName("kapt") != null) {
            project.extensions.findByName("kapt").arguments {
                arg("root_project_dir", project.rootProject.projectDir.absolutePath)
            }
        }
        //2.实现旧的构建产物的自动清理
        project.clean.doFirst {
            File routerMappingDir = new File(project.rootProject.projectDir, "router_mapping")
            if (routerMappingDir.exists()) {
                routerMappingDir.deleteDir()
            }
        }
        if(!project.plugins.hasPlugin(AppPlugin)){
            return
        }
        println("I am from RouterPlugin,apply from ${project.name}")
        project.getExtensions().create("router", RouterExtension)

        project.afterEvaluate {
            RouterExtension extension = project["router"]
            println("用户设置的wiki路径为：${extension.wikiDir}")
            //3.在javac任务后，生成对应的稳定
            project.tasks.findAll {
                task -> task.name.startsWith('compile') && task.name.endsWith('JavaWithJavac')
            }.each {
                task ->
                    task.doLast {
                        File routerMappingDir = new File(project.rootProject.projectDir, "router_mapping")
                        if (!routerMappingDir.exists()) {
                            return
                        }
                        File[] allChildFiles = routerMappingDir.listFiles()
                        if (allChildFiles.length < 1) {
                            return
                        }
                        StringBuilder sb=new StringBuilder()
                        sb.append("# 页面文档\n\n")

                        allChildFiles.each {
                            child->if(child.name.endsWith(".json")){
                                JsonSlurper jsonSlurper=new JsonSlurper()
                                def content=jsonSlurper.parse(child)

                                content.each {
                                    innerContent->
                                        def url=innerContent['url']
                                        def description=innerContent['description']
                                        def realPath=innerContent['realPath']

                                        sb.append(" ## $description\n")
                                        sb.append(" - $url\n")
                                        sb.append(" - $realPath\n")
                                }
                            }
                        }
                        File wikiFileDir=new File(extension.wikiDir)
                        if (!wikiFileDir.exists()){
                            wikiFileDir.mkdir()
                        }
                        File wikiFile=new File(wikiFileDir,"页面文档.md")
                        if (wikiFile.exists()){
                            wikiFile.delete()
                        }
                        wikiFile.write(sb.toString())
                    }
            }
        }
    }
}