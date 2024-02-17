package cn.dioxide.gravity.infra;

import cn.dioxide.gravity.util.DomBuilder;
import cn.dioxide.gravity.util.InferStream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;
import java.util.Map;

/**
 * @author Dioxide.CN
 * @date 2024/2/17
 * @since 1.0
 */
@Component
@AllArgsConstructor
public class HeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;

    private static final String TEMPLATE_ID_VARIABLE = "_templateId";

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
        IElementModelStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();
        // 全响应式链路
        return InferStream
            // 目录页 JS
            .<Void>infer(whichTemplate(context, "directory"))
            .success(() -> model.add(modelFactory.createText(
                DomBuilder.use()
                    .script("/native/pinyin-pro.min.js")
                    .script("/lib/DirectorySort.js")
                    .build())))
            .last();
    }

    public boolean whichTemplate(ITemplateContext context, String template) {
        return template.equals(context.getVariable(TEMPLATE_ID_VARIABLE));
    }

}
