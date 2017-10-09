package com.yyx.merge;

import com.yyx.merge.function.FieldUpdateNotifier;

public class UpdateCollector {
    private final Object from;
    private final Object to;
    private final FieldUpdateNotifier notifier;

    public UpdateCollector(Object from, Object to, FieldUpdateNotifier notifier) {
        this.from = from;
        this.to = to;
        this.notifier = notifier;
    }

    public Object getFrom() {
        return from;
    }

    public Object getTo() {
        return to;
    }

    public FieldUpdateNotifier getNotifier() {
        return notifier;
    }
}
