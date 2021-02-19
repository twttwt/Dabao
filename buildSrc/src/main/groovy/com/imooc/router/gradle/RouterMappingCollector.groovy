package com.imooc.router.gradle

import java.util.jar.JarEntry
import java.util.jar.JarFile

class RouterMappingCollector {

    private static final String PACKAGE_NAME = 'com\\twt\\dabao\\mapping'
    private static final String CLASS_NAME_PREFIX = 'RouterMapping_'
    private static final String CLASS_FILE_SUFFIX = '.class'
    private final Set<String> mappingClassNames = new HashSet<>()

    Set<String> getMappingClassNames() {
        return mappingClassNames
    }
/**
 * 收集Class文件或者Class文件目录中的映射表类
 * @param classFile
 */
    void collect(File classFile) {
        if (classFile == null || !classFile.exists()) {
            return
        }

        if (classFile.isFile()) {
            if (classFile.absolutePath.contains(PACKAGE_NAME) && classFile.name.startsWith(CLASS_NAME_PREFIX) && classFile.name.endsWith(CLASS_FILE_SUFFIX)) {
                String className = classFile.name.replace(CLASS_FILE_SUFFIX, "")
                mappingClassNames.add(className)
            }
        } else {
            classFile.listFiles().each {
                collect(it)
            }
        }
    }
    /**
     * 收集jar包中的目标类
     * @param jarFile
     */
    void collectFromJarFile(File jarFile) {
        Enumeration enumeration = new JarFile(jarFile).entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()

            String entryName = jarEntry.getName()

            if (entryName.contains(PACKAGE_NAME) && entryName.contains(CLASS_FILE_SUFFIX) && entryName.contains(CLASS_NAME_PREFIX)) {
                String className = entryName.replace(PACKAGE_NAME, "").replace("/", "").replace(CLASS_FILE_SUFFIX, "")
                mappingClassNames.add(className)
            }
        }
    }
}