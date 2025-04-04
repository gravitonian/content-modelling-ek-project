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
