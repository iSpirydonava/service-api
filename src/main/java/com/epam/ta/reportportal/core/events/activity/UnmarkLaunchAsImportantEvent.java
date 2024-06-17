/*
 * Copyright 2024 EPAM Systems
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
package com.epam.ta.reportportal.core.events.activity;

import com.epam.ta.reportportal.builder.ActivityBuilder;
import com.epam.ta.reportportal.core.events.ActivityEvent;
import com.epam.ta.reportportal.entity.activity.Activity;
import com.epam.ta.reportportal.entity.activity.ActivityAction;
import com.epam.ta.reportportal.entity.activity.EventAction;
import com.epam.ta.reportportal.entity.activity.EventObject;
import com.epam.ta.reportportal.entity.activity.EventPriority;
import com.epam.ta.reportportal.entity.activity.EventSubject;
import com.epam.ta.reportportal.model.activity.LaunchActivityResource;

/**
 * @author Andrei Varabyeu
 */
public class UnmarkLaunchAsImportantEvent extends AbstractEvent implements ActivityEvent {

  private LaunchActivityResource launchActivityResource;

  public UnmarkLaunchAsImportantEvent() {
  }

  public UnmarkLaunchAsImportantEvent(LaunchActivityResource launchActivityResource, Long userId,
      String userLogin) {
    super(userId, userLogin);
    this.launchActivityResource = launchActivityResource;
  }

  public LaunchActivityResource getLaunchActivityResource() {
    return launchActivityResource;
  }

  public void setLaunchActivityResource(LaunchActivityResource launchActivityResource) {
    this.launchActivityResource = launchActivityResource;
  }

  @Override
  public Activity toActivity() {
    return new ActivityBuilder()
        .addCreatedNow()
        .addAction(EventAction.UPDATE)
        .addEventName(ActivityAction.UNMARK_LAUNCH_AS_IMPORTANT.getValue())
        .addPriority(EventPriority.HIGH)
        .addObjectId(launchActivityResource.getId())
        .addObjectName(launchActivityResource.getName())
        .addObjectType(EventObject.LAUNCH)
        .addProjectId(launchActivityResource.getProjectId())
        .addSubjectId(getUserId())
        .addSubjectName(getUserLogin())
        .addSubjectType(EventSubject.USER)
        .get();
  }
}
