package io.github.jtpadilla.a2a.server.lib.operations.executor.impl.event;

import com.google.lf.a2a.v1.Message;

public record EmitterMessageEvent(Message event) implements EmitterEvent {}
