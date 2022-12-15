package com.zzg.mybatis.generator.global;

import com.zzg.mybatis.generator.bridge.MekMybatisGeneratorBridge;
import com.zzg.mybatis.generator.model.GeneratorConfig;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * @author: mek
 * Date: 2022\12\15 0015
 * Time: 12:15
 * vx: 250023777
 * Description: 描述
 * @version: 1.0
 */
public class GlobalObj {
    public static TopLevelClass topLevelClass;
    public static MekMybatisGeneratorBridge bridge;
    public static GeneratorConfig generatorConfig;
    public static CompilationUnit compilationUnit;
}
