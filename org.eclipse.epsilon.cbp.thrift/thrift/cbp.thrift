namespace java org.eclipse.epsilon.cbp.resource.thrift.structs


union EObjectReference {
	 /*  */ 1: optional i64 id,
	 /*  */ 2: optional string xref,
}

struct TAddToEAttributeEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
	 /*  */ 3: required list<string> values,
	 /*  */ 4: required i32 position,
}

struct TCreateEObjectEvent {
	 /*  */ 1: required string ePackage,
	 /*  */ 2: required string eClass,
	 /*  */ 3: required i64 id,
}

struct TMoveWithinEAttributeEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
	 /*  */ 3: required i32 fromPosition,
	 /*  */ 4: required i32 toPosition,
}

struct TMoveWithinEReferenceEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
	 /*  */ 3: required i32 fromPosition,
	 /*  */ 4: required i32 toPosition,
}

struct TRegisterEPackageEvent {
	 /*  */ 1: required string ePackage,
}

struct TRemoveFromEAttributeEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
	 /*  */ 3: required list<string> values,
	 /*  */ 4: required i32 position,
}

struct TSetEAttributeEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
	 /*  */ 3: required list<string> values,
}

struct TUnsetEAttributeEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
}

struct TUnsetEReferenceEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
}

struct TAddToEReferenceEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
	 /*  */ 3: required list<EObjectReference> values,
	 /*  */ 4: required i32 position,
}

struct TAddToResourceEvent {
	 /*  */ 1: required list<EObjectReference> values,
	 /*  */ 2: required i32 position,
}

struct TRemoveFromEReferenceEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
	 /*  */ 3: required list<EObjectReference> values,
	 /*  */ 4: required i32 position,
}

struct TRemoveFromResourceEvent {
	 /*  */ 1: required list<EObjectReference> values,
	 /*  */ 2: required i32 position,
}

struct TSetEReferenceEvent {
	 /*  */ 1: required string name,
	 /*  */ 2: required i64 target,
	 /*  */ 3: required list<EObjectReference> values,
}

union TChangeEvent {
	 /*  */ 1: optional TCreateEObjectEvent createEObject,
	 /*  */ 2: optional TAddToEAttributeEvent addToEAttribute,
	 /*  */ 3: optional TSetEAttributeEvent setEAttribute,
	 /*  */ 4: optional TMoveWithinEAttributeEvent moveWithinEAttribute,
	 /*  */ 5: optional TRemoveFromEAttributeEvent removeFromEAttribute,
	 /*  */ 6: optional TUnsetEAttributeEvent unsetEAttribute,
	 /*  */ 7: optional TAddToEReferenceEvent addToEReference,
	 /*  */ 8: optional TMoveWithinEReferenceEvent moveWithinEReference,
	 /*  */ 9: optional TRemoveFromEReferenceEvent removeFromEReference,
	 /*  */ 10: optional TSetEReferenceEvent setEReference,
	 /*  */ 11: optional TUnsetEReferenceEvent unsetEReference,
	 /*  */ 12: optional TRegisterEPackageEvent registerEPackage,
	 /*  */ 13: optional TAddToResourceEvent addToResource,
	 /*  */ 14: optional TRemoveFromResourceEvent removeFromResource,
}

