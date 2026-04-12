package io.github.jtpadilla.a2a.server.lib.operations.executor.impl.event;

import com.google.lf.a2a.v1.Task;

public record EmitterTaskCreateEvent(Task event) implements EmitterEvent {}
