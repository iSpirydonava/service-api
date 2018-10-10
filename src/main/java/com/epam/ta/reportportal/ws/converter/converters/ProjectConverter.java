/*
 * Copyright (C) 2018 EPAM Systems
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

package com.epam.ta.reportportal.ws.converter.converters;

import com.epam.ta.reportportal.entity.enums.ProjectAttributeEnum;
import com.epam.ta.reportportal.entity.item.issue.IssueType;
import com.epam.ta.reportportal.entity.project.Project;
import com.epam.ta.reportportal.entity.project.ProjectIssueType;
import com.epam.ta.reportportal.entity.project.ProjectUtils;
import com.epam.ta.reportportal.ws.model.project.ProjectConfiguration;
import com.epam.ta.reportportal.ws.model.project.ProjectResource;
import com.epam.ta.reportportal.ws.model.project.config.IssueSubTypeResource;
import com.epam.ta.reportportal.ws.model.project.email.EmailSenderCaseDTO;
import com.epam.ta.reportportal.ws.model.project.email.ProjectEmailConfigDTO;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Pavel Bortnik
 */
public final class ProjectConverter {

	private ProjectConverter() {
		//static only
	}

	public static final Function<Project, ProjectResource> TO_PROJECT_RESOURCE = project -> {
		if (project == null) {
			return null;
		}

		ProjectResource projectResource = new ProjectResource();
		projectResource.setProjectId(project.getId());
		projectResource.setProjectName(project.getName());
		projectResource.setCreationDate(project.getCreationDate());
		projectResource.setUsers(project.getUsers().stream().map(user -> {
			ProjectResource.ProjectUser projectUser = new ProjectResource.ProjectUser();
			projectUser.setLogin(user.getUser().getLogin());
			projectUser.setProjectRole(user.getProjectRole().toString());
			return projectUser;
		}).collect(Collectors.toList()));

		Map<String, List<IssueSubTypeResource>> subTypes = project.getProjectIssueTypes()
				.stream()
				.map(ProjectIssueType::getIssueType)
				.collect(Collectors.groupingBy(
						it -> it.getIssueGroup().getTestItemIssueGroup().getValue(),
						Collectors.mapping(ProjectConverter.TO_SUBTYPE_RESOURCE, Collectors.toList())
				));

		ProjectConfiguration projectConfiguration = new ProjectConfiguration();
		projectConfiguration.setSubTypes(subTypes);

		ProjectEmailConfigDTO projectEmailConfigDTO = new ProjectEmailConfigDTO();

		Map<String, String> attributes = ProjectUtils.getConfigParameters(project.getProjectAttributes());
		List<EmailSenderCaseDTO> emailCases = project.getEmailCases()
				.stream()
				.map(EmailConfigConverters.TO_CASE_RESOURCE)
				.collect(Collectors.toList());

		projectEmailConfigDTO.setEmailCases(emailCases);
		projectEmailConfigDTO.setEmailEnabled(BooleanUtils.toBoolean(attributes.get(ProjectAttributeEnum.EMAIL_ENABLED.getAttribute())));
		projectEmailConfigDTO.setFrom(attributes.get(ProjectAttributeEnum.EMAIL_FROM.getAttribute()));
		projectConfiguration.setEmailConfig(projectEmailConfigDTO);

		projectConfiguration.setProjectAttributes(attributes);
		projectResource.setConfiguration(projectConfiguration);
		return projectResource;
	};

	static final Function<IssueType, IssueSubTypeResource> TO_SUBTYPE_RESOURCE = issueType -> {
		IssueSubTypeResource issueSubTypeResource = new IssueSubTypeResource();
		issueSubTypeResource.setLocator(issueType.getLocator());
		issueSubTypeResource.setColor(issueType.getHexColor());
		issueSubTypeResource.setLongName(issueType.getLongName());
		issueSubTypeResource.setShortName(issueType.getShortName());
		issueSubTypeResource.setTypeRef(issueType.getIssueGroup().getTestItemIssueGroup().getValue());
		return issueSubTypeResource;
	};

}
