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

package com.epam.ta.reportportal.ws.rabbit;

import com.epam.ta.reportportal.core.configs.Conditions;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

/**
 * @author Konstantin Antipin
 */
@Component
@Conditional(Conditions.NotTestCondition.class)
public class ReportingStartupService {

  @Autowired
  @Qualifier("listenerContainers")
  private List<AbstractMessageListenerContainer> listenerContainers;

  @PostConstruct
  public void init() {
    for (AbstractMessageListenerContainer listenerContainer : listenerContainers) {
      listenerContainer.start();
    }
  }

}
