package org.emberjs.index;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.intellij.xml.util.HtmlUtil;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Dennis.Ushakov
 */
public class EmberTemplateCacheIndex extends ScalarIndexExtension<String> {
    public static final ID<String, Void> TEMPLATE_CACHE_INDEX = ID.create("angularjs.template.cache");
    private final DataIndexer<String, Void, FileContent> myDataIndexer = new MyDataIndexer();

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return TEMPLATE_CACHE_INDEX;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return myDataIndexer;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return new EnumeratorStringDescriptor();
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new DefaultFileTypeSpecificInputFilter(StdFileTypes.HTML, StdFileTypes.XHTML) {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                return super.acceptInput(file) && !(file.getFileSystem() instanceof JarFileSystem);
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return EmberIndexUtil.BASE_VERSION;
    }

    private static class MyDataIndexer implements DataIndexer<String, Void, FileContent> {
        @NotNull
        @Override
        public Map<String, Void> map(@NotNull FileContent inputData) {
            final Map<String, Void> result = new THashMap<String, Void>();
            PsiFile psiFile = inputData.getPsiFile();
            processTemplates(psiFile, new Processor<XmlAttribute>() {
                @Override
                public boolean process(XmlAttribute attribute) {
                    result.put(attribute.getValue(), null);
                    return true;
                }
            });

            return result;
        }
    }

    public static void processTemplates(final PsiFile psiFile, final Processor<XmlAttribute> processor) {
        if (psiFile instanceof XmlFile) {
            psiFile.accept(new XmlRecursiveElementVisitor() {
                @Override
                public void visitXmlTag(XmlTag tag) {
                    // TODO: Fix to correct handlebars script type
                    if (HtmlUtil.isScriptTag(tag) && "text/x-handlebars".equals(tag.getAttributeValue("type"))) {
                        final XmlAttribute id = tag.getAttribute("id");
                        if (id != null) {
                            processor.process(id);
                            return;
                        }
                    }
                    super.visitXmlTag(tag);
                }
            });
        }
    }
}
