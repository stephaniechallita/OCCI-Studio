/*
 * generated by Xtext 2.10.0
 */
package org.eclipse.cmf.occi.core.xtext

import org.eclipse.cmf.occi.core.xtext.scoping.OCCILinker
import org.eclipse.xtext.parsetree.reconstr.ITransientValueService
import org.eclipse.cmf.occi.core.xtext.services.OCCITransientValueService
import org.eclipse.xtext.resource.IDerivedStateComputer
import org.eclipse.xtext.resource.IResourceDescription.Manager
import org.eclipse.xtext.resource.DerivedStateAwareResourceDescriptionManager
import org.eclipse.xtext.resource.DerivedStateAwareResource
import org.eclipse.xtext.resource.XtextResource

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
class OCCIRuntimeModule extends AbstractOCCIRuntimeModule {
	
	override Class<? extends org.eclipse.xtext.linking.ILinker> bindILinker() 
	{ 
		OCCILinker
	}
	override Class<? extends ITransientValueService> bindITransientValueService() {
			OCCITransientValueService
	}
	def Class<? extends IDerivedStateComputer> bindIDerivedStateComputer() {
		OCCIDerivedStateComputer
	}
	
	def Class<? extends Manager> bindIResourceDescription$Manager() {
		DerivedStateAwareResourceDescriptionManager
	}
	
	override Class<? extends XtextResource> bindXtextResource() {
		DerivedStateAwareResource
	}
}
