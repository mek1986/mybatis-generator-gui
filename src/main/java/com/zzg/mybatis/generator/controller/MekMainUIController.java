package com.zzg.mybatis.generator.controller;

import com.jcraft.jsch.Session;
import com.zzg.mybatis.generator.bridge.MybatisGeneratorBridge;
import com.zzg.mybatis.generator.model.DatabaseConfig;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.model.UITableColumnVO;
import com.zzg.mybatis.generator.util.ConfigHelper;
import com.zzg.mybatis.generator.util.DbUtil;
import com.zzg.mybatis.generator.util.MyStringUtils;
import com.zzg.mybatis.generator.view.AlertUtil;
import com.zzg.mybatis.generator.view.UIProgressCallback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.IgnoredColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MekMainUIController extends MainUIController {

    private static final Logger _LOG = LoggerFactory.getLogger(MekMainUIController.class);
    private static final String FOLDER_NO_EXIST = "部分目录不存在，是否创建";
    // tool bar buttons
    @FXML
    private Label connectionLabel;
    @FXML
    private Label configsLabel;
    @FXML
    private TextField modelTargetPackage;
    @FXML
    private TextField mapperTargetPackage;
    @FXML
    private TextField daoTargetPackage;
    @FXML
    private TextField tableNameField;
    @FXML
    private TextField domainObjectNameField;
    @FXML
    private TextField generateKeysField;    //主键ID
    @FXML
    private TextField modelTargetProject;
    @FXML
    private TextField mappingTargetProject;
    @FXML
    private TextField daoTargetProject;
    @FXML
    private TextField mapperName;
    @FXML
    private TextField projectFolderField;
    @FXML
    private CheckBox offsetLimitCheckBox;
    @FXML
    private CheckBox commentCheckBox;
    @FXML
    private CheckBox overrideXML;
    @FXML
    private CheckBox needToStringHashcodeEquals;
    @FXML
    private CheckBox useLombokPlugin;
    @FXML
    private CheckBox forUpdateCheckBox;
    @FXML
    private CheckBox annotationDAOCheckBox;
    @FXML
    private CheckBox useTableNameAliasCheckbox;
    @FXML
    private CheckBox annotationCheckBox;
    @FXML
    private CheckBox useActualColumnNamesCheckbox;
    @FXML
    private CheckBox useExample;
    @FXML
    private CheckBox useDAOExtendStyle;
    @FXML
    private CheckBox useSchemaPrefix;
    @FXML
    private CheckBox jsr310Support;
    @FXML
    private TreeView<String> leftDBTree;
    @FXML
    public TextField filterTreeBox;
    // Current selected databaseConfig
    private DatabaseConfig selectedDatabaseConfig;
    // Current selected tableName
    private String tableName;

    private List<IgnoredColumn> ignoredColumns;

    private List<ColumnOverride> columnOverrides;

    @FXML
    private ChoiceBox<String> encodingChoice;

    private void displayTables(TreeItem<String> treeItem) {
        if (treeItem == null) {
            return;
        }
        if (!treeItem.isExpanded()) {
            return;
        }
        DatabaseConfig selectedConfig = (DatabaseConfig) treeItem.getGraphic().getUserData();
        try {
            String filter = filterTreeBox.getText();
            List<String> tables = DbUtil.getTableNames(selectedConfig, filter);
            if (tables.size() > 0) {
                ObservableList<TreeItem<String>> children = treeItem.getChildren();
                children.clear();
                for (String tableName : tables) {
                    TreeItem<String> newTreeItem = new TreeItem<>();
                    ImageView imageView = new ImageView("icons/table.png");
                    imageView.setFitHeight(16);
                    imageView.setFitWidth(16);
                    newTreeItem.setGraphic(imageView);
                    newTreeItem.setValue(tableName);
                    children.add(newTreeItem);
                }
            } else if (StringUtils.isNotBlank(filter)) {
                treeItem.getChildren().clear();
            }
            if (StringUtils.isNotBlank(filter)) {
                ImageView imageView = new ImageView("icons/filter.png");
                imageView.setFitHeight(16);
                imageView.setFitWidth(16);
                imageView.setUserData(treeItem.getGraphic().getUserData());
                treeItem.setGraphic(imageView);
            } else {
                ImageView dbImage = new ImageView("icons/computer.png");
                dbImage.setFitHeight(16);
                dbImage.setFitWidth(16);
                dbImage.setUserData(treeItem.getGraphic().getUserData());
                treeItem.setGraphic(dbImage);
            }
        } catch (SQLRecoverableException e) {
            _LOG.error(e.getMessage(), e);
            AlertUtil.showErrorAlert("连接超时");
        } catch (Exception e) {
            _LOG.error(e.getMessage(), e);
            AlertUtil.showErrorAlert(e.getMessage());
        }
    }

    private void setTooltip() {
        encodingChoice.setTooltip(new Tooltip("生成文件的编码，必选"));
        generateKeysField.setTooltip(new Tooltip("insert时可以返回主键ID"));
        offsetLimitCheckBox.setTooltip(new Tooltip("是否要生成分页查询代码"));
        commentCheckBox.setTooltip(new Tooltip("使用数据库的列注释作为实体类字段名的Java注释 "));
        useActualColumnNamesCheckbox.setTooltip(new Tooltip("是否使用数据库实际的列名作为实体类域的名称"));
        useTableNameAliasCheckbox.setTooltip(new Tooltip("在Mapper XML文件中表名使用别名，并且列全部使用as查询"));
        overrideXML.setTooltip(new Tooltip("重新生成时把原XML文件覆盖，否则是追加"));
        useDAOExtendStyle.setTooltip(new Tooltip("将通用接口方法放在公共接口中，DAO接口留空"));
        forUpdateCheckBox.setTooltip(new Tooltip("在Select语句中增加for update后缀"));
        useLombokPlugin.setTooltip(new Tooltip("实体类使用Lombok @Data简化代码"));
    }

    void loadLeftDBTree() {
        TreeItem rootTreeItem = leftDBTree.getRoot();
        rootTreeItem.getChildren().clear();
        try {
            List<DatabaseConfig> dbConfigs = ConfigHelper.loadDatabaseConfig();
            for (DatabaseConfig dbConfig : dbConfigs) {
                TreeItem<String> treeItem = new TreeItem<>();
                treeItem.setValue(dbConfig.getName());
                ImageView dbImage = new ImageView("icons/computer.png");
                dbImage.setFitHeight(16);
                dbImage.setFitWidth(16);
                dbImage.setUserData(dbConfig);
                treeItem.setGraphic(dbImage);
                rootTreeItem.getChildren().add(treeItem);
            }
        } catch (Exception e) {
            _LOG.error("connect db failed, reason", e);
            AlertUtil.showErrorAlert(e.getMessage() + "\n" + ExceptionUtils.getStackTrace(e));
        }
    }

    private String validateConfig() {
        String projectFolder = projectFolderField.getText();
        if (StringUtils.isEmpty(projectFolder)) {
            return "项目目录不能为空";
        }
        if (StringUtils.isEmpty(domainObjectNameField.getText())) {
            return "类名不能为空";
        }
        if (StringUtils.isAnyEmpty(modelTargetPackage.getText(), mapperTargetPackage.getText(), daoTargetPackage.getText())) {
            return "包名不能为空";
        }

        return null;
    }

    /**
     * 检查并创建不存在的文件夹
     *
     * @return
     */
    private boolean checkDirs(GeneratorConfig config) {
        List<String> dirs = new ArrayList<>();
        dirs.add(config.getProjectFolder());
        dirs.add(config.getProjectFolder().concat("/").concat(config.getModelPackageTargetFolder()));
        dirs.add(config.getProjectFolder().concat("/").concat(config.getDaoTargetFolder()));
        dirs.add(config.getProjectFolder().concat("/").concat(config.getMappingXMLTargetFolder()));
        boolean haveNotExistFolder = false;
        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists()) {
                haveNotExistFolder = true;
            }
        }
        if (haveNotExistFolder) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText(FOLDER_NO_EXIST);
            Optional<ButtonType> optional = alert.showAndWait();
            if (optional.isPresent()) {
                if (ButtonType.OK == optional.get()) {
                    try {
                        for (String dir : dirs) {
                            FileUtils.forceMkdir(new File(dir));
                        }
                        return true;
                    } catch (Exception e) {
                        AlertUtil.showErrorAlert("创建目录失败，请检查目录是否是文件而非目录");
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public GeneratorConfig getGeneratorConfigFromUI() {
        GeneratorConfig generatorConfig = new GeneratorConfig();
        generatorConfig.setProjectFolder(projectFolderField.getText());
        generatorConfig.setModelPackage(modelTargetPackage.getText());
        generatorConfig.setGenerateKeys(generateKeysField.getText());
        generatorConfig.setModelPackageTargetFolder(modelTargetProject.getText());
        generatorConfig.setDaoPackage(daoTargetPackage.getText());
        generatorConfig.setDaoTargetFolder(daoTargetProject.getText());
        generatorConfig.setMapperName(mapperName.getText());
        generatorConfig.setMappingXMLPackage(mapperTargetPackage.getText());
        generatorConfig.setMappingXMLTargetFolder(mappingTargetProject.getText());
        generatorConfig.setTableName(tableNameField.getText());
        generatorConfig.setDomainObjectName(domainObjectNameField.getText());
        generatorConfig.setOffsetLimit(offsetLimitCheckBox.isSelected());
        generatorConfig.setComment(commentCheckBox.isSelected());
        generatorConfig.setOverrideXML(overrideXML.isSelected());
        generatorConfig.setNeedToStringHashcodeEquals(needToStringHashcodeEquals.isSelected());
        generatorConfig.setUseLombokPlugin(useLombokPlugin.isSelected());
        generatorConfig.setUseTableNameAlias(useTableNameAliasCheckbox.isSelected());
        generatorConfig.setNeedForUpdate(forUpdateCheckBox.isSelected());
        generatorConfig.setAnnotationDAO(annotationDAOCheckBox.isSelected());
        generatorConfig.setAnnotation(true);
        generatorConfig.setUseActualColumnNames(useActualColumnNamesCheckbox.isSelected());
        generatorConfig.setEncoding(encodingChoice.getValue());
        generatorConfig.setUseExample(useExample.isSelected());
        generatorConfig.setUseDAOExtendStyle(useDAOExtendStyle.isSelected());
        generatorConfig.setUseSchemaPrefix(useSchemaPrefix.isSelected());
        generatorConfig.setJsr310Support(jsr310Support.isSelected());
        return generatorConfig;
    }
}
