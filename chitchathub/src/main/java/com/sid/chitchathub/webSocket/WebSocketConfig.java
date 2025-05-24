package com.sid.chitchathub.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200")  //Allowed regions
                .withSockJS();  //over sock js
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("app");   //when the client(frontend) send at app/**, Tells Spring to route /app/** messages to @MessageMapping methods.
        registry.enableSimpleBroker("/user");  //Enables routing via the in-memory broker (you don’t subscribe to /user directly).
        registry.setUserDestinationPrefix("/user");            //Lets you subscribe to /user/** on frontend and send with convertAndSendToUser()
    }


    //✅ It is used for deserialization of incoming WebSocket messages.
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(APPLICATION_JSON); // Ensures JSON is expected

        //It converts json to message
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());  // Uses Jackson for conversion
        converter.setContentTypeResolver(resolver);     // Uses resolver to determine type

        messageConverters.add(converter);
        return false;
    }
}
