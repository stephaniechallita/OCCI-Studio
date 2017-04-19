/*******************************************************************************
 * Copyright (c) 2015-2017 Obeo, Inria
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * 	   William Piers <william.piers@obeo.fr>
 *     Philippe Merle <philippe.merle@inria.fr>
 *     Faiez Zalila <faiez.zalila@inria.fr>
 *******************************************************************************/
package org.eclipse.cmf.occi.core.gen.emf;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.BasicEMap.Entry;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMapUtil.EntryImpl;

import com.google.common.collect.Lists;

import org.eclipse.cmf.occi.core.Action;
import org.eclipse.cmf.occi.core.ArrayType;
import org.eclipse.cmf.occi.core.Attribute;
import org.eclipse.cmf.occi.core.BooleanType;
import org.eclipse.cmf.occi.core.Constraint;
import org.eclipse.cmf.occi.core.DataType;
import org.eclipse.cmf.occi.core.EObjectType;
import org.eclipse.cmf.occi.core.EnumerationLiteral;
import org.eclipse.cmf.occi.core.EnumerationType;
import org.eclipse.cmf.occi.core.Extension;
import org.eclipse.cmf.occi.core.Kind;
import org.eclipse.cmf.occi.core.Mixin;
import org.eclipse.cmf.occi.core.MixinBase;
import org.eclipse.cmf.occi.core.NumericType;
import org.eclipse.cmf.occi.core.OCCIFactory;
import org.eclipse.cmf.occi.core.OCCIPackage;
import org.eclipse.cmf.occi.core.RecordField;
import org.eclipse.cmf.occi.core.RecordType;
import org.eclipse.cmf.occi.core.StringType;
import org.eclipse.cmf.occi.core.Type;
import org.eclipse.cmf.occi.core.util.OCCI2Ecore;

/**
 * Convert an OCCI Extension to Ecore.
 */
public class OCCIExtension2Ecore {
	/**
	 * Store mapping from OCCI Kind to Ecore EClass.
	 */
	private Map<Kind, EClass> occiKind2emfEclass = new HashMap<Kind, EClass>();

	/**
	 * Get the EClass associated to an OCCI Kind.
	 * 
	 * @param kind
	 *            the given OCCI kind.
	 * @return the EClass.
	 */
	private EClass getMappedEClass(Kind kind) {
		EClass res = null;
		if (kind != null) {
			// retrieve from currently converted kinds
			res = occiKind2emfEclass.get(kind);
			if (res == null) {
				// retrieve from installed extensions.
				EPackage p = ConverterUtils.getEPackage(kind);
				res = (EClass) p.getEClassifier(ConverterUtils.toU1Case(kind.getTerm()));
				// Cache it for optimizing next searches.
				occiKind2emfEclass.put(kind, res);
			}
		}
		return res;
	}

	/**
	 * Store mapping from OCCI EDataType to Ecore EClassifier.
	 */
	private Map<DataType, EClassifier> occiType2emfType = new HashMap<DataType, EClassifier>();

	/**
	 * Get the EMF data type associated to an OCCI data type.
	 * 
	 * @param type
	 *            the given OCCI data type.
	 * @return the EMF data type.
	 */
	private EClassifier getMappedType(DataType type) {
		EClassifier res = null;
		if (type == null) {
			res = EcorePackage.eINSTANCE.getEString();
		} else {
			// retrieve from currently converted data types
			res = occiType2emfType.get(type);
			if (res == null) {
				// retrieve from installed extensions.
				EPackage p = ConverterUtils.getEPackage(type);
				res = p.getEClassifier(type.getName());
				// Cache it for optimizing next searches.
				occiType2emfType.put(type, res);
			}
		}
		return res;
	}

