/*
 *  Copyright 2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.zzg.mybatis.generator.plugins;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.zzg.mybatis.generator.global.GlobalObj;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * 此插件使用数据库表中列的注释来生成Java Model中属性的注释
 *
 * @author Owen Zou
 */
public class DbRemarksCommentGenerator implements CommentGenerator {
    private Map<String, Field> fieldMap = new HashMap<>();
    private Map<String, Method> methodMap = new HashMap<>();
    private TopLevelClass topLevelClass;
    private CompilationUnit compilationUnit;

    private Properties properties;
    private boolean columnRemarks;
    private boolean isAnnotations;

    public DbRemarksCommentGenerator() {
        super();
        properties = new Properties();
    }


    public void addJavaFileComment(CompilationUnit compilationUnit) {
        // add no file level comments by default
        if (isAnnotations) {
            this.compilationUnit = compilationUnit;
            GlobalObj.compilationUnit = compilationUnit;
//            compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Table"));
//            compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Id"));
//            compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.Column"));
//            compilationUnit.addImportedType(new FullyQualifiedJavaType("javax.persistence.GeneratedValue"));
//            compilationUnit.addImportedType(new FullyQualifiedJavaType("org.hibernate.validator.constraints.NotEmpty"));
//            String packageName = topLevelClass.getType().getPackageName();
        }
    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and
     * when it was generated.
     */
    public void addComment(XmlElement xmlElement) {
    }

    public void addRootComment(XmlElement rootElement) {
        // add no document level comments by default
        return;
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {
        methodMap.put(method.getName(), method);
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {
        methodMap.put(method.getName(), method);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> set) {

    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> set) {
    }

    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);
        columnRemarks = isTrue(properties
                .getProperty("columnRemarks"));
        isAnnotations = isTrue(properties
                .getProperty("annotations"));
    }

    public void addClassComment(InnerClass innerClass,
                                IntrospectedTable introspectedTable) {
    }

    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    public Map<String, Method> getMethodMap() {
        return methodMap;
    }

    public TopLevelClass getTopLevelClass() {
        return topLevelClass;
    }

    public void addModelClassComment(TopLevelClass topLevelClass,
                                     IntrospectedTable introspectedTable) {
        String remarks = introspectedTable.getRemarks();
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * @author ");
        topLevelClass.addJavaDocLine(" * " + (Strings.isNullOrEmpty(remarks) ? "无" : remarks));
        topLevelClass.addJavaDocLine(" */");

        this.topLevelClass = topLevelClass;
        GlobalObj.topLevelClass = topLevelClass;
        String packageName = topLevelClass.getType().getPackageName();
        GlobalObj.compilationUnit.addImportedType(new FullyQualifiedJavaType(packageName + ".orm.annotations.SqlColumn"));
        GlobalObj.compilationUnit.addImportedType(new FullyQualifiedJavaType(packageName + ".orm.annotations.SqlTable"));

        if (isAnnotations) {
            topLevelClass.addAnnotation("@SqlTable(name=\"" + introspectedTable.getFullyQualifiedTableNameAtRuntime() + "\",description=\"" + (Strings.isNullOrEmpty(remarks) ? "无" : remarks) + "\")");
            topLevelClass.setSuperClass(topLevelClass.getType().getPackageName() + ".IModel");
            introspectedTable.getPrimaryKeyColumns().remove(0);
            List<IntrospectedColumn> baseColumns = introspectedTable.getBaseColumns();
            Set<String> blackSet = new HashSet<>();
            blackSet.add("adddate");
            blackSet.add("adduser");
            blackSet.add("deletedate");
            blackSet.add("deleteuser");
            blackSet.add("departmentcode");
            blackSet.add("isadmin");
            blackSet.add("isdelete");
            blackSet.add("manageorgs");
            blackSet.add("onlyid");
            blackSet.add("updatedate");
            blackSet.add("updateuser");
            blackSet.add("usertype");

            List<IntrospectedColumn> removeColumns = new ArrayList<>();

            for (IntrospectedColumn baseColumn : baseColumns) {
                if (blackSet.contains(baseColumn.getJavaProperty().toLowerCase())) {
                    removeColumns.add(baseColumn);
                }
            }

            for (IntrospectedColumn item : removeColumns) {
                introspectedTable.getBaseColumns().remove(item);
            }
        }
    }

    public void addEnumComment(InnerEnum innerEnum,
                               IntrospectedTable introspectedTable) {
    }

    public void addFieldComment(Field field,
                                IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();

        field.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append(Strings.isNullOrEmpty(remarks) ? "无" : remarks);
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");

        fieldMap.put(field.getName(), field);


        if (isAnnotations) {
            boolean isId = false;
            field.addAnnotation("@SqlColumn(name = \"" + field.getName() + "\", description = \"" + introspectedColumn.getRemarks() + "\")");
            field.setVisibility(JavaVisibility.PUBLIC);
            if (introspectedTable.getPrimaryKeyColumns().size() > 0) {
                for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
                    if (introspectedColumn == column) {
                        isId = true;
//                        field.addAnnotation("@Id");
//                        field.addAnnotation("@GeneratedValue");
                        break;
                    }
                }
            }

            if (!introspectedColumn.isNullable() && !isId) {
//                field.addAnnotation("@NotEmpty");
            }
            if (introspectedColumn.isIdentity()) {
                if (introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement().equals("JDBC")) {
                    field.addAnnotation("@GeneratedValue(generator = \"JDBC\")");
                } else {
                    field.addAnnotation("@GeneratedValue(strategy = GenerationType.IDENTITY)");
                }
            } else if (introspectedColumn.isSequenceColumn()) {
                field.addAnnotation("@SequenceGenerator(name=\"\",sequenceName=\"" + introspectedTable.getTableConfiguration().getGeneratedKey().getRuntimeSqlStatement() + "\")");
            }
        }
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    }

    public void addGeneralMethodComment(Method method,
                                        IntrospectedTable introspectedTable) {
    }

    public void addGetterComment(Method method,
                                 IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
    }

    public void addSetterComment(Method method,
                                 IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
    }

    public void addClassComment(InnerClass innerClass,
                                IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        innerClass.addJavaDocLine("/**"); //$NON-NLS-1$
        innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }
}
