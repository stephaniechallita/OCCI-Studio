/*
 * generated by Xtext 2.12.0
 */
package org.eclipse.cmf.occi.core.xtext.validation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;

public abstract class AbstractOCCIValidator extends AbstractDeclarativeValidator {
	
	@Override
	protected List<EPackage> getEPackages() {
		List<EPackage> result = new ArrayList<EPackage>();
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.eclipse.org/emf/2002/Ecore"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://schemas.ogf.org/occi/core/ecore"));
		return result;
	}
	
}
