package org.emberjs.refactoring;

import com.intellij.lang.javascript.index.JSNamedElementProxy;
import com.intellij.lang.javascript.refactoring.JSDefaultRenameProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenameUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.usageView.UsageViewTypeLocation;
import com.intellij.util.IncorrectOperationException;
import org.emberjs.codeInsight.ComponentUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dennis.Ushakov
 */
public class EmberJSComponentRenameProcessor extends JSDefaultRenameProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return ComponentUtil.getComponent(element) != null;
    }

    @Nullable
    @Override
    public PsiElement substituteElementToRename(PsiElement element, @Nullable Editor editor) {
        return ComponentUtil.getComponent(element);
    }

    @Override
    public void renameElement(PsiElement element, String newName, UsageInfo[] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        final String attributeName = ComponentUtil.getAttributeName(newName);
        for (UsageInfo usage : usages) {
            RenameUtil.rename(usage, attributeName);
        }

        ((PsiNamedElement)element).setName(newName);
        if (listener != null) {
            listener.elementRenamed(element);
        }
    }

    @Override
    public RenameDialog createRenameDialog(Project project, final PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
        final String directiveName = ComponentUtil.attributeToComponent(((PsiNamedElement)element).getName());
        return new RenameDialog(project, element, nameSuggestionContext, editor) {
            @Override
            public String[] getSuggestedNames() {
                return new String[] {directiveName};
            }
        };
    }

    public static class AngularJSComponentElementDescriptor implements ElementDescriptionProvider {
        @Nullable
        @Override
        public String getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
            JSNamedElementProxy directive = ComponentUtil.getComponent(element);
            if (directive != null) {
                if (location instanceof UsageViewTypeLocation) return "component";
                return ComponentUtil.attributeToComponent(directive.getName());
            }
            return null;
        }
    }
}
