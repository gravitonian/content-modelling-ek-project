package org.acme.platformsample;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Repository Action implementation to create a Supplier/Client Profile content-less node.
 *
 * @author martin.bergljung with the help of Claud AI
 */
public class CreateSupplierClientProfileActionExecuter extends ActionExecuterAbstractBase {
	private static final Log logger = LogFactory.getLog(CreateSupplierClientProfileActionExecuter.class);

	/**
	 * Action constants
	 */
	public static final String NAME = "create-supplier-client";

	public static final String PARAM_NAME = "prop_cm_name";
	public static final String PARAM_TITLE = "prop_cm_title";
	public static final String PARAM_DESCRIPTION = "prop_cm_description";
	public static final String PARAM_PROFILE_NAME = "prop_ek_profileName";
	public static final String PARAM_PROFILE_TYPE = "prop_ek_profileType";
	public static final String PARAM_CONTACT_ADDRESS = "prop_ek_contactAddress";
	public static final String PARAM_CONTACT_PHONE = "prop_ek_contactPhone";
	public static final String PARAM_CONTACT_EMAIL = "prop_ek_contactEmail";
	public static final String PARAM_COMPLIANCE_CERTIFICATIONS = "prop_ek_complianceCertifications";
	public static final String PARAM_RELATED_CONTRACTS = "assoc_ek_profileRelatedContracts";

	/**
	 * The Alfresco namespace URI for the EK content model
	 */
	private static final String EK_NAMESPACE_URI = "http://www.mycompany.com/model/ek/1.0";

	/**
	 * Node service
	 */
	private NodeService nodeService;

	/**
	 * Set the node service
	 *
	 * @param nodeService the NodeService
	 */
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * @see org.alfresco.repo.action.executer.ActionExecuterAbstractBase#executeImpl(org.alfresco.service.cmr.action.Action, org.alfresco.service.cmr.repository.NodeRef)
	 */
	@Override
	protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {
		// The actioned upon node is the destination folder
		if (nodeService.exists(actionedUponNodeRef) == false) {
			return;
		}

		// Get the parameter values
		String name = (String) action.getParameterValue(PARAM_NAME);
		String title = (String) action.getParameterValue(PARAM_TITLE);
		String description = (String) action.getParameterValue(PARAM_DESCRIPTION);
		String profileName = (String) action.getParameterValue(PARAM_PROFILE_NAME);
		String profileType = (String) action.getParameterValue(PARAM_PROFILE_TYPE);
		String contactAddress = (String) action.getParameterValue(PARAM_CONTACT_ADDRESS);
		String contactPhone = (String) action.getParameterValue(PARAM_CONTACT_PHONE);
		String contactEmail = (String) action.getParameterValue(PARAM_CONTACT_EMAIL);
		String complianceCertifications = (String) action.getParameterValue(PARAM_COMPLIANCE_CERTIFICATIONS);
		NodeRef[] relatedContracts = (NodeRef[]) action.getParameterValue(PARAM_RELATED_CONTRACTS);

		// Create properties for the new node
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();

		// Set content model properties (from cm:titled aspect)
		properties.put(QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "name"), name);
		if (title != null && !title.isEmpty()) {
			properties.put(QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "title"), title);
		}
		if (description != null && !description.isEmpty()) {
			properties.put(QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "description"), description);
		}

		// Set EK model properties
		if (profileName != null && !profileName.isEmpty()) {
			properties.put(QName.createQName(EK_NAMESPACE_URI, "profileName"), profileName);
		}
		if (profileType != null && !profileType.isEmpty()) {
			properties.put(QName.createQName(EK_NAMESPACE_URI, "profileType"), profileType);
		}
		if (contactAddress != null && !contactAddress.isEmpty()) {
			properties.put(QName.createQName(EK_NAMESPACE_URI, "contactAddress"), contactAddress);
		}
		if (contactPhone != null && !contactPhone.isEmpty()) {
			properties.put(QName.createQName(EK_NAMESPACE_URI, "contactPhone"), contactPhone);
		}
		if (contactEmail != null && !contactEmail.isEmpty()) {
			properties.put(QName.createQName(EK_NAMESPACE_URI, "contactEmail"), contactEmail);
		}
		if (complianceCertifications != null && !complianceCertifications.isEmpty()) {
			properties.put(QName.createQName(EK_NAMESPACE_URI, "complianceCertifications"), complianceCertifications);
		}

		// Create the supplier/client profile node
		QName typeQName = QName.createQName(EK_NAMESPACE_URI, "supplierClientProfileData");
		QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name));

		ChildAssociationRef childAssoc = nodeService.createNode(
				actionedUponNodeRef,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "contains"),
				assocQName,
				typeQName,
				properties);

		NodeRef supplierClientNodeRef = childAssoc.getChildRef();

		// Add related contracts association if provided
		if (relatedContracts != null && relatedContracts.length > 0) {
			QName assocTypeQName = QName.createQName(EK_NAMESPACE_URI, "profileRelatedContracts");
			for (NodeRef contractRef : relatedContracts) {
				nodeService.createAssociation(supplierClientNodeRef, contractRef, assocTypeQName);
			}
		}

		// Log creation
		if (logger.isDebugEnabled()) {
			logger.debug("Created supplier/client profile: " + supplierClientNodeRef);
		}
	}

	/**
	 * @see org.alfresco.repo.action.ParameterizedItemAbstractBase#addParameterDefinitions(java.util.List)
	 */
	@Override
	protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
		paramList.add(new ParameterDefinitionImpl(PARAM_NAME, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_NAME)));
		paramList.add(new ParameterDefinitionImpl(PARAM_TITLE, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_TITLE)));
		paramList.add(new ParameterDefinitionImpl(PARAM_DESCRIPTION, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_DESCRIPTION)));
		paramList.add(new ParameterDefinitionImpl(PARAM_PROFILE_NAME, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_PROFILE_NAME)));
		paramList.add(new ParameterDefinitionImpl(PARAM_PROFILE_TYPE, DataTypeDefinition.TEXT, true, getParamDisplayLabel(PARAM_PROFILE_TYPE)));
		paramList.add(new ParameterDefinitionImpl(PARAM_CONTACT_ADDRESS, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_CONTACT_ADDRESS)));
		paramList.add(new ParameterDefinitionImpl(PARAM_CONTACT_PHONE, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_CONTACT_PHONE)));
		paramList.add(new ParameterDefinitionImpl(PARAM_CONTACT_EMAIL, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_CONTACT_EMAIL)));
		paramList.add(new ParameterDefinitionImpl(PARAM_COMPLIANCE_CERTIFICATIONS, DataTypeDefinition.TEXT, false, getParamDisplayLabel(PARAM_COMPLIANCE_CERTIFICATIONS)));
		paramList.add(new ParameterDefinitionImpl(PARAM_RELATED_CONTRACTS, DataTypeDefinition.NODE_REF, false, getParamDisplayLabel(PARAM_RELATED_CONTRACTS), true));
	}
}