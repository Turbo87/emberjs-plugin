package org.emberjs.index;

import com.intellij.lang.javascript.index.JSEntryIndex;
import com.intellij.lang.javascript.index.JSSymbolUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Dennis.Ushakov
 */
public abstract class EmberIndexBase extends ScalarIndexExtension<String> {
    private final DataIndexer<String, Void, FileContent> myIndexer =
            new DataIndexer<String, Void, FileContent>() {
                @Override
                @NotNull
                public Map<String, Void> map(@NotNull FileContent inputData) {
                    return JSSymbolUtil.indexFile(inputData.getPsiFile(), inputData).indexEntry.getStoredNames(getName().toString());
                }
            };
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return myIndexer;
    }

    @NotNull
    @Override
    public abstract ID<String, Void> getName();

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return JSEntryIndex.ourIndexedFilesFilter;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return EmberIndexUtil.BASE_VERSION + JSEntryIndex.getVersionStatic();
    }
}
