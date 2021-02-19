package com.twt.router_processor;

import com.google.auto.service.AutoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.twt.router_annotations.Destination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import jdk.nashorn.internal.runtime.JSONListAdapter;

@AutoService(Processor.class)
public class DestinationProcessor extends AbstractProcessor {
    private static final String TAG = "DestinationProcessor";
    private Messager messager;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }
        String root_project_dir = processingEnv.getOptions().get("root_project_dir");
//        if (root_project_dir != null) {
//            throw new RuntimeException("rootDir=" + root_project_dir);
//        }
        System.out.println(TAG + " >>> process start...==========================="+root_project_dir);
        //获取到了所有标记了@Destination注解的类信息
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Destination.class);
        System.out.println(TAG + " >>> count..." + elementsAnnotatedWith.size());
        if (elementsAnnotatedWith.size() < 1) {
            return false;
        }
        String className = "RouterMapping_" + System.currentTimeMillis();

        StringBuilder builder = new StringBuilder();
        builder.append("package com.twt.dabao.mapping;\n\n");
        builder.append("import java.util.HashMap;\n");
        builder.append("import java.util.Map;\n");
        builder.append("public class ").append(className).append(" {\n\n");
        builder.append("public static Map<String, String> get() {\n\n");
        builder.append("    Map<String, String> mapping = new HashMap<>();\n");

        final JsonArray destionationJsonArray =new JsonArray();
        for (Element element : elementsAnnotatedWith) {

            final TypeElement typeElement = (TypeElement) element;

            final Destination destination = typeElement.getAnnotation(Destination.class);

            if (destination == null) {
                continue;
            }

            final String url = destination.url();
            final String description = destination.description();
            //全类名
            final String realPath = typeElement.getQualifiedName().toString();

            JsonObject jo=new JsonObject();
            jo.addProperty("url",url);
            jo.addProperty("description",description);
            jo.addProperty("realPath",realPath);
            System.out.println(TAG + " url:" + url);
            System.out.println(TAG + " description:" + description);
            System.out.println(TAG + " realPath:" + realPath);
            destionationJsonArray.add(jo);
            builder.append("    ").append("mapping.put(").append("\"" + url + "\"").append(", ").append("\"" + realPath + "\"").append(");\n");
        }
        builder.append("     return mapping;\n");
        builder.append("   }\n");
        builder.append("}\n");

        String mappingFullClassName = "com.twt.dabao.mapping." + className;
        System.out.println(TAG + " >>>mappingFullClassName: " + mappingFullClassName);
        System.out.println(TAG + " >>>builder: " + builder.toString());

        try {
            JavaFileObject source = processingEnv.getFiler().createSourceFile(mappingFullClassName);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error while create file", e);
        }

        //写入到JSON本地文件中
        File rootDirFile=new File(root_project_dir);
        if (!rootDirFile.exists()){
            throw new RuntimeException("root_project_dir not exist");
        }
        //创建子目录
        File routerFileDir=new File(rootDirFile,"router_mapping");
        if (!routerFileDir.exists()){
            routerFileDir.mkdir();
        }
        File mappingFile=new File(routerFileDir,"mapping_"+System.currentTimeMillis()+".json");
        try {
            BufferedWriter out=new BufferedWriter(new FileWriter(mappingFile));
            String json = destionationJsonArray.toString();
            out.write(json);
            out.flush();
            out.close();
        }catch (Throwable throwable){
            throw new RuntimeException("Error while write json: ",throwable);
        }
        System.out.println(TAG + "process finish");
        return false;
    }

    /**
     * 告诉编译器，当前处理器支持的注解类型
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Destination.class.getCanonicalName());
    }
}
