package io.github.jtpadilla.a2a.server.lib.operations.executor.impl.event;

import com.google.lf.a2a.v1.TaskStatusUpdateEvent;

public record EmitterTaskStatusUpdateEvent(TaskStatusUpdateEvent event) implements EmitterEvent {}
