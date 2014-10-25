package org.emberjs.codeInsight;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.Language;
import com.intellij.lang.javascript.completion.JSLookupUtilImpl;
import com.intellij.lang.javascript.psi.JSNamedElement;
import com.intellij.lang.javascript.psi.impl.JSReferenceExpressionImpl;
import com.intellij.lang.javascript.psi.resolve.VariantsProcessor;
import com.intellij.openapi.application.AccessToken;
import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.Consumer;
import org.emberjs.lang.EmberJSLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * @author Kristian Mandrup
 */
public class EmberJSCompletionContributor extends CompletionContributor {
    private static final int EMBER_VARIABLE_PRIORITY = VariantsProcessor.LookupPriority.LOCAL_SCOPE_MAX_PRIORITY;

    @Override
    public void fillCompletionVariants(@NotNull final CompletionParameters parameters, @NotNull final CompletionResultSet result) {
        if (!getElementLanguage(parameters).is(EmberJSLanguage.INSTANCE)) return;
        PsiReference ref = parameters.getPosition().getContainingFile().findReferenceAt(parameters.getOffset());

        if (ref instanceof JSReferenceExpressionImpl && ((JSReferenceExpressionImpl)ref).getQualifier() == null) {
            final PsiElement parent = ((JSReferenceExpressionImpl)ref).getParent();

            // if (addControllerVariants(result, ref, parent)) return;

            EmberJSProcessor.process(parameters.getPosition(), new Consumer<JSNamedElement>() {
                @Override
                public void consume(JSNamedElement element) {
                    result.consume(JSLookupUtilImpl.createPrioritizedLookupItem(element, element.getName(), EMBER_VARIABLE_PRIORITY, false, false));
                }
            });
        }
    }

//    private static boolean addControllerVariants(CompletionResultSet result, PsiReference ref, PsiElement parent) {
//        if (EmberJSAsExpression.isAsControllerRef(ref, parent)) {
//            for (String controller : EmberIndexUtil.getAllKeys(EmberControllerIndex.INDEX_ID, parent.getProject())) {
//                result.consume(JSLookupUtilImpl.createPrioritizedLookupItem(null, controller, EMBER_VARIABLE_PRIORITY, false, false));
//            }
//            return true;
//        }
//        return false;
//    }


    private static Language getElementLanguage(final CompletionParameters parameters) {
        final AccessToken l = ReadAction.start();
        try {
            return PsiUtilCore.getLanguageAtOffset(parameters.getPosition().getContainingFile(), parameters.getOffset());
        } finally {
            l.finish();
        }
    }
}
