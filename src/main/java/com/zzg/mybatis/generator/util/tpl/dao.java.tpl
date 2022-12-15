package ${globalObj.generatorConfig.daoPackage};

import ${globalObj.generatorConfig.modelPackage}.${globalObj.generatorConfig.domainObjectName};
import ${pack}.orm.dbcall.SqlStatement;
import ${pack}.orm.entityquery.EntityWork;
import ${pack}.orm.entityquery.PagerModel;
import ${pack}.orm.entityquery.SearchUtil;
import ${pack}.util.DateUtil;
import ${pack}.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * create_time ${.now}
 */
public class ${globalObj.generatorConfig.mapperName} {
	/**
	 * 单实例
	 *
	 *
	 */
	public static class Singtonle {
		private static ${globalObj.generatorConfig.mapperName} instance = new ${globalObj.generatorConfig.mapperName}();
	}

	/**
	 * 实例操作类
	 *
	 * @return
	 */
	public static ${globalObj.generatorConfig.mapperName} instance() {
		return Singtonle.instance;
	}

	EntityWork uk = new EntityWork();

	/**
	 * 获取数据
	 *
	 * @param searchUtil
	 * @return
	 */
	public List<${globalObj.generatorConfig.domainObjectName}> getListByWhere(SearchUtil searchUtil) {
		return uk.getListByWhere(searchUtil, ${globalObj.generatorConfig.domainObjectName}.class);
	}

	/**
	 * 获取分页数据
	 *
	 * @param searchUtil
	 * @return
	 */
	public PagerModel<${globalObj.generatorConfig.domainObjectName}> getList(SearchUtil searchUtil) {
		return uk.getList(searchUtil, ${globalObj.generatorConfig.domainObjectName}.class);
	}

	/**
	 * 保存
	 * @param model
	 * @return
	 */
	public int save(${globalObj.generatorConfig.domainObjectName} model) {
		List<SqlStatement> sqls = new ArrayList<SqlStatement>();
		if (StringUtil.isNull(model.getId())) {
			model.setId(StringUtil.createUuid());
			model.setAddDate(DateUtil.getCurrentTime());
			sqls.add(uk.createInsert(model));
		} else {
			model.setUpdateDate(DateUtil.getCurrentTime());
			sqls.add(uk.createUpdate(model));
		}
		return uk.Exec(sqls);
	}

	/**
	 * 删除
	 * @param model
	 * @return
	 */
	public int delete(${globalObj.generatorConfig.domainObjectName} model) {
		if (model == null)
			return 0;
		List<SqlStatement> sqls = new ArrayList<SqlStatement>();
		if (StringUtil.isNull(model.getId())) {
			return 0;
		} else {
			model.setDeleteDate(DateUtil.getCurrentTime());
			sqls.add(uk.createUpdate(model));
		}
		return uk.Exec(sqls);
	}

	public ${globalObj.generatorConfig.domainObjectName} findById(String id) {
		if(StringUtil.isNull(id))
			return null;
		${globalObj.generatorConfig.domainObjectName} model = new ${globalObj.generatorConfig.domainObjectName}();
		model.setId(id);
		return uk.getModel(model, ${globalObj.generatorConfig.domainObjectName}.class);
	}
}