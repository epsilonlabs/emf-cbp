package org.eclipse.epsilon.cbp.thrift;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransportException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.DefaultChangeEventVisitor;
import org.eclipse.epsilon.cbp.event.EObjectValuesEvent;
import org.eclipse.epsilon.cbp.event.EStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.PrimitiveValuesEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
import org.eclipse.epsilon.cbp.resource.thrift.structs.EObjectReference;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TAddToEAttributeEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TAddToEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TAddToResourceEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TChangeEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TCreateEObjectEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TMoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TMoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TRegisterEPackageEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TRemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TRemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TRemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TSetEAttributeEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TSetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TUnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.resource.thrift.structs.TUnsetEReferenceEvent;

public class CBPThriftResourceImpl extends CBPResource {

	public class SaveChangeEventVisitor extends DefaultChangeEventVisitor<TChangeEvent> {

		@Override
		public TChangeEvent visit(AddToEAttributeEvent addToEAttributeEvent) {
			final TAddToEAttributeEvent ev = new TAddToEAttributeEvent();
			ev.setValues(getLiteralValues(addToEAttributeEvent));
			ev.setName(addToEAttributeEvent.getEStructuralFeature().getName());
			ev.setPosition(addToEAttributeEvent.getPosition());
			ev.setTarget(getTargetId(addToEAttributeEvent));
			return TChangeEvent.addToEAttribute(ev);
		}

		@Override
		public TChangeEvent visit(AddToEReferenceEvent addToEReferenceEvent) {
			final TAddToEReferenceEvent ev = new TAddToEReferenceEvent();
			ev.setValues(getEObjectValues(addToEReferenceEvent));
			ev.setName(addToEReferenceEvent.getEStructuralFeature().getName());
			ev.setPosition(addToEReferenceEvent.getPosition());
			ev.setTarget(getTargetId(addToEReferenceEvent));
			return TChangeEvent.addToEReference(ev);
		}

		@Override
		public TChangeEvent visit(AddToResourceEvent addToResourceEvent) {
			final TAddToResourceEvent ev = new TAddToResourceEvent();
			ev.setValues(getEObjectValues(addToResourceEvent));
			ev.setPosition(addToResourceEvent.getPosition());
			return TChangeEvent.addToResource(ev);
		}

		@Override
		public TChangeEvent visit(CreateEObjectEvent c) {
			TCreateEObjectEvent ev = new TCreateEObjectEvent();
			ev.setEPackage(c.getEClass().getEPackage().getNsURI());
			ev.setEClass(c.getEClass().getName());
			ev.setId(Long.valueOf(c.getId()));
			return TChangeEvent.createEObject(ev);
		}

		@Override
		public TChangeEvent visit(MoveWithinEAttributeEvent moveWithinEAttributeEvent) {
			TMoveWithinEAttributeEvent ev = new TMoveWithinEAttributeEvent();
			ev.setName(moveWithinEAttributeEvent.getEStructuralFeature().getName());
			ev.setFromPosition(moveWithinEAttributeEvent.getFromPosition());
			ev.setToPosition(moveWithinEAttributeEvent.getPosition());
			ev.setTarget(getTargetId(moveWithinEAttributeEvent));
			return TChangeEvent.moveWithinEAttribute(ev);
		}

		@Override
		public TChangeEvent visit(MoveWithinEReferenceEvent moveWithinEReferenceEvent) {
			TMoveWithinEReferenceEvent ev = new TMoveWithinEReferenceEvent();
			ev.setName(moveWithinEReferenceEvent.getEStructuralFeature().getName());
			ev.setFromPosition(moveWithinEReferenceEvent.getFromPosition());
			ev.setToPosition(moveWithinEReferenceEvent.getPosition());
			ev.setTarget(getTargetId(moveWithinEReferenceEvent));
			return TChangeEvent.moveWithinEReference(ev);
		}

