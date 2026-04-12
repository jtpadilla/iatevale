package io.github.jtpadilla.a2a.server.lib.operations.executor.impl.event;

import com.google.lf.a2a.v1.TaskArtifactUpdateEvent;

public record EmitterTaskArtifactUpdateEvent(TaskArtifactUpdateEvent event) implements EmitterEvent {}
