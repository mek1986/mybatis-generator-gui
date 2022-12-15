package com.zzg.mybatis.generator.view;

import com.zzg.mybatis.generator.bridge.MekMybatisGeneratorBridge;
import com.zzg.mybatis.generator.global.GlobalObj;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import com.zzg.mybatis.generator.util.MyFileCreator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import org.mybatis.generator.api.ProgressCallback;

import java.io.IOException;

/**
 * Created by Owen on 6/21/16.
 */
public class MekUIProgressCallback extends Alert implements ProgressCallback {

    private StringProperty progressText = new SimpleStringProperty();
    private MekMybatisGeneratorBridge bridge;
    GeneratorConfig generatorConfig;

    public MekUIProgressCallback(AlertType alertType, MekMybatisGeneratorBridge bridge,GeneratorConfig generatorConfig) {
        super(alertType);
        this.contentTextProperty().bindBidirectional(progressText);
        this.bridge = bridge;
        this.generatorConfig = generatorConfig;
        GlobalObj.generatorConfig =generatorConfig;
        GlobalObj.bridge = bridge;
    }

    @Override
    public void introspectionStarted(int totalTasks) {
        progressText.setValue("开始代码检查");
    }

    @Override
    public void generationStarted(int totalTasks) {
        progressText.setValue("开始代码生成");
    }

    @Override
    public void saveStarted(int totalTasks) {
        progressText.setValue("开始保存生成的文件");
    }

    @Override
    public void startTask(String taskName) {
        progressText.setValue("代码生成任务开始");
    }

    @Override
    public void done() {
        progressText.setValue("代码生成完成");
        try {
            new MyFileCreator().createDaoFile();
            new MyFileCreator().createEntityFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("finish");
    }

    @Override
    public void checkCancel() throws InterruptedException {
    }
}
