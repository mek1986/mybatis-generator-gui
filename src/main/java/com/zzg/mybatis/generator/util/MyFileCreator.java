package com.zzg.mybatis.generator.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.zzg.mybatis.generator.bridge.MekMybatisGeneratorBridge;
import com.zzg.mybatis.generator.global.GlobalObj;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.plugins.DbRemarksCommentGenerator;
import freemarker.cache.FileTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;

import java.io.*;
import java.util.*;

/**
 * @author: mek
 * Date: 2022\12\14 0014
 * Time: 13:59
 * vx: 250023777
 * Description: 描述
 * @version: 1.0
 */
public class MyFileCreator {
    private Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);

    public MyFileCreator() throws IOException {
        setConfig();
    }

    private void setConfig() throws IOException {
        freemarkerCfg.setTemplateLoader(new FileTemplateLoader(new File(this.getClass().getResource("").getPath() + "tpl\\")));

//        cfg.setDirectoryForTemplateLoading(file);
        // Recommended settings for new projects:
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        freemarkerCfg.setLogTemplateExceptions(false);
    }

    public boolean createDaoFile() throws IOException {
        GeneratorConfig config = GlobalObj.generatorConfig;
        String xmlPath = config.getMappingXMLPackage().replace(".", "/");
        Template temp = freemarkerCfg.getTemplate("dao.java.tpl");
        String path = config.getProjectFolder() + "/" + config.getModelPackageTargetFolder() + "/" + xmlPath + "/";
        File dir = new File(path);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("create output dir error");
                return false;
            }
        }

        try (OutputStream os = new FileOutputStream(path + config.getMapperName() + ".java"); Writer out = new OutputStreamWriter(os);) {
            BeansWrapper wrapper = new BeansWrapperBuilder(new Version(Configuration.VERSION_2_3_23.toString())).build();
            TemplateHashModel staticModels = wrapper.getStaticModels();
            TemplateHashModel globalObj =
                    (TemplateHashModel) staticModels.get(GlobalObj.class.getName());

            JSONObject obj = new JSONObject();
            obj.put("globalObj", globalObj);
            obj.put("pack", config.getDaoPackage().replace(".dao",""));

            temp.process(obj, out);
        } catch (FileNotFoundException | MalformedTemplateNameException | TemplateException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean createEntityFile() throws IOException {
        GeneratorConfig config = GlobalObj.generatorConfig;
        String xmlPath = config.getModelPackage().replace(".", "/");
        Template temp = freemarkerCfg.getTemplate("entity.java.tpl");
        String path = config.getProjectFolder() + "/" + config.getModelPackageTargetFolder() + "/" + xmlPath + "/";
        File dir = new File(path);

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("create output dir error");
                return false;
            }
        }

        try (OutputStream os = new FileOutputStream(path + config.getDomainObjectName() + ".java"); Writer out = new OutputStreamWriter(os);) {
            BeansWrapper wrapper = new BeansWrapperBuilder(new Version(Configuration.VERSION_2_3_23.toString())).build();
            TemplateHashModel staticModels = wrapper.getStaticModels();
            TemplateHashModel globalObj =
                    (TemplateHashModel) staticModels.get(GlobalObj.class.getName());

            JSONObject obj = new JSONObject();
            obj.put("globalObj", globalObj);
            Map<String, Field> fieldMap = ((DbRemarksCommentGenerator) GlobalObj.bridge.getContext().getCommentGenerator()).getFieldMap();
            Map<String, String> types = new HashMap<>();
            fieldMap.forEach((k, v) -> {
                types.put(k, v.getType().getShortName());
            });

            List<GeneratedJavaFile> generatedJavaFiles = GlobalObj.bridge.getMyBatisGenerator().getGeneratedJavaFiles();
            GeneratedJavaFile file = null;
            for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
                if (Objects.equals(generatedJavaFile.getCompilationUnit().getType().getShortNameWithoutTypeArguments(), GlobalObj.generatorConfig.getDomainObjectName())) {
                    file = generatedJavaFile;
                    break;
                }
            }

            List<JSONObject> methodList = new ArrayList<>();
            List<Method> methods = GlobalObj.topLevelClass.getMethods();
            for (Method method : methods) {
                JSONObject m = new JSONObject();
                m.put("name", method.getName());
                m.put("ret", method.getReturnType() == null ? "void" : method.getReturnType().getShortNameWithoutTypeArguments());
                m.put("params", method.getParameters());
                m.put("doc", method.getJavaDocLines());
                m.put("ano", method.getAnnotations());
                m.put("body", method.getBodyLines());
                methodList.add(m);
            }

            obj.put("fieldMap", fieldMap);
            obj.put("types", types);
            obj.put("methodList", methodList);
            obj.put("pack", config.getDaoPackage().replace(".dao",""));

            temp.process(obj, out);
        } catch (FileNotFoundException | MalformedTemplateNameException | TemplateException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws IOException, TemplateException {

    }
}
