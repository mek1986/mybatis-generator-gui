package ${globalObj.generatorConfig.modelPackage};

import ${pack}.orm.annotations.SqlColumn;
import ${pack}.orm.annotations.SqlTable;

/**
 * ${globalObj.generatorConfig.tableName}
 * create_time ${.now}
 */
@SqlTable(name = "${globalObj.generatorConfig.tableName}", description = "${globalObj.generatorConfig.tableName}")
public class ${globalObj.generatorConfig.domainObjectName} extends IModel {
    public ${globalObj.generatorConfig.domainObjectName}() {

	}

	public ${globalObj.generatorConfig.domainObjectName}(String _BName) {
		this.bName = _BName;
		this.tableName = "${globalObj.generatorConfig.tableName}";
	}

	<#assign keys = fieldMap?keys>
	<#list keys as key>
    <#list fieldMap[key].javaDocLines as item>
    ${item}
    </#list>
    <#list fieldMap[key].annotations as item>
    ${item}
    </#list>
    public ${types[key]} ${key};

    public ${types[key]} f_${key}() {
        return getFiledName("${key}");
    }
	</#list>

	<#list methodList as m>
	public ${m.ret} ${m.name}(${m.params?join(", ")}){
		<#list m.body as bd>
		${bd}
		</#list>
	}
	</#list>
}