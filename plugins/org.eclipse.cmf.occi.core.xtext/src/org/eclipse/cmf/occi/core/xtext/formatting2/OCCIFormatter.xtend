/*
 * generated by Xtext 2.11.0
 */
package org.eclipse.cmf.occi.core.xtext.formatting2

import com.google.inject.Inject
import org.eclipse.cmf.occi.core.AttributeState
import org.eclipse.cmf.occi.core.Configuration
import org.eclipse.cmf.occi.core.Link
import org.eclipse.cmf.occi.core.Mixin
import org.eclipse.cmf.occi.core.MixinBase
import org.eclipse.cmf.occi.core.Resource
import org.eclipse.cmf.occi.core.xtext.services.OCCIGrammarAccess
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument
import org.eclipse.cmf.occi.core.OCCIPackage

class OCCIFormatter extends AbstractFormatter2 {
	
	@Inject extension OCCIGrammarAccess
	
	
	def dispatch void format(Configuration configuration, extension IFormattableDocument document) {
//		val kconf = configuration.regionFor.keyword("configuration")
//		kconf.append[newLine]
//		val kdesc = configuration.regionFor.feature(OCCIPackage.Literals.CONFIGURATION__DESCRIPTION)
//		kdesc.append[newLine]
		val klocation = configuration.regionFor.feature(OCCIPackage.Literals.CONFIGURATION__LOCATION)
		klocation.append[newLines = 2]
		val kuse = configuration.regionFor.keyword(")")
		kuse.append[newLines = 2]
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		for (Resource resource : configuration.getResources()) {
			resource.format;
		}
		for (Mixin mixin : configuration.getMixins()) {
			mixin.format;
		}
	}

	def dispatch void format(Resource resource, extension IFormattableDocument document) {
		val kkind = resource.regionFor.feature(OCCIPackage.Literals.ENTITY__TITLE)
		kkind.append[newLines = 1]
		val kattribute = resource.regionFor.assignment(resourceAccess.attributesAssignment_8)
		kattribute.append[newLines = 2]
		for (MixinBase mixinBase : resource.getParts()) {
			mixinBase.format;
		}
		for (AttributeState attributeState : resource.getAttributes()) {
			attributeState.format;
		}
		for (Link link : resource.getLinks()) {
			link.format;
		}
	}
	
	// TODO: implement for Link, MixinBase, Extension, Kind, Mixin, Attribute, Action, FSM, State, EnumerationType, RecordType, RecordField
}