	/**
	 * Convert an OCCI extension to an Ecore package.
	 * 
	 * @param extension
	 *            the OCCI extension to convert.
	 * @return the resulting Ecore package.
	 */
	public EPackage convertExtension(Extension extension) {
		// Create the Ecore package.
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		// Set the name of the Ecore package.
		String formattedName = ConverterUtils
				.formatName(extension.getName().replaceFirst("OCCI ", "").replaceFirst("OCCIware ", "").toLowerCase());
		ePackage.setName(formattedName);
		// Set the name space prefix of the Ecore package.
		ePackage.setNsPrefix(formattedName);
		// Set the URI of the Ecore package.
		ePackage.setNsURI(OCCI2Ecore.convertOcciScheme2EcoreNamespace(extension.getScheme()));

		// TODO fetch occi package
		// but won't solve issue if user wants to use types from installed
		// models
		// EClass root = EcoreFactory.eINSTANCE.createEClass();
		// ePackage.getEClassifiers().add(root);
		// root.setName(ConverterUtils.toU1Case(extension.getName()+"Configuration"));
		// root.getESuperTypes().add(OCCIPackage.eINSTANCE.getConfiguration());

		// Convert all data types of the OCCI extension to data types of the
		// Ecore package.

		// TODO FZ to update
		for (DataType type : extension.getTypes()) {
			// // Create the EMF type from the OCCI data type.
			EClassifier createdEMFType = null;
			if (type instanceof StringType) {
				createdEMFType = createStringType((StringType) type);
			}
			if (type instanceof NumericType) {
				createdEMFType = createNumericType((NumericType) type);
			}
			if (type instanceof BooleanType) {
				createdEMFType = createBooleanType((BooleanType) type);
			}
			if (type instanceof EnumerationType) {
				createdEMFType = createEnumerationType((EnumerationType) type);
			}
			if (type instanceof EObjectType) {
				createdEMFType = createEObjectType((EObjectType) type);
			}
			if (type instanceof ArrayType) {
				createdEMFType = createArrayType((ArrayType) type);
			}
			if (type instanceof RecordType) {
				createdEMFType = createRecordType((RecordType)type);
			}
			// Cache the copied data type to search it later.
			occiType2emfType.put(type, createdEMFType);
			// Add the data type to the Ecore package.
			ePackage.getEClassifiers().add(createdEMFType);
		}
		for (DataType type : extension.getTypes()) {
			if (type instanceof ArrayType) {
				completeArrayTypeFeatures((ArrayType)type, (EClass)occiType2emfType.get(type), ePackage);
			}
			if (type instanceof RecordType) {
				completeRecordTypeFeatures((RecordType)type, (EClass)occiType2emfType.get(type));
		}
		}
		// Convert all OCCI kinds.
		for (Kind kind : extension.getKinds()) {
			// Convert each OCCI kind to an Ecore class.
			EClass convertKind = convertKind(kind);
			// Add the Ecore class to the Ecore package.
			if (convertKind != null) {
				ePackage.getEClassifiers().add(convertKind);
			}
		}

		// Convert all OCCI mixins.
		for (Mixin mixin : extension.getMixins()) {
			EClass convertMixin = convertMixin(mixin);
			if (convertMixin != null) {
				ePackage.getEClassifiers().add(convertMixin);
			}
		}

		// Resolve inheritance between OCCI kinds.
		for (Kind kind : extension.getKinds()) {
			// Get the Ecore class of this OCCI kind.
			EClass mappedEClass = getMappedEClass(kind);
			// If kind has a parent kind then
			if (kind.getParent() != null) {
				// Get the Ecore class of the OCCI kind's parent.
				EClass mappedParentEClass = getMappedEClass(kind.getParent());
				if (mappedParentEClass != null) {
					// The Ecore class of the kind's parent is a super type of
					// the Ecore class of the OCCI kind.
					System.out.println("mappedParentEClass " + mappedParentEClass + mappedEClass.getEPackage());
					mappedEClass.getESuperTypes().add(mappedParentEClass);
				} else {
					// Should never happen!
					throw new IllegalArgumentException("Not found: " + kind.getParent());
				}
			}
		}
		return ePackage;
	}

