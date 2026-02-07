package com.fitness.activityservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
  @Value("${rabbitmq.exchange.name}")
  private String exchangeName;
  @Value("${rabbitmq.routing.key}")
  private String routingKey;
  @Value("${rabbitmq.queue.name}")
  private String queueName;

  @Bean
  public Queue activityQueue() {
    return new Queue(queueName,true);
  }

  @Bean
  public DirectExchange activityExchange() {
    return new DirectExchange(exchangeName);
  }

  @Bean
  public Binding activityBinding(Queue activitiesQueue, DirectExchange activitiesExchange) {
    return BindingBuilder.bind(activitiesQueue).to(activitiesExchange).with(routingKey);
  }

  @Bean
  public JacksonJsonMessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
  }
}
