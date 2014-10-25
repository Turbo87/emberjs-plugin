package org.emberjs.codeInsight.refs;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dennis.Ushakov
 */
public class EmberJSTemplateReferencesProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        final FileReferenceSet fileReferenceSet = new FileReferenceSet(element) {
            @Override
            protected boolean isSoft() {
                return true;
            }
        };
        return ArrayUtil.mergeArrays(fileReferenceSet.getAllReferences(),
                new PsiReference[] {new EmberJSTemplateCacheReference((JSLiteralExpression)element)});
    }
}
