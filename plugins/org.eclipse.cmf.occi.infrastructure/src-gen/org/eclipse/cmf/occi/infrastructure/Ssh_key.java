/**
 * Copyright (c) 2015-2017 Obeo, Inria
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 	
 * Contributors:
 * - William Piers <william.piers@obeo.fr>
 * - Philippe Merle <philippe.merle@inria.fr>
 * - Faiez Zalila <faiez.zalila@inria.fr>
 */
package org.eclipse.cmf.occi.infrastructure;

import java.util.Map;

import org.eclipse.cmf.occi.core.MixinBase;

import org.eclipse.emf.common.util.DiagnosticChain;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ssh key</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.cmf.occi.infrastructure.Ssh_key#getOcciCredentialsSshPublickey <em>Occi Credentials Ssh Publickey</em>}</li>
 * </ul>
 *
 * @see org.eclipse.cmf.occi.infrastructure.InfrastructurePackage#getSsh_key()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='appliesConstraint'"
 * @generated
 */
public interface Ssh_key extends MixinBase {
	/**
	 * Returns the value of the '<em><b>Occi Credentials Ssh Publickey</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The contents of the public key file to be injected into the Compute Resource
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Occi Credentials Ssh Publickey</em>' attribute.
	 * @see #setOcciCredentialsSshPublickey(String)
	 * @see org.eclipse.cmf.occi.infrastructure.InfrastructurePackage#getSsh_key_OcciCredentialsSshPublickey()
	 * @model dataType="org.eclipse.cmf.occi.core.String" required="true"
	 *        annotation="http://www.eclipse.org/emf/2002/GenModel get='throw new UnsupportedOperationException();  // FIXME Unimplemented http://schemas.ogf.org/occi/infrastructure/ecore!Ssh_key!occiCredentialsSshPublickey'"
	 * @generated
	 */
	String getOcciCredentialsSshPublickey();

	/**
	 * Sets the value of the '{@link org.eclipse.cmf.occi.infrastructure.Ssh_key#getOcciCredentialsSshPublickey <em>Occi Credentials Ssh Publickey</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Occi Credentials Ssh Publickey</em>' attribute.
	 * @see #getOcciCredentialsSshPublickey()
	 * @generated
	 */
	void setOcciCredentialsSshPublickey(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model annotation="http://www.eclipse.org/emf/2002/GenModel body='/**\n *\n * inv appliesConstraint:\n *   let severity : Integer[1] = \'Ssh_key::appliesConstraint\'.getSeverity()\n *   in\n *     if severity <= 0\n *     then true\n *     else\n *       let result : occi::Boolean[1] = self.entity.oclIsKindOf(Compute)\n *       in\n *         \'Ssh_key::appliesConstraint\'.logDiagnostic(self, null, diagnostics, context, null, severity, result, 0)\n *     endif\n \052/\nfinal /*@NonInvalid\052/ <%org.eclipse.ocl.pivot.evaluation.Executor%> executor = <%org.eclipse.ocl.pivot.internal.utilities.PivotUtilInternal%>.getExecutor(this);\nfinal /*@NonInvalid\052/ <%org.eclipse.ocl.pivot.ids.IdResolver%> idResolver = executor.getIdResolver();\nfinal /*@NonInvalid\052/ <%org.eclipse.ocl.pivot.values.IntegerValue%> severity_0 = <%org.eclipse.ocl.pivot.library.string.CGStringGetSeverityOperation%>.INSTANCE.evaluate(executor, <%org.eclipse.cmf.occi.infrastructure.InfrastructureTables%>.STR_Ssh_key_c_c_appliesConstraint);\nfinal /*@NonInvalid\052/ boolean le = <%org.eclipse.ocl.pivot.library.oclany.OclComparableLessThanEqualOperation%>.INSTANCE.evaluate(executor, severity_0, <%org.eclipse.cmf.occi.infrastructure.InfrastructureTables%>.INT_0).booleanValue();\n/*@NonInvalid\052/ boolean symbol_0;\nif (le) {\n\tsymbol_0 = <%org.eclipse.ocl.pivot.utilities.ValueUtil%>.TRUE_VALUE;\n}\nelse {\n\tfinal /*@NonInvalid\052/ <%org.eclipse.ocl.pivot.Class%> TYP_infrastructure_c_c_Compute_0 = idResolver.getClass(<%org.eclipse.cmf.occi.infrastructure.InfrastructureTables%>.CLSSid_Compute, null);\n\tfinal /*@NonInvalid\052/ <%org.eclipse.cmf.occi.core.Entity%> entity = this.getEntity();\n\tfinal /*@NonInvalid\052/ boolean result = <%org.eclipse.ocl.pivot.library.oclany.OclAnyOclIsKindOfOperation%>.INSTANCE.evaluate(executor, entity, TYP_infrastructure_c_c_Compute_0).booleanValue();\n\tfinal /*@NonInvalid\052/ boolean logDiagnostic = <%org.eclipse.ocl.pivot.library.string.CGStringLogDiagnosticOperation%>.INSTANCE.evaluate(executor, <%org.eclipse.ocl.pivot.ids.TypeId%>.BOOLEAN, <%org.eclipse.cmf.occi.infrastructure.InfrastructureTables%>.STR_Ssh_key_c_c_appliesConstraint, this, (Object)null, diagnostics, context, (Object)null, severity_0, result, <%org.eclipse.cmf.occi.infrastructure.InfrastructureTables%>.INT_0).booleanValue();\n\tsymbol_0 = logDiagnostic;\n}\nreturn Boolean.TRUE == symbol_0;'"
	 * @generated
	 */
	boolean appliesConstraint(DiagnosticChain diagnostics, Map<Object, Object> context);

} // Ssh_key
