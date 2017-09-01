package com.yyx.merge.copier;

import com.yyx.merge.Merger;
import com.yyx.merge.copier.impl.DeepCopyCopier;

public class CopierFactory {
    private final Merger merger;

    public CopierFactory(Merger merger) {this.merger = merger;}

    public Copier getCopier() {
        return new DeepCopyCopier(merger);
    }
}
