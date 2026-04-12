package io.github.jtpadilla.a2a.server.base.lib.spec;

import com.google.lf.a2a.v1.Artifact;
import com.google.lf.a2a.v1.Message;
import com.google.lf.a2a.v1.Task;
import com.google.lf.a2a.v1.TaskStatus;
import com.google.protobuf.Struct;

import java.util.Optional;

public interface Emitter {

    Optional<String> taskId();
    Optional<String> contextId();

    Message.Builder messageBuilder();
    void messageSend(Message message) throws EmitterException;

    void taskCreate(Task task) throws EmitterException;

    void taskStatusUpdate(TaskStatus taskStatus) throws EmitterException;
    void taskStatusUpdate(TaskStatus taskStatus, Struct metadata) throws EmitterException;

    void taskArtifactUpdate(Artifact artifact) throws EmitterException;
    void taskArtifactUpdate(Artifact artifact, Struct metadata) throws EmitterException;
    void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk) throws EmitterException;
    void taskArtifactUpdate(Artifact artifact, boolean append, boolean lastChunk, Struct metadata) throws EmitterException;

}