		@Override
		public TChangeEvent visit(RegisterEPackageEvent r) {
			TRegisterEPackageEvent ev = new TRegisterEPackageEvent();
			ev.setEPackage(r.getEPackage().getNsURI());
			return TChangeEvent.registerEPackage(ev);
		}

		@Override
		public TChangeEvent visit(RemoveFromEAttributeEvent removeFromEAttributeEvent) {
			TRemoveFromEAttributeEvent ev = new TRemoveFromEAttributeEvent();
			ev.setName(removeFromEAttributeEvent.getEStructuralFeature().getName());
			ev.setPosition(removeFromEAttributeEvent.getPosition());
			ev.setTarget(getTargetId(removeFromEAttributeEvent));
			ev.setValues(getLiteralValues(removeFromEAttributeEvent));
			return TChangeEvent.removeFromEAttribute(ev);
		}

		@Override
		public TChangeEvent visit(RemoveFromEReferenceEvent removeFromEReferenceEvent) {
			TRemoveFromEReferenceEvent ev = new TRemoveFromEReferenceEvent();
			ev.setName(removeFromEReferenceEvent.getEStructuralFeature().getName());
			ev.setPosition(removeFromEReferenceEvent.getPosition());
			ev.setTarget(getTargetId(removeFromEReferenceEvent));
			ev.setValues(getEObjectValues(removeFromEReferenceEvent));
			return TChangeEvent.removeFromEReference(ev);
		}

		@Override
		public TChangeEvent visit(RemoveFromResourceEvent removeFromResourceEvent) {
			TRemoveFromResourceEvent ev = new TRemoveFromResourceEvent();
			ev.setValues(getEObjectValues(removeFromResourceEvent));
			ev.setPosition(removeFromResourceEvent.getPosition());
			return TChangeEvent.removeFromResource(ev);
		}

		@Override
		public TChangeEvent visit(SetEAttributeEvent setEAttributeEvent) {
			TSetEAttributeEvent ev = new TSetEAttributeEvent();
			ev.setName(setEAttributeEvent.getEStructuralFeature().getName());
			ev.setValues(getLiteralValues(setEAttributeEvent));
			ev.setTarget(getTargetId(setEAttributeEvent));
			return TChangeEvent.setEAttribute(ev);
		}

		@Override
		public TChangeEvent visit(SetEReferenceEvent setEReferenceEvent) {
			TSetEReferenceEvent ev = new TSetEReferenceEvent();
			ev.setName(setEReferenceEvent.getEStructuralFeature().getName());
			ev.setValues(getEObjectValues(setEReferenceEvent));
			ev.setTarget(getTargetId(setEReferenceEvent));
			return TChangeEvent.setEReference(ev);
		}

		@Override
		public TChangeEvent visit(UnsetEAttributeEvent unsetEAttributeEvent) {
			TUnsetEAttributeEvent ev = new TUnsetEAttributeEvent();
			ev.setName(unsetEAttributeEvent.getEStructuralFeature().getName());
			ev.setTarget(getTargetId(unsetEAttributeEvent));
			return TChangeEvent.unsetEAttribute(ev);
		}

		@Override
		public TChangeEvent visit(UnsetEReferenceEvent unsetEReferenceEvent) {
			TUnsetEReferenceEvent ev = new TUnsetEReferenceEvent();
			ev.setName(unsetEReferenceEvent.getEStructuralFeature().getName());
			ev.setTarget(getTargetId(unsetEReferenceEvent));
			return TChangeEvent.unsetEReference(ev);
		}

		protected List<EObjectReference> getEObjectValues(EObjectValuesEvent ev) {
			List<EObjectReference> refs = new ArrayList<>();
			for (EObject value : ev.getValues()) {
				String id = eObjectToIdMap.get(value);
				if (id != null) {
					refs.add(EObjectReference.id(Long.valueOf(id)));
				} else {
					refs.add(EObjectReference.xref(EcoreUtil.getURI(value).toString()));
				}
			}
			return refs;
		}

		protected List<String> getLiteralValues(PrimitiveValuesEvent ev) {
			final List<String> values = new ArrayList<>();
			for (Object value : ev.getValues()) {
				values.add(value + "");
			}
			return values;
		}

		protected <T> Long getTargetId(EStructuralFeatureEvent<T> ev) {
			return Long.valueOf(getURIFragment(ev.getTarget()));
		}

	}

	protected int persistedEvents = 0;

	public CBPThriftResourceImpl() {
		super();
	}

	public CBPThriftResourceImpl(URI uri) {
		super(uri);
	}

	protected TProtocolFactory getProtocolFactory() {
		return new TTupleProtocol.Factory();
	}

	@Override
	public void doSave(OutputStream out, Map<?, ?> options) throws IOException {
		final TIOStreamTransport transport = new TIOStreamTransport(out);
		final TProtocol protocol = getProtocolFactory().getProtocol(transport);
		try {
			SaveChangeEventVisitor loadVisitor = new SaveChangeEventVisitor();
			for (ChangeEvent<?> event : getChangeEvents().subList(persistedEvents, getChangeEvents().size())) {
				TChangeEvent chEvent = event.accept(loadVisitor);
				chEvent.write(protocol);
			}

			persistedEvents = getChangeEvents().size();
		} catch (TException ex) {
			throw new IOException(ex);
		} finally {
			transport.close();
		}
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		changeEventAdapter.setEnabled(false);
		eObjectToIdMap.clear();
		getChangeEvents().clear();

		final TIOStreamTransport transport = new TIOStreamTransport(inputStream);
		final TProtocol protocol = getProtocolFactory().getProtocol(transport);

		try {
			while (transport.peek()) {
				final TChangeEvent chEvent = new TChangeEvent();
				chEvent.read(protocol);

				ChangeEvent<?> newEvent = buildEvent(chEvent);
				newEvent.replay();
				getChangeEvents().add(newEvent);
			}
		} catch (TTransportException tex) {
			// TODO find out a better way to catch the end of the file...
			if (tex.getType() != TTransportException.END_OF_FILE) {
				throw new IOException(tex);
			}
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			transport.close();

			persistedEvents = getChangeEvents().size();
			changeEventAdapter.setEnabled(true);
		}
	}

	private ChangeEvent<?> buildEvent(TChangeEvent chEvent) {
		switch (chEvent.getSetField()) {
		case ADD_TO_EATTRIBUTE: {
			TAddToEAttributeEvent ev = chEvent.getAddToEAttribute();
			AddToEAttributeEvent event = new AddToEAttributeEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), ev.getPosition());
			loadLiteralValues(ev.getValues(), event.getEStructuralFeature(), event);
			return event;
		}
		case ADD_TO_EREFERENCE: {
			TAddToEReferenceEvent ev = chEvent.getAddToEReference();
			AddToEReferenceEvent event = new AddToEReferenceEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), ev.getPosition());
			loadEObjectValues(event, ev.getValues());
			return event;
		}
		case ADD_TO_RESOURCE: {
			TAddToResourceEvent ev = chEvent.getAddToResource();
			AddToResourceEvent event = new AddToResourceEvent();
			event.setPosition(ev.getPosition());
			event.setResource(this);
			loadEObjectValues(event, ev.getValues());
			return event;
		}
		case CREATE_EOBJECT: {
			TCreateEObjectEvent ev = chEvent.getCreateEObject();
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(ev.getEPackage());
			EClass eClass = (EClass) ePackage.getEClassifier(ev.getEClass());
			return new CreateEObjectEvent(eClass, this, ev.getId() + "");
		}
		case MOVE_WITHIN_EATTRIBUTE: {
			TMoveWithinEAttributeEvent ev = chEvent.getMoveWithinEAttribute();
			MoveWithinEAttributeEvent event = new MoveWithinEAttributeEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), ev.getToPosition());
			event.setFromPosition(ev.getFromPosition());
			return event;
		}
		case MOVE_WITHIN_EREFERENCE: {
			TMoveWithinEReferenceEvent ev = chEvent.getMoveWithinEReference();
			MoveWithinEReferenceEvent event = new MoveWithinEReferenceEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), ev.getToPosition());
			event.setFromPosition(ev.getFromPosition());
			return event;

		}
		case REGISTER_EPACKAGE: {
			TRegisterEPackageEvent ev = chEvent.getRegisterEPackage();
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(ev.getEPackage());
			return new RegisterEPackageEvent(ePackage, changeEventAdapter);

		}
		case REMOVE_FROM_EATTRIBUTE: {
			TRemoveFromEAttributeEvent ev = chEvent.getRemoveFromEAttribute();
			RemoveFromEAttributeEvent event = new RemoveFromEAttributeEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), ev.getPosition());
			loadLiteralValues(ev.getValues(), event.getEStructuralFeature(), event);
			return event;
		}
		case REMOVE_FROM_EREFERENCE: {
			TRemoveFromEReferenceEvent ev = chEvent.getRemoveFromEReference();
			RemoveFromEReferenceEvent event = new RemoveFromEReferenceEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), ev.getPosition());
			loadEObjectValues(event, ev.getValues());
			return event;
		}
		case REMOVE_FROM_RESOURCE: {
			TRemoveFromResourceEvent ev = chEvent.getRemoveFromResource();
			RemoveFromResourceEvent event = new RemoveFromResourceEvent();
			event.setPosition(ev.getPosition());
			event.setResource(this);
			loadEObjectValues(event, ev.getValues());
			return event;
		}
		case SET_EATTRIBUTE: {
			TSetEAttributeEvent ev = chEvent.getSetEAttribute();
			SetEAttributeEvent event = new SetEAttributeEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), 0);
			loadLiteralValues(ev.getValues(), event.getEStructuralFeature(), event);
			return event;
		}
		case SET_EREFERENCE: {
			TSetEReferenceEvent ev = chEvent.getSetEReference();
			SetEReferenceEvent event = new SetEReferenceEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), 0);
			loadEObjectValues(event, ev.getValues());
			return event;
		}
		case UNSET_EATTRIBUTE: {
			TUnsetEAttributeEvent ev = chEvent.getUnsetEAttribute();
			UnsetEAttributeEvent event = new UnsetEAttributeEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), 0);
			return event;
		}
		case UNSET_EREFERENCE:
			TUnsetEReferenceEvent ev = chEvent.getUnsetEReference();
			UnsetEReferenceEvent event = new UnsetEReferenceEvent();
			loadEStructuralFeatureEvent(event, ev.getName(), ev.getTarget(), 0);
			return event;
		}

		return null;
	}

	private void loadEObjectValues(EObjectValuesEvent event, final List<EObjectReference> values) {
		for (EObjectReference value : values) {
			switch (value.getSetField()) {
			case ID:
				event.getValues().add(getEObject(value.getId() + ""));
				break;
			case XREF:
				event.getValues().add(resolveXRef(value.getXref()));
				break;
			}
		}
	}

	private void loadLiteralValues(List<String> values, EStructuralFeature sf, PrimitiveValuesEvent event) {
		final EDataType eDataType = ((EDataType) sf.getEType());
		final EFactory eFactoryInstance = eDataType.getEPackage().getEFactoryInstance();
		for (String value : values) {
			event.getValues().add(eFactoryInstance.createFromString(eDataType, value));
		}
	}

	private void loadEStructuralFeatureEvent(EStructuralFeatureEvent<?> event, String featureName, final long targetId, final int position) {
		final EObject target = getEObject(targetId + "");
		final EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(featureName);
		event.setEStructuralFeature(eStructuralFeature);
		event.setTarget(target);
		event.setPosition(position);
	}

}
