/*
 * This file is generated by jOOQ.
*/
package com.epam.ta.reportportal.store.jooq.tables.pojos;

import javax.annotation.Generated;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * This class is generated by jOOQ.
 */
@Generated(value = { "http://www.jooq.org", "jOOQ version:3.10.5" }, comments = "This class is generated by jOOQ")
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JDashboard implements Serializable {

	private static final long serialVersionUID = -1777277192;

	private Integer id;
	private String name;
	private Integer projectId;
	private Timestamp creationDate;

	public JDashboard() {
	}

	public JDashboard(JDashboard value) {
		this.id = value.id;
		this.name = value.name;
		this.projectId = value.projectId;
		this.creationDate = value.creationDate;
	}

	public JDashboard(Integer id, String name, Integer projectId, Timestamp creationDate) {
		this.id = id;
		this.name = name;
		this.projectId = projectId;
		this.creationDate = creationDate;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Timestamp getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("JDashboard (");

		sb.append(id);
		sb.append(", ").append(name);
		sb.append(", ").append(projectId);
		sb.append(", ").append(creationDate);

		sb.append(")");
		return sb.toString();
	}
}
