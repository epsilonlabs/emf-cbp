package org.eclipse.epsilon.cbp.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.context.PersistenceManager;
import org.eclipse.epsilon.cbp.exceptions.UnknownPackageException;
import org.eclipse.epsilon.cbp.util.Changelog;
import org.eclipse.epsilon.cbp.util.ModelElementIDMap;
import org.eclipse.epsilon.cbp.util.PrimitiveTypeLength;
import org.eclipse.epsilon.cbp.util.SerialisationEventType;
import org.eclipse.epsilon.cbp.util.SimpleType;

import gnu.trove.map.TObjectIntMap;

public class CBPBinaryDeserializer 
{
	private EPackage ePackage = null;
	private final Changelog changelog;
	
	private final HashMap<Integer, EObject> IDToEObjectMap = new HashMap<Integer, EObject>();
	
	private final PersistenceManager manager;
    private final ModelElementIDMap ePackageElementsNamesMap;
    private final Charset STRING_ENCODING = StandardCharsets.UTF_8;
    
    private final TObjectIntMap<String> commonSimpleTypeNameMap;

    public CBPBinaryDeserializer(PersistenceManager manager, Changelog aChangelog,
            ModelElementIDMap ePackageElementsNamesMap)
    {
    	 this.manager = manager;
         this.changelog = aChangelog;
         this.ePackageElementsNamesMap = ePackageElementsNamesMap;
         
         this.commonSimpleTypeNameMap = manager.getCommonSimpleTypesMap();
    }
    
    public void load(Map<?,?> options) throws Exception
    { 
    	InputStream inputStream = new BufferedInputStream
				(new FileInputStream(manager.getURI().path()));
		
    	/*Read File Header*/
		 inputStream.skip(11); // skip file header
		
		/*Load metamodel*/
		int nsUriLength = readInt(inputStream);
		loadMetamodel(readString(inputStream,nsUriLength));
		
		/*Read binary records*/
		while(inputStream.available()> 0)
		{
			switch (readInt(inputStream))
			{
			case SerialisationEventType.CREATE_AND_ADD_TO_RESOURCE:
				handleCreateAndAddToResource(inputStream);
				break;
			case SerialisationEventType.CREATE_AND_SET_EREFERENCE:
				handeCreateAndSetEReferenceValue(inputStream);
				break;
			case SerialisationEventType.ADD_TO_RESOURCE:
				handleAddToResource(inputStream);
				break;
			case SerialisationEventType.REMOVE_FROM_RESOURCE:
				handleRemoveFromResource(inputStream);
				break;
			case SerialisationEventType.SET_EREFERENCE:
				handleEReferenceEvent(inputStream,true);
				break;
			case SerialisationEventType.UNSET_EREFERENCE:
				handleEReferenceEvent(inputStream,false);
				break;
			case SerialisationEventType.SET_EATTRIBUTE_COMPLEX:
				handleComplexEAttributeType(inputStream,true);
				break;
			case SerialisationEventType.UNSET_EATTRIBUTE_COMPLEX:
				handleComplexEAttributeType(inputStream,false);
				break;
			case SerialisationEventType.SET_EATTRIBUTE_PRIMITIVE:
				handlePrimitiveEAttributeType(inputStream, true);
				break;
			case SerialisationEventType.UNSET_EATTRIBUTE_PRIMITIVE:
				handlePrimitiveEAttributeType(inputStream,false);
				break;
			}
		}
		
		inputStream.close();	
    }
    
    private int getTypeID(EDataType type)
    {
    	if(commonSimpleTypeNameMap.containsKey(type.getName()))
    	{
    		return commonSimpleTypeNameMap.get(type.getName());
    	}
    	return SimpleType.COMPLEX_TYPE;
    }
    
