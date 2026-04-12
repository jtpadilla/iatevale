package io.github.jtpadilla.a2a.server.lib.operations.executor.impl.event;

public sealed interface EmitterEvent permits EmitterMessageEvent, EmitterTaskArtifactUpdateEvent, EmitterTaskCreateEvent, EmitterTaskStatusUpdateEvent {
}
