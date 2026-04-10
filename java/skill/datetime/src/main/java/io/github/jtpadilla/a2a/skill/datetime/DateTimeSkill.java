package io.github.jtpadilla.a2a.skill.datetime;

import com.google.lf.a2a.v1.*;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.A2AError;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.AgentEmitter;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.RequestContext;
import io.github.jtpadilla.a2a.server.base.service.skill.spi.SkillProvider;
import io.helidon.service.registry.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Service.Singleton
public class DateTimeSkill implements SkillProvider {

    private static final Logger LOGGER = Logger.getLogger(DateTimeSkill.class.getName());

    @Override
    public AgentSkill getSkillCard() {
        return AgentSkill.newBuilder()
                .setId("b3a1c2d4-e5f6-7890-abcd-ef1234567890")
                .setName("DateTime")
                .setDescription("Returns current UTC date and time in ISO 8601 format")
                .addTags("datetime")
                .addTags("time")
                .addTags("date")
                .build();
    }

    @Override
    public void execute(RequestContext context, AgentEmitter agentEmitter) throws A2AError {

        /*
        Message responseMessage = Message.newBuilder()
                .setMessageId(UUID.randomUUID().toString())
                .setContextId(request.getMessage().getContextId())
                .setRole(Role.ROLE_AGENT)
                .addParts(Part.newBuilder().setText(currentDateTimeUtc()).build())
                .build();

            responseObserver.onNext(SendMessageResponse.newBuilder()
                    .setMessage(responseMessage)
                    .build());
            responseObserver.onCompleted();

         */
    }

    @Override
    public void cancel(RequestContext context, AgentEmitter agentEmitter) throws A2AError {
    }

    private String currentDateTimeUtc() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }

}