	protected EClass convertMixin(Mixin mixin) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		//eClass.setName(ConverterUtils.toU1Case(ConverterUtils.formatName(mixin.getTerm())));
		eClass.setName(mixin.getName());
		
		eClass.setAbstract(true);

		addSuperType(eClass, "MixinBase");
		
		
		for (Attribute attribute : mixin.getAttributes()) {
			EAttribute convertAttribute = convertAttribute(attribute);
			if (convertAttribute != null) {
				eClass.getEStructuralFeatures().add(convertAttribute);
			}
		}
		for (Action action : mixin.getActions()) {
			EOperation convertAction = convertAction(action);
			if (convertAction != null) {
				eClass.getEOperations().add(convertAction);
			}
		}
		convertConstraints(eClass, mixin);
		return eClass;
	}

	private Resource createAndLoadOCCIEcoreResource(String pathToDDLEcore) {
		// creating a proper URI is vitally important since this is how
		// referenced objects in the is ecore file will be found from the ecore
		// file that we produce.
		ResourceSet resSet = new ResourceSetImpl();
		final URI uri = URI.createPlatformPluginURI(pathToDDLEcore, true);
		Resource res = resSet.createResource(uri);
		try {
			res.load(Collections.emptyMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	private void addSuperType(EClass subClass, String esuperClassName) {
		Resource oCCIEcoreResource = createAndLoadOCCIEcoreResource("org.eclipse.cmf.occi.core/model/OCCI.ecore");
		// of course, in production code we would fail here if there were no
		// contents or they weren't of type EPackage.
		final EPackage oCCIEPackage = (EPackage) oCCIEcoreResource.getContents().get(0);
		final EClass eSuperClass = (EClass) oCCIEPackage.getEClassifier(esuperClassName);
		subClass.getESuperTypes().add(eSuperClass);
	}

	private EEnum createEnumerationType(EnumerationType type) {
		EEnum eenum = EcoreFactory.eINSTANCE.createEEnum();
		eenum.setName(type.getName());
		int value = 0;
		for (EnumerationLiteral literal : type.getLiterals()) {
			EEnumLiteral eenumliteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
			eenum.getELiterals().add(eenumliteral);
			eenumliteral.setName(literal.getName());
			eenumliteral.setLiteral(literal.getName());
			eenumliteral.setValue(value);
			value++;
			if (literal.getDocumentation() != null) {
				attachInfo(eenumliteral, literal.getDocumentation());
			}
		}
		if (type.getDocumentation() != null) {
			attachInfo(eenum, type.getDocumentation());
		}
		return eenum;
	}
	
	private EDataType createEObjectType(EObjectType type) {
		EDataType edatatype = EcoreFactory.eINSTANCE.createEDataType();
		edatatype.setName(type.getName());
		edatatype.setInstanceTypeName(type.getInstanceClassName());
		if (type.getDocumentation() != null) {
			attachInfo(edatatype, type.getDocumentation());
		}
		return edatatype;
	}
	
	private EDataType createBooleanType(BooleanType type) {
		EDataType edatatype = EcoreFactory.eINSTANCE.createEDataType();
		edatatype.setName(type.getName());
		edatatype.setInstanceTypeName("java.lang.Boolean");
		if (type.getDocumentation() != null) {
			attachInfo(edatatype, type.getDocumentation());
		}
		return edatatype;
	}

	private EDataType createNumericType(NumericType type) {
		EDataType edatatype = EcoreFactory.eINSTANCE.createEDataType();
		edatatype.setName(type.getName());
		switch (type.getType()) {
		case BYTE:
			edatatype.setInstanceTypeName("java.lang.Byte");
			break;
		case DOUBLE:
			edatatype.setInstanceTypeName("java.lang.Double");
			break;
		case FLOAT:
			edatatype.setInstanceTypeName("java.lang.Float");
			break;
		case INTEGER:
			edatatype.setInstanceTypeName("java.lang.Integer");
			break;
		case LONG:
			edatatype.setInstanceTypeName("java.lang.Long");
			break;
		case SHORT:
			edatatype.setInstanceTypeName("java.lang.Short");
			break;
		}
		if (type.isSetTotalDigits() 
				|| type.isSetMinInclusive() 
				|| type.isSetMinExclusive() 
				|| type.isSetMaxInclusive()
				|| type.isSetMaxExclusive()) {
			EAnnotation eannotation = EcoreFactory.eINSTANCE.createEAnnotation();
			edatatype.getEAnnotations().add(eannotation);
			eannotation.setSource("http:///org/eclipse/emf/ecore/util/ExtendedMetaData");
			if (type.isSetTotalDigits())
				eannotation.getDetails().put("totalDigits", Integer.toString(type.getTotalDigits()));
			if (type.isSetMinInclusive())
				eannotation.getDetails().put("MinInclusive", type.getMinInclusive());
			if (type.isSetMinExclusive())
				eannotation.getDetails().put("MinExclusive", type.getMinExclusive());
			if (type.isSetMaxInclusive())
				eannotation.getDetails().put("MaxInclusive", type.getMaxInclusive());
			if (type.isSetMaxExclusive())
				eannotation.getDetails().put("MaxExclusive", type.getMaxExclusive());
		}
		if (type.getDocumentation() != null) {
			attachInfo(edatatype, type.getDocumentation());
		}
		return edatatype;
	}

	private EDataType createStringType(StringType type) {
		EDataType edatatype = EcoreFactory.eINSTANCE.createEDataType();
		edatatype.setName(type.getName());
		edatatype.setInstanceTypeName("java.lang.String");
		if (type.getDocumentation() != null) {
			attachInfo(edatatype, type.getDocumentation());
		}
		if (type.isSetLength() || type.isSetMaxLength() || type.isSetMinLength() || type.getPattern() != null) {
			EAnnotation eannotation = EcoreFactory.eINSTANCE.createEAnnotation();
			edatatype.getEAnnotations().add(eannotation);
			eannotation.setSource("http:///org/eclipse/emf/ecore/util/ExtendedMetaData");
			if (type.isSetLength())
				eannotation.getDetails().put("length", Integer.toString(type.getLength()));
			if (type.isSetMaxLength())
				eannotation.getDetails().put("maxLength", Integer.toString(type.getMaxLength()));
			if (type.isSetMinLength())
				eannotation.getDetails().put("minLength", Integer.toString(type.getMinLength()));
			if (type.getPattern() != "")
				eannotation.getDetails().put("pattern", type.getPattern());
		}
		return edatatype;
	}
	private EClass createArrayType(ArrayType arrayType) {
		System.out.println("maaaaap "+occiType2emfType);
		EClass type = EcoreFactory.eINSTANCE.createEClass();
		type.setName(arrayType.getName());
		if (arrayType.getDocumentation() != null) {
			attachInfo(type, arrayType.getDocumentation());
		}
		return type;
	}
	private void completeArrayTypeFeatures(ArrayType arrayType, EClass type, EPackage ePackage){
		if(! (arrayType.getType() instanceof RecordType || arrayType.getType() instanceof ArrayType)){
			// Added EClass to represent a value
			EClass aValueType = EcoreFactory.eINSTANCE.createEClass();
			aValueType.setName(arrayType.getName()+"Value");
			EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
			aValueType.getEStructuralFeatures().add(attribute);
			attribute.setName("value");
			attribute.setLowerBound(1);
			attribute.setEType(getMappedType(arrayType.getType()));
			
			//ArraType refers to aValueType
			EReference values = EcoreFactory.eINSTANCE.createEReference();
			type.getEStructuralFeatures().add(values);
			values.setName(arrayType.getName().toLowerCase()+"Values");
			values.setUpperBound(-1);
			values.setEType(aValueType);
			values.setContainment(true);
			
			ePackage.getEClassifiers().add(aValueType);
		}
		else{
			//ArraType refers to aValueType
			EReference values = EcoreFactory.eINSTANCE.createEReference();
			type.getEStructuralFeatures().add(values);
			values.setName(arrayType.getName().toLowerCase()+"Values");
			values.setUpperBound(-1);
			values.setEType(getMappedType(arrayType.getType()));
			values.setContainment(true);
		}
		
	}

	private EClass createRecordType(RecordType recordType) {
		System.out.println("maaaaap "+occiType2emfType);
		EClass type = EcoreFactory.eINSTANCE.createEClass();
		type.setName(recordType.getName());
		if (recordType.getDocumentation() != null) {
			attachInfo(type, recordType.getDocumentation());
		}
		return type;}
	
	private void completeRecordTypeFeatures(RecordType recordType, EClass type){
		
		for(RecordField rf : recordType.getRecordFields()){
			if(!(rf.getType() instanceof RecordType || rf.getType() instanceof ArrayType)){
				EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
				type.getEStructuralFeatures().add(attribute);
				attribute.setName(rf.getName());
				attribute.setLowerBound(1);
				attribute.setEType(getMappedType(rf.getType()));
			}
			else
			{
				EReference reference = EcoreFactory.eINSTANCE.createEReference();
				type.getEStructuralFeatures().add(reference);
				reference.setName(rf.getName());
				reference.setLowerBound(1);
				reference.setUpperBound(1);
				reference.setEType(getMappedType(rf.getType()));
				reference.setContainment(true);
			}
		}
	}
	
	/**
	 * Convert an OCCI kind to an Ecore class.
	 * 
	 * @param kind
	 *            the OCCI kind to convert.
	 * @return the resulting Ecore class.
	 */
	protected EClass convertKind(Kind kind) {
		// Create the Ecore class.
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		// Set the name of the Ecore class.
		eClass.setName(ConverterUtils.toU1Case(ConverterUtils.formatName(kind.getTerm())));
		// Convert all attributes of the OCCI kind.
		for (Attribute attribute : kind.getAttributes()) {
			// Convert each OCCI attribute to an Ecore attribute.
			EAttribute convertAttribute = convertAttribute(attribute);
			if (convertAttribute != null) {
				// Add the Ecore attribute as a structural feature of the Ecore
				// class.
				eClass.getEStructuralFeatures().add(convertAttribute);
			}
		}
		// Convert all actions of the OCCI kind.
		for (Action action : kind.getActions()) {
			// Convert each OCCI action to an Ecore operation.
			EOperation convertAction = convertAction(action);
			if (convertAction != null) {
				// Add the Ecore action as an operation of the Ecore class.
				eClass.getEOperations().add(convertAction);
			}
		}
		attachInfo(eClass, kind.getTitle());
		// Keep the Ecore class into a cache to search it later.
		occiKind2emfEclass.put(kind, eClass);
		
		
		// Convert all constraints of the OCCI kind.
		convertConstraints(eClass, kind);
		
		return eClass;
	}
	protected void convertConstraints(EClass eClass, Type type){
		if(type.getConstraints().size()>0){
			//EMF Annotation
			EAnnotation annotation_emf = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation_emf.setSource("http://www.eclipse.org/emf/2002/Ecore");
			String value ="";
			//OCL Annotation
			EAnnotation annotation_ocl = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation_ocl.setSource("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot");
			for (Constraint constraint: type.getConstraints()){
				annotation_ocl.getDetails().put(constraint.getName(), convertbody(constraint.getBody(), (Extension)type.eContainer()) );
				if(value.equals("")){
					value+= constraint.getName();
				}
				else{
					value+= " ";
					value+=constraint.getName();
				}
				convertbody(constraint.getBody(), (Extension)type.eContainer());
			}
			annotation_emf.getDetails().put("constraints", value);
			eClass.getEAnnotations().add(annotation_emf);
			eClass.getEAnnotations().add(annotation_ocl);
			

		}
	}
	
	public String convertbody(String body, Extension extension)
	{
		List<EObject> attributesList = Lists.newArrayList(extension.eAllContents()).stream().filter(eobject-> eobject instanceof Attribute).collect(Collectors.toList());
		HashMap<String, String> attributes = new HashMap<String, String>();
		for (EObject attribute : attributesList){
			attributes.put(((Attribute)attribute).getName(), OCCI2Ecore.convertOcciAttributeName2EcoreAttributeName(((Attribute)attribute).getName()));
		}
		 for (Map.Entry<String, String> entry : attributes.entrySet()) {
		        body = body.replace(entry.getKey(), entry.getValue());
		    }
		System.out.println("bodyyy  "+body);
		return body;
	}
	
	/**
	 * Convert an OCCI action to an Ecore operation.
	 * 
	 * @param action
	 *            the OCCI action to convert.
	 * @return the resulting Ecore operation.
	 */
	protected EOperation convertAction(Action action) {
		// Create the Ecore operation.
		EOperation eOperation = EcoreFactory.eINSTANCE.createEOperation();
		// Set the name of the Ecore operation.
		eOperation.setName(ConverterUtils.formatName(action.getTerm()));
		// Convert all attributes of the OCCI action.
		for (Attribute attribute : action.getAttributes()) {
			// Each OCCI attribute of the OCCI action is converted to an Ecore
			// parameter of the Ecore operation.
			EParameter convertParameter = convertParameter(attribute);
			if (convertParameter != null) {
				// Add the Ecore parameter to the Ecore operation.
				eOperation.getEParameters().add(convertParameter);
			}
		}
		attachInfo(eOperation, action.getTitle());

		return eOperation;
	}

	/**
	 * Convert an OCCI action's attribute to an Ecore operation parameter.
	 * 
	 * @param attribute
	 *            the OCCI attribute to convert.
	 * @return the resulting Ecore operation parameter.
	 */
	protected EParameter convertParameter(Attribute attribute) {
		// Create an Ecore parameter.
		EParameter eParam = EcoreFactory.eINSTANCE.createEParameter();
		// Set the name of the Ecore parameter.
		eParam.setName(OCCI2Ecore.convertOcciAttributeName2EcoreAttributeName(attribute.getName()));

		// Set the type of the Ecore parameter.
		eParam.setEType(getMappedType(attribute.getType()));

		// If the OCCI attribute is required then the Ecore parameter is also
		// required.
		if (attribute.isRequired()) {
			eParam.setLowerBound(1);
		}
		attachInfo(eParam, attribute.getDescription());

		
		return eParam;
	}

	/**
	 * Convert an OCCI attribute to an Ecore attribute.
	 * 
	 * @param attribute
	 *            the OCCI attribute to convert.
	 * @return the resulting Ecore attribute.
	 */
	protected EAttribute convertAttribute(Attribute attribute) {
		// Create an Ecore attribute.
		EAttribute eAttr = EcoreFactory.eINSTANCE.createEAttribute();
		// Set the name of the Ecore attribute.
		eAttr.setName(OCCI2Ecore.convertOcciAttributeName2EcoreAttributeName(attribute.getName()));
		// Set the type of the Ecore attribute.

		eAttr.setEType(getMappedType(attribute.getType()));

		// Set the default value of the Ecore attribute.
		String defaultValue = attribute.getDefault();
		if (defaultValue != null && !defaultValue.isEmpty()) {
			eAttr.setDefaultValue(defaultValue);
		}
		// The Ecore attribute is required when the OCCI attribute is required.
		if (attribute.isRequired()) {
			eAttr.setLowerBound(1);
		}
		attachInfo(eAttr, attribute.getDescription());

		// TODO: setUpperBound(-1) if attribute.multiple_value
		return eAttr;
	}

	private void attachInfo(EModelElement element, String value) {

			EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
			annotation.setSource("http://www.eclipse.org/emf/2002/GenModel");
			element.getEAnnotations().add(annotation);
		if(value != null) 
			annotation.getDetails().put("documentation", value);
		else
			annotation.getDetails().put("documentation", "");
	}
}