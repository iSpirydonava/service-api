/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.ta.reportportal.core.configs.rabbit;

import com.epam.ta.reportportal.core.configs.Conditions;
import com.epam.ta.reportportal.ws.rabbit.AsyncReportingListener;
import com.epam.ta.reportportal.ws.validation.JaskonRequiredPropertiesValidator;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Konstantin Antipin
 */
@Configuration
@Conditional(Conditions.NotTestCondition.class)
public class ReportingConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(ReportingConfiguration.class);

	public static final long DEAD_LETTER_DELAY_MILLIS = 60_000L;
	public static final long DEAD_LETTER_MAX_RETRY = 10L;

	/**
	 * Exchanges
	 */
	public static final String EXCHANGE_REPORTING = "reporting";
	public static final String EXCHANGE_REPORTING_RETRY = "reporting.retry";


	/**
	 * Queue definitions
	 */
	public static final String QUEUE_PREFIX = "reporting";
	public static final String QUEUE_RETRY_PREFIX = "reporting.retry";
	public static final String QUEUE_DLQ = "reporting.dlq";
	public static final int QUEUE_AMOUNT = 20;

	/**
	 * Cluster configuration parameter.
	 * Number of queues to be processed by this service-api pod (default effectively infinite)
	 * Note: should correlate with number QUEUE_AMOUNT & number of service-api pods being started in cluster
	 */
	@Value("${rp.amqp.queuesPerPod:1000000}")
	private int queuesPerPod;

	@Bean
	@Qualifier("reportingExchange")
	public Exchange reportingExchange(AmqpAdmin amqpAdmin) {
		Exchange exchange = ExchangeBuilder.directExchange(EXCHANGE_REPORTING).durable(true).build();
		amqpAdmin.declareExchange(exchange);
		return exchange;
	}

	@Bean
	@Qualifier("reportingRetryExchange")
	public Exchange reportingRetryExchange(AmqpAdmin amqpAdmin) {
		Exchange exchange = ExchangeBuilder.directExchange(EXCHANGE_REPORTING_RETRY).durable(true).build();
		amqpAdmin.declareExchange(exchange);
		return exchange;
	}


	@Bean
	@Qualifier("reportingQueues")
	public List<Queue> queues(AmqpAdmin amqpAdmin) {
		List<Queue> queues = new ArrayList();
		for (int i = 0; i < QUEUE_AMOUNT; i++) {
			String index = String.valueOf(i);
			String queueName = QUEUE_PREFIX + "." + index;
			Queue queue = QueueBuilder.durable(queueName)
					.withArgument("x-dead-letter-exchange", EXCHANGE_REPORTING_RETRY)
					.withArgument("x-dead-letter-routing-key", index)
					.build();
			queues.add(queue);
			amqpAdmin.declareQueue(queue);
		}
		return queues;
	}

	@Bean
	@Qualifier("reportingRetryQueues")
	public List<Queue> retryQueues(AmqpAdmin amqpAdmin) {
		List<Queue> queues = new ArrayList();
		for (int i = 0; i < QUEUE_AMOUNT; i++) {
			String index = String.valueOf(i);
			String queueName = QUEUE_RETRY_PREFIX + "." + index;
			Queue retryQueue = QueueBuilder.durable(queueName)
					.withArgument("x-dead-letter-exchange", EXCHANGE_REPORTING)
					.withArgument("x-dead-letter-routing-key", index)
					.withArgument("x-message-ttl", DEAD_LETTER_DELAY_MILLIS)
					.build();
			queues.add(retryQueue);
			amqpAdmin.declareQueue(retryQueue);
		}
		return queues;
	}

	@Bean
	@Qualifier("queueDlq")
	public Queue queueDlq(AmqpAdmin amqpAdmin) {
		Queue queue = QueueBuilder.durable(QUEUE_DLQ).build();
		amqpAdmin.declareQueue(queue);
		return queue;
	}

	@Bean
	public List<Binding> bindings(AmqpAdmin amqpAdmin, @Qualifier("reportingExchange") Exchange reportingExchange,
								  @Qualifier("reportingRetryExchange") Exchange reportingRetryExchange,
								  @Qualifier("reportingQueues") List<Queue> queues,
								  @Qualifier("queueDlq") Queue queueDlq,
								  @Qualifier("reportingRetryQueues") List<Queue> retryQueues) {
		List<Binding> bindings = new ArrayList<>();
		int i = 0;
		for (Queue queue : queues) {
			String index = String.valueOf(i);
			Binding queueBinding = BindingBuilder.bind(queue).to(reportingExchange).with(index).noargs();
			bindings.add(queueBinding);
			amqpAdmin.declareBinding(queueBinding);
			i++;
		}
		i = 0;
		for (Queue retryQueue : retryQueues) {
			String index = String.valueOf(i);
			Binding queueBinding = BindingBuilder.bind(retryQueue).to(reportingRetryExchange).with(index).noargs();
			bindings.add(queueBinding);
			amqpAdmin.declareBinding(queueBinding);
			i++;
		}
		Binding queueBinding = BindingBuilder.bind(queueDlq).to(reportingRetryExchange).with(QUEUE_DLQ).noargs();
		amqpAdmin.declareBinding(queueBinding);

		return bindings;
	}

	@Bean
	@Qualifier("reportingListenerContainers")
	public List<AbstractMessageListenerContainer> listenerContainers(ConnectionFactory connectionFactory,
																	 @Qualifier("queues") List<Queue> queues) {
		List<AbstractMessageListenerContainer> containers = new ArrayList<>();
		Channel channel = connectionFactory.createConnection().createChannel(false);
		int myQueues = 0;
		for (Queue queue : queues) {
			try {
				if (myQueues < queuesPerPod && getQueueConsumerCount(channel, queue) == 0) {
					SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer(connectionFactory);
					containers.add(listenerContainer);
					listenerContainer.setConnectionFactory(connectionFactory);
					listenerContainer.addQueueNames(queue.getName());
					listenerContainer.setExclusive(true);
					listenerContainer.setMissingQueuesFatal(false);
					listenerContainer.setupMessageListener(reportingListener());
					listenerContainer.afterPropertiesSet();
					myQueues++;
					logger.info("Consumer connected to queue {}, myQueues current count is {}", queue.getName(), myQueues);
				}
			} catch (Exception e) {
				logger.error("Trying to connect to queue {}, myQueues current count is {}, exception ", queue.getName(), myQueues, e);
			}
		}
		if (containers.size() < queuesPerPod) {
			logger.error("Started amount of consumers less then configured (or single node start) : {} < {}", containers.size(), queuesPerPod);
		}
		return containers;
	}

	private int getQueueConsumerCount(Channel channel, Queue queue) throws IOException {
		return channel.queueDeclarePassive(queue.getName()).getConsumerCount();
	}

	@Bean
	@Qualifier("reportingListener")
	public MessageListener reportingListener() {
		return new AsyncReportingListener();
	}

}
