package cn.dioxide.gravity;

import cn.dioxide.gravity.config.ConfigFolderConfiguration;
import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
 * @author Dioxide.CN
 * @date 2024/2/17
 * @since 1.0
 */
@Component
public class GravityStarter extends BasePlugin {

    private final ConfigFolderConfiguration configFolderConfiguration;

    public GravityStarter(PluginContext pluginContext,
                          ConfigFolderConfiguration configFolderConfiguration) {
        super(pluginContext);
        this.configFolderConfiguration = configFolderConfiguration;
    }

    @Override
    public void start() {
        this.configFolderConfiguration.init(GravityStarter.class);
        System.out.println("Gravity Power 插件启动成功！");
    }

    @Override
    public void stop() {
        System.out.println("Gravity Power 插件停止！");
    }

    public ConfigFolderConfiguration getConfigContext() {
        return configFolderConfiguration;
    }

}