    private void setPrimitiveEAttributeValues(EObject focusObject, EAttribute eAttribute, Object[] featureValuesArray)
    {
		if(eAttribute.isMany())
		{
			@SuppressWarnings("unchecked")
	        EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);  
			
			for(Object obj : featureValuesArray)
			{
				featureValuesList.add(obj);
			}
		}
		else
		{
			focusObject.eSet(eAttribute,featureValuesArray[0]);
		}
    }
    
    private void unsetPrimitiveEAttributeValues(EObject focusObject, EAttribute eAttribute,Object[] featureValuesArray)
    {
		if(eAttribute.isMany())
		{
			@SuppressWarnings("unchecked")
	        EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);  
			
			for(Object obj : featureValuesArray)
			{
				featureValuesList.remove(obj);
			}
		}
		else
		{
			focusObject.eUnset(eAttribute);
		}
    }
    
    private void setComplexEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray)
    {
    	EDataType eDataType = eAttribute.getEAttributeType();
    	
		if(eAttribute.isMany())
		{
			@SuppressWarnings("unchecked")
	        EList<Object> featureValueList = (EList<Object>) focusObject.eGet(eAttribute);  
			
			for(String str : featureValuesArray)
			{
				if(str.equals(manager.NULL_STRING))
					featureValueList.add(null);
				
				else if (eDataType.getName().equals("EString"))
						featureValueList.add(str);
				else
					featureValueList.add(EcoreUtil.createFromString(eDataType,str));
			}
		}
		else
		{
			String str = featureValuesArray[0];
			
			if(str.equals(manager.NULL_STRING))
				focusObject.eSet(eAttribute,null);
			
			else if (eDataType.getName().equals("EString"))
				focusObject.eSet(eAttribute,str);
			
			else
				focusObject.eSet(eAttribute,EcoreUtil.createFromString(eDataType, str));
		}
    }
    
    private void unsetComplexEAttributeValues(EObject focusObject, EAttribute eAttribute, String[] featureValuesArray)
    {
    	EDataType eDataType = eAttribute.getEAttributeType();
    	
		if(eAttribute.isMany())
		{
			@SuppressWarnings("unchecked")
	        EList<Object> featureValueList = (EList<Object>) focusObject.eGet(eAttribute);  
			
			for(String str : featureValuesArray)
			{
				if(str.equals(manager.NULL_STRING))
					featureValueList.remove(null);
				
				else if (eDataType.getName().equals("EString"))
						featureValueList.remove(str);
				else
					featureValueList.remove(EcoreUtil.createFromString(eDataType,str));
			}
		}
		else
		{
			focusObject.eUnset(eAttribute);
		}
    }
    
    private void setPrimitiveBooleanEAttributeValues(EObject focusObject, EAttribute eAttribute, Object[] featureValuesArray)
    {
    	boolean b = true;
		
		if(eAttribute.isMany())
		{
			@SuppressWarnings("unchecked")
            EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);  
			
			for(Object obj : featureValuesArray)
			{
				if((int)obj == 0)
					b = false;
				
				featureValuesList.add(b);
			}
		}
		else
		{
			focusObject.eSet(eAttribute,b);
		}
    }
    
    private void unsetPrimitiveBooleanEAttributeValues(EObject focusObject, EAttribute eAttribute, Object[] featureValuesArray)
    {
    	boolean b = true;
		
		if(eAttribute.isMany())
		{
			@SuppressWarnings("unchecked")
            EList<Object> featureValuesList = (EList<Object>) focusObject.eGet(eAttribute);  
			
			for(Object obj : featureValuesArray)
			{
				if((int)obj == 0)
					b = false;
				
				featureValuesList.remove(b);
			}
		}
		else
		{
			focusObject.eUnset(eAttribute);
		}
    }
   
    
    private void handlePrimitiveEAttributeType(InputStream in, boolean isSetEAttribute) throws IOException
    {
    	EObject focusObject = IDToEObjectMap.get(readInt(in));
    	
    	EAttribute eAttribute = (EAttribute) focusObject.eClass().getEStructuralFeature
				(ePackageElementsNamesMap.getName(readInt(in)));
    	
    	EDataType eDataType = eAttribute.getEAttributeType();
    	
    	int primitiveSize = -1;
    	int primitiveType = -1;
    	
    	switch(getTypeID(eDataType))
    	{
    	case SimpleType.SIMPLE_TYPE_INT:
    		primitiveType = SimpleType.SIMPLE_TYPE_INT;
    		primitiveSize = PrimitiveTypeLength.INTEGER_SIZE;
    		break;
    	case SimpleType.SIMPLE_TYPE_SHORT:
    		primitiveType = SimpleType.SIMPLE_TYPE_SHORT;
    		primitiveSize = PrimitiveTypeLength.SHORT_SIZE;
    		break;
    	case SimpleType.SIMPLE_TYPE_LONG:
    		primitiveType = SimpleType.SIMPLE_TYPE_LONG;
    		primitiveSize = PrimitiveTypeLength.LONG_SIZE;
    		break;
    	case SimpleType.SIMPLE_TYPE_FLOAT:
    		primitiveType = SimpleType.SIMPLE_TYPE_FLOAT;
    		primitiveSize = PrimitiveTypeLength.FLOAT_SIZE;
    		break;
    	case SimpleType.SIMPLE_TYPE_DOUBLE:
    		primitiveType = SimpleType.SIMPLE_TYPE_DOUBLE;
    		primitiveSize = PrimitiveTypeLength.DOUBLE_SIZE;
    		break;
    	case SimpleType.SIMPLE_TYPE_BOOLEAN:
    		primitiveType = SimpleType.SIMPLE_TYPE_BOOLEAN;
    		primitiveSize = PrimitiveTypeLength.INTEGER_SIZE;
    		break;
    	case SimpleType.SIMPLE_TYPE_CHAR:
    		primitiveType = SimpleType.SIMPLE_TYPE_CHAR;
    		primitiveSize = PrimitiveTypeLength.CHAR_SIZE;
    		return;
    	}
    	
    	int numPrimitives = readInt(in);
    	
    	Object[] featureValuesArray = new Object[numPrimitives];
    	
    	byte[] buffer = new byte[numPrimitives * primitiveSize];
    	
    	in.read(buffer);
    	
    	int index = 0;
    	
    	for(int i = 0; i < numPrimitives; i++)
    	{
    		featureValuesArray[i] = byteArrayToPrimitive(Arrays.copyOfRange(buffer, index, index+primitiveSize),primitiveType);
    		
    		index = index + primitiveSize;
    	}
    	
    	if(isSetEAttribute)
    	{
    		if(primitiveType == SimpleType.SIMPLE_TYPE_BOOLEAN)
    			setPrimitiveBooleanEAttributeValues(focusObject,eAttribute,featureValuesArray);
    		
    		else
    			setPrimitiveEAttributeValues(focusObject,eAttribute,featureValuesArray);
    	}
    	else
    	{
    		if(primitiveType == SimpleType.SIMPLE_TYPE_BOOLEAN)
    			unsetPrimitiveBooleanEAttributeValues(focusObject,eAttribute,featureValuesArray);
    		
    		else
    			unsetPrimitiveEAttributeValues(focusObject,eAttribute,featureValuesArray);
    	}
    }

    private void handleComplexEAttributeType(InputStream in, boolean isSetEAttribute) throws IOException
    {
    	EObject focusObject = IDToEObjectMap.get(readInt(in));
    	
    	EAttribute eAttribute = (EAttribute) focusObject.eClass().getEStructuralFeature
				(ePackageElementsNamesMap.getName(readInt(in)));
    
    	int numStrings = readInt(in);
    	
    	String[] featureValuesArray = new String[numStrings];
    	
    	for(int i = 0; i < numStrings; i++)
    	{
    		int numBytes = readInt(in);
    		
    		featureValuesArray[i] = readString(in,numBytes);
    	}
    	
    	if(isSetEAttribute)
    		setComplexEAttributeValues(focusObject,eAttribute,featureValuesArray);
    	
    	else
    		unsetComplexEAttributeValues(focusObject,eAttribute,featureValuesArray);	
    }
    
    private void handleEReferenceEvent(InputStream in, boolean isSetEReference) throws IOException
    {
    	EObject focusObject = IDToEObjectMap.get(readInt(in));
    	
    	
    	
    	EReference eReference = (EReference) focusObject.eClass().getEStructuralFeature
				(ePackageElementsNamesMap.getName(readInt(in)));
    	
    	int numInts = readInt(in);
    	
    	byte[] buffer = new byte[numInts * 4];
    	
    	in.read(buffer);
    	
    	int[] intArray = new int[numInts]; //stores 'n' numbers
    	
    	int index = 0;
    	
    	for(int i = 0; i < numInts; i++)
    	{
    		intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index,index+4));
    		
    		index = index + 4;
    	}
    	
    	if(isSetEReference)
    		setEReferenceValues(focusObject,eReference,intArray);
    	
    	else
    		unsetEReferenceValues(focusObject,eReference,intArray);
    		
    }
    
    private void handleRemoveFromResource(InputStream in) throws IOException
    {
    	int numInts = readInt(in);
    	
    	byte[] buffer = new byte[numInts * 4];
    	
    	in.read(buffer);
    	
    	int index = 0;
    	
    	for(int i = 0; i < numInts; i++)
    	{
    		int id = byteArrayToInt(Arrays.copyOfRange(buffer, index,index+4));
    		
    		manager.removeEObjectFromContents(IDToEObjectMap.get(id));
    		
    		index = index + 4;
    	}
    }
    
    private void setEReferenceValues(EObject focusObject, EReference eReference, int[] eObjectIDArray)
    {
    	if(eReference.isMany())
    	{
    		@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);
    		
    		for(int i : eObjectIDArray)
    		{
    			featureValuesList.add(IDToEObjectMap.get(i));
    		}
    	}
    	else
    	{
    		focusObject.eSet(eReference, IDToEObjectMap.get(eObjectIDArray[0]));
    	}
    }
    
    private void unsetEReferenceValues(EObject focusObject, EReference eReference, int[] eObjectIDArray)
    {
    	if(eReference.isMany())
    	{
    		@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(eReference);
    		
    		for(int i : eObjectIDArray)
    		{
    			featureValuesList.remove(IDToEObjectMap.get(i));
    		}
    	}
    	else
    	{
    		focusObject.eUnset(eReference);
    	}
    }
    
    private void handleCreateAndAddToResource(InputStream in) throws IOException
    {
    	int numInts = readInt(in);
    	
    	byte[] buffer = new byte[numInts * 4]; //stores bytes for all 'n' numbers
    	
    	in.read(buffer);
    	
    	int[] intArray = new int[numInts]; //stores 'n' numbers
    	
    	int index = 0;
    	
    	for(int i = 0; i < numInts; i++)
    	{
    		intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index,index+4));
    		index = index + 4;
    	}
    	
    	index = 0;
    	
    	for(int i = 0; i < (numInts / 2); i++)
    	{
    		EObject obj = createEObject(ePackageElementsNamesMap.getName(intArray[index]));
    		
    		int id = intArray[index+1];
    		
    		index = index + 2;
    		
    		changelog.addObjectToMap(obj, id);
    		IDToEObjectMap.put(id,obj);
    		
    		manager.addEObjectToContents(obj);
    	}
    }
    
    private void handeCreateAndSetEReferenceValue(InputStream in) throws IOException
    {
    	EObject focusObject = IDToEObjectMap.get(readInt(in));
    	
    	EReference ref = (EReference) focusObject.eClass().getEStructuralFeature
                (ePackageElementsNamesMap.getName(readInt(in)));
    	
    	int numInts = readInt(in);
    	
    	byte[] buffer = new byte[numInts * 4]; //stores 'n' numbers
    	
    	in.read(buffer); //read in bytes for 'n' numbers
    	
    	int[] intArray = new int[numInts]; //stores 'n' numbers
    	
    	int index = 0;
    	
    	for(int i = 0; i < numInts; i++)
    	{
    		intArray[i] = byteArrayToInt(Arrays.copyOfRange(buffer, index,index+4));
    		
    		index = index + 4;
    	}
    	
    	index = 0;
    	
    	for(int i = 0; i < (numInts / 2); i++)
    	{
    		EObject obj = createEObject(ePackageElementsNamesMap.getName(intArray[index]));
    		
    		int id = intArray[index + 1];
    		
    		index = index + 2;
    		
    		changelog.addObjectToMap(obj, id);
   
    		IDToEObjectMap.put(id,obj);
    	}
    	
    	if(ref.isMany())
    	{
    		@SuppressWarnings("unchecked")
			EList<EObject> featureValuesList = (EList<EObject>) focusObject.eGet(ref);
    		
    		for(int i = 1; i < numInts; i = i + 2)
    		{
    			featureValuesList.add(IDToEObjectMap.get(intArray[i]));
    		}
    	}
    	else
    	{
    		focusObject.eSet(ref, IDToEObjectMap.get(intArray[1]));
    	}
    }
    
    private void handleAddToResource(InputStream in) throws IOException
    {
    	int numInts = readInt(in);
    	
    	byte[] buffer = new byte[numInts * 4]; //stores 'n' numbers
    	
    	in.read(buffer); //read in bytes for 'n' numbers
    	
    	int startIndex = 0;
    	
    	for(int i = 0; i < numInts; i++)
    	{
    		int id = byteArrayToInt(Arrays.copyOfRange(buffer, startIndex,startIndex + PrimitiveTypeLength.INTEGER_SIZE));
    		
    		startIndex = startIndex + PrimitiveTypeLength.INTEGER_SIZE;
    		
    		manager.addEObjectToContents(IDToEObjectMap.get(id));
    	}
    }
    
    private void loadMetamodel(String metamodelURI) throws UnknownPackageException
    {
    	
        if(EPackage.Registry.INSTANCE.containsKey(metamodelURI))
            ePackage = EPackage.Registry.INSTANCE.getEPackage(metamodelURI);
        
        else
            throw new UnknownPackageException(metamodelURI);
        
    }
    
    private EObject createEObject(String eClassName) 
	{
		return ePackage.getEFactoryInstance().create((EClass)
				ePackage.getEClassifier(eClassName));
	}
    
    private String readString(InputStream in, int length) throws IOException
    {
    	byte[] bytes = new byte[length];
    	in.read(bytes);
    	
    	return new String(bytes, STRING_ENCODING);
    }
    
    private Object byteArrayToPrimitive(byte[] bytes,int primitiveType)
    {
    	switch(primitiveType)
    	{
    	case SimpleType.SIMPLE_TYPE_BOOLEAN:
    		return byteArrayToInt(bytes);
    	case SimpleType.SIMPLE_TYPE_BYTE:
    		return byteArrayToByte(bytes);
    	case SimpleType.SIMPLE_TYPE_CHAR:
    		return byteArrayToChar(bytes);
    	case SimpleType.SIMPLE_TYPE_DOUBLE:
    		return byteArrayToDouble(bytes);
    	case SimpleType.SIMPLE_TYPE_FLOAT:
    		return byteArrayToFloat(bytes);
    	case SimpleType.SIMPLE_TYPE_LONG:
    		return byteArrayToLong(bytes);
    	case SimpleType.SIMPLE_TYPE_INT:
    		return byteArrayToInt(bytes);
    	case SimpleType.SIMPLE_TYPE_SHORT:
    		return byteArrayToShort(bytes);
    	}
    	return null;
    }
    
    private int byteArrayToInt(byte[] bytes)
    {
    	return ByteBuffer.wrap(bytes).getInt();
    }
    
    private byte byteArrayToByte(byte[] bytes)
    {
    	return ByteBuffer.wrap(bytes).get();
    }
    
    private char byteArrayToChar(byte[] bytes)
    {
    	return ByteBuffer.wrap(bytes).getChar();
    }
    
    private double byteArrayToDouble(byte[] bytes)
    {
    	return ByteBuffer.wrap(bytes).getDouble();
    }
    
    private float byteArrayToFloat(byte[] bytes)
    {
    	return ByteBuffer.wrap(bytes).getFloat();
    }
    
    private long byteArrayToLong(byte[] bytes)
    {
    	return ByteBuffer.wrap(bytes).getLong();
    }
    
    private short byteArrayToShort(byte[] bytes)
    {
    	return ByteBuffer.wrap(bytes).getShort();
    }
    
    private int readInt(InputStream in) throws IOException
    {
    	byte[] bytes = new byte[PrimitiveTypeLength.INTEGER_SIZE];
    	in.read(bytes);
    	
    	return ByteBuffer.wrap(bytes).getInt();
    }
}
