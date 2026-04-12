package io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl;

import com.google.lf.a2a.v1.*;
import com.google.protobuf.Struct;
import io.github.jtpadilla.a2a.server.base.lib.model.TaskStateUtil;
import io.github.jtpadilla.a2a.server.base.lib.operations.executor.impl.event.*;
import io.github.jtpadilla.a2a.server.base.lib.spec.Emitter;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EmitterImpl implements Emitter {

    private final Consumer<EmitterEvent> emitter;
    @Nullable
    private final String contextId;
    @Nullable
    private final String taskId;
    private final AtomicBoolean terminalStateReached = new AtomicBoolean(false);

    public EmitterImpl(Consumer<EmitterEvent> emitter, SendMessageRequest sendMessageRequest) {
        this.emitter = emitter;
        if (!sendMessageRequest.hasMessage()) {
            throw new IllegalArgumentException("SendMessageRequest must have a message");
        }
        final Message message = sendMessageRequest.getMessage();
        this.contextId = message.getContextId().isBlank() ? null : message.getContextId().trim();
        this.taskId = message.getTaskId().isBlank() ? null : message.getTaskId().trim();
    }

    public Optional<String> taskId() {
        return Optional.ofNullable(taskId);
    }

    public Optional<String> contextId() {
        return Optional.ofNullable(contextId);
    }

    private String taskIdRequired() {
        return taskId().orElseThrow(()->new IllegalArgumentException("TaskId is missing"));
    }

    private String contextIdRequired() {
        return contextId().orElseThrow(()->new IllegalArgumentException("ContextId is missing"));
    }

    @Override
    public void messageSend(Message message) {
        // Se encola el evento
        emitter.accept(new EmitterMessageEvent(message));
    }

    @Override
    public void taskCreate(Task task) {
        emitter.accept(new EmitterTaskCreateEvent(task));
    }

    @Override
    public void taskStatusUpdate(TaskStatus taskStatus) {
        taskStatusUpdateImpl(taskStatus, null);
    }

    @Override
    public void taskStatusUpdate(TaskStatus taskStatus, Struct metadata) {
        taskStatusUpdateImpl(taskStatus, metadata);
    }

    private void taskStatusUpdateImpl(TaskStatus taskStatus, @Nullable Struct metadata) {

        final boolean isTerminal = TaskStateUtil.isTerminal(taskStatus.getState());

        // Comprobar estado terminal primero (fallo rápido)
        if (terminalStateReached.get()) {
            throw new IllegalStateException("Cannot update task status - terminal state already reached");
        }

        // Para estados finales, establecer el indicador de forma atómica
        if (isTerminal) {
            if (!terminalStateReached.compareAndSet(false, true)) {
                throw new IllegalStateException("Cannot update task status - terminal state already reached");
            }
        }

        // Se compone el mensaje dirigido al cliente
        final TaskStatusUpdateEvent.Builder taskStatusUpdateEvent = TaskStatusUpdateEvent.newBuilder()
                .setTaskId(taskIdRequired())
                .setContextId(contextIdRequired())
                .setStatus(taskStatus);
        if (metadata != null) {
            taskStatusUpdateEvent.setMetadata(metadata);
        }

        // Se encola el evento
        emitter.accept(new EmitterTaskStatusUpdateEvent(taskStatusUpdateEvent.build()));

    }

    @Override
    public void taskArtifactUpdate(Artifact artifact) {
        taskArtifactUpdateImpl(artifact, false, true, null);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, Struct metadata) {
        taskArtifactUpdateImpl(artifact, false, true, metadata);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk) {
        taskArtifactUpdateImpl(artifact, append, lastChunk, null);
    }

    @Override
    public void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk, Struct metadata) {
        taskArtifactUpdateImpl(artifact, append, lastChunk, metadata);
    }

    private void taskArtifactUpdateImpl(Artifact artifact, boolean append, boolean lastChunk, @Nullable Struct metadata) {

        // Se compone el mensaje dirigido al cliente
        final TaskArtifactUpdateEvent.Builder taskArtifactUpdateEvent = TaskArtifactUpdateEvent.newBuilder()
                .setTaskId(taskIdRequired())
                .setContextId(contextIdRequired())
                .setArtifact(artifact)
                .setAppend(append)
                .setLastChunk(lastChunk);
        if (metadata != null) {
            taskArtifactUpdateEvent.setMetadata(metadata);
        }

        // Se encola el evento
        emitter.accept(new EmitterTaskArtifactUpdateEvent(taskArtifactUpdateEvent.build()));

    }

}
