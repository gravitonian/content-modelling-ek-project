/**
 * <p/>
 * This file is part of the Hyland Alfresco - Content Modelling and Classification Udemy course.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.platformsample;

import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A list value constraint that dynamically sets the allowed values instead of getting them from
 * the static content model definition of the constraint.
 *
 * @author Martin Bergljung
 */
public class DynamicListConstraint extends ListOfValuesConstraint {
	private final static Log logger = LogFactory.getLog(DynamicListConstraint.class);

	/**
	 * The parameter name that is used to set the dynamic list name that an instance of this dynamic constraint should serve
	 */
	public final static String LIST_NAME_PARAM = "listName";

	/**
	 * <b>Parameter required</b>
	 * <p>
	 * This is the list name used to get the list of constraint values, for example "departments".
	 * This class could support several different types of dynamic lists,
	 * and this name tells the class what list we are interested in.
	 * </p>
	 */
	private String listName = null;

	/**
	 * @return the list name to use when fetching constraint values
	 */
	public String getListName() {
		return this.listName;
	}

	/**
	 * @param listName the list name to use when fetching constraint values
	 */
	public void setListName(String listName) {
		this.listName = listName;
	}

	/**
	 * Default ctor is used to instantiate it.
	 */
	public DynamicListConstraint() {
		super();
	}

	@Override
	public void initialize() {
		checkPropertyNotNull(LIST_NAME_PARAM, this.listName);

		// Populate the out-of-the-box LIST constraint with dynamic values, instead of from the static content model XML
		super.setAllowedValues(getDynamicValues(this.listName));

		// Make sure constraint is registered
		super.initialize();
	}

	@Override
	public Map<String, Object> getParameters() {
		Map<String, Object> params = super.getParameters();
		params.put(LIST_NAME_PARAM, this.listName);
		return params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(this.getClass().getSimpleName());
		sb.append("[").append(listName).append("=").append(this.listName);
		sb.append("][values=").append(getAllowedValues().toString()).append(" ]");

		return sb.toString();
	}

	/**
	 * These are the values that are fetched from for example a database table, static file, properties file etc
	 * for the passed in list name.
	 *
	 * @param listName the name of the list we want values for
	 * @return the list of values
	 */
	private List<String> getDynamicValues(String listName) {
		System.out.println("getDynamicValues starting...");

		List<String> dynamicValues = new ArrayList<String>();

		if (StringUtils.isNotEmpty(this.listName)) {
			if (StringUtils.equals(this.listName, "departments")) {
				dynamicValues.add("Executive Leadership");
				dynamicValues.add("HR");
				dynamicValues.add("Finance");
				dynamicValues.add("Marketing");
				dynamicValues.add("Sales");
				dynamicValues.add("Operations");
				dynamicValues.add("IT");
				dynamicValues.add("R&D");
				dynamicValues.add("Customer Service");
				dynamicValues.add("Legal");
				dynamicValues.add("Administration");
				dynamicValues.add("Production");
				dynamicValues.add("QA");
				dynamicValues.add("Procurement");
				dynamicValues.add("Logistics");
				dynamicValues.add("Business Development");
				dynamicValues.add("PR");
				dynamicValues.add("Facilities Management");
				dynamicValues.add("Product Management");
				dynamicValues.add("Engineering");
			} else {
				dynamicValues.add("Unknown listName = " + this.listName);
			}
		} else {
			dynamicValues.add("listName isn't set");
		}

		return dynamicValues;
	}
}
