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
package org.acme.doclibaction.evaluator;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * DocLib action evaluator that will return true if a node is the
 * "Client & Supplier profiles" folder in the Enterprise KB Site.
 *
 * @author martin.bergljung
 */
public class CheckIfFolderIsClientSupplierEvaluator extends BaseEvaluator {
	private static final String PROP_NAME = "cm:name";
	private static final String CLIENT_AND_SUPPLIER_FOLDER_NAME = "Supplier & Client profiles";

	@Override
	public boolean evaluate(JSONObject jsonObject) {
		try {
			Object obj = getProperty(jsonObject, PROP_NAME);
			if (obj == null) {
				return false;
			} else {
				String nodeName = (String)obj;
				if (nodeName.equalsIgnoreCase(CLIENT_AND_SUPPLIER_FOLDER_NAME)) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception err) {
			throw new RuntimeException("JSONException whilst running action evaluator: " + err.getMessage());
		}
	}
}
