/*
 * This file is generated by jOOQ.
*/
package com.epam.ta.reportportal.store.jooq.tables.pojos;

import javax.annotation.Generated;
import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@Generated(value = { "http://www.jooq.org", "jOOQ version:3.10.5" }, comments = "This class is generated by jOOQ")
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JOauthRegistrationScope implements Serializable {

	private static final long serialVersionUID = -547556983;

	private Integer id;
	private String oauthRegistrationFk;
	private String scope;

	public JOauthRegistrationScope() {
	}

	public JOauthRegistrationScope(JOauthRegistrationScope value) {
		this.id = value.id;
		this.oauthRegistrationFk = value.oauthRegistrationFk;
		this.scope = value.scope;
	}

	public JOauthRegistrationScope(Integer id, String oauthRegistrationFk, String scope) {
		this.id = id;
		this.oauthRegistrationFk = oauthRegistrationFk;
		this.scope = scope;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOauthRegistrationFk() {
		return this.oauthRegistrationFk;
	}

	public void setOauthRegistrationFk(String oauthRegistrationFk) {
		this.oauthRegistrationFk = oauthRegistrationFk;
	}

	public String getScope() {
		return this.scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("JOauthRegistrationScope (");

		sb.append(id);
		sb.append(", ").append(oauthRegistrationFk);
		sb.append(", ").append(scope);

		sb.append(")");
		return sb.toString();
	}
}
