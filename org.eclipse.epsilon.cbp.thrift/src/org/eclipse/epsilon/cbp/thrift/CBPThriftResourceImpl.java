package org.eclipse.epsilon.cbp.thrift;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransportException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.epsilon.cbp.event.AddToEAttributeEvent;
import org.eclipse.epsilon.cbp.event.AddToEReferenceEvent;
import org.eclipse.epsilon.cbp.event.AddToResourceEvent;
import org.eclipse.epsilon.cbp.event.ChangeEvent;
import org.eclipse.epsilon.cbp.event.CreateEObjectEvent;
import org.eclipse.epsilon.cbp.event.EAttributeEvent;
import org.eclipse.epsilon.cbp.event.EObjectValuesEvent;
import org.eclipse.epsilon.cbp.event.EStructuralFeatureEvent;
import org.eclipse.epsilon.cbp.event.FromPositionEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEAttributeEvent;
import org.eclipse.epsilon.cbp.event.MoveWithinEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RegisterEPackageEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEAttributeEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromEReferenceEvent;
import org.eclipse.epsilon.cbp.event.RemoveFromResourceEvent;
import org.eclipse.epsilon.cbp.event.ResourceEvent;
import org.eclipse.epsilon.cbp.event.SetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.SetEReferenceEvent;
import org.eclipse.epsilon.cbp.event.UnsetEAttributeEvent;
import org.eclipse.epsilon.cbp.event.UnsetEReferenceEvent;
import org.eclipse.epsilon.cbp.resource.CBPResource;
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

	protected int persistedEvents = 0;

	public CBPThriftResourceImpl() {
		super();
	}

	public CBPThriftResourceImpl(URI uri) {
		super(uri);
	}

	@Override
	public void doSave(OutputStream out, Map<?, ?> options) throws IOException {
		// TODO: wait, don't we need to jump to the end to the file to simply append?
		final TIOStreamTransport transport = new TIOStreamTransport(out);
		final TProtocol protocol = new TTupleProtocol(transport);
		try {
			for (ChangeEvent<?> event : getChangeEvents().subList(persistedEvents, getChangeEvents().size())) {
				TChangeEvent chEvent = null;

				if (event instanceof RegisterEPackageEvent) {
					RegisterEPackageEvent r = ((RegisterEPackageEvent) event);
					TRegisterEPackageEvent ev = new TRegisterEPackageEvent();
					chEvent = TChangeEvent.registerEPackage(ev);
					ev.setEPackage(r.getEPackage().getNsURI());
				}
				else if (event instanceof CreateEObjectEvent) {
					CreateEObjectEvent c = ((CreateEObjectEvent) event);
					TCreateEObjectEvent ev = new TCreateEObjectEvent();
					chEvent = TChangeEvent.createEObject(ev);
					ev.setEPackage(c.getEClass().getEPackage().getNsURI());
					ev.setEClass(c.getEClass().getName());
					ev.setId(Long.valueOf(c.getId()));
				}
				else if (event instanceof AddToResourceEvent) chEvent = TChangeEvent.addToResource(new TAddToResourceEvent());
				else if (event instanceof RemoveFromResourceEvent) chEvent = TChangeEvent.removeFromResource(new TRemoveFromResourceEvent());
				else if (event instanceof AddToEReferenceEvent) chEvent = TChangeEvent.addToEReference(new TAddToEReferenceEvent());
				else if (event instanceof RemoveFromEReferenceEvent) chEvent = TChangeEvent.removeFromEReference(new TRemoveFromEReferenceEvent());
				else if (event instanceof SetEAttributeEvent) chEvent = TChangeEvent.setEAttribute(new TSetEAttributeEvent());
				else if (event instanceof SetEReferenceEvent) chEvent = TChangeEvent.setEReference(new TSetEReferenceEvent());
				else if (event instanceof UnsetEReferenceEvent) chEvent = TChangeEvent.unsetEReference(new TUnsetEReferenceEvent());
				else if (event instanceof UnsetEAttributeEvent) chEvent = TChangeEvent.unsetEAttribute(new TUnsetEAttributeEvent());	
				else if (event instanceof AddToEAttributeEvent) chEvent = TChangeEvent.addToEAttribute(new TAddToEAttributeEvent());
				else if (event instanceof RemoveFromEAttributeEvent) chEvent = TChangeEvent.removeFromEAttribute(new TRemoveFromEAttributeEvent());
				else if (event instanceof MoveWithinEReferenceEvent) chEvent = TChangeEvent.moveWithinEReference(new TMoveWithinEReferenceEvent());
				else if (event instanceof MoveWithinEAttributeEvent) chEvent = TChangeEvent.moveWithinEAttribute(new TMoveWithinEAttributeEvent());
				else {
					throw new RuntimeException("Unexpected event:" + event);
				}

				/*
				 * Use reflection to overcome the fact that Thrift does not
				 * support inheritance between structs. This could be replaced
				 * in the future with code without reflection, but it would have
				 * a lot of duplication...
				 */
				
				Object rawEvent = chEvent.getFieldValue();
				if (event instanceof EStructuralFeatureEvent<?>) {
					final EStructuralFeatureEvent<?> sfEvent = (EStructuralFeatureEvent<?>) event;
					rawEvent.getClass().getMethod("setName", String.class).invoke(rawEvent, sfEvent.getEStructuralFeature().getName());
					rawEvent.getClass().getMethod("setTarget", long.class).invoke(rawEvent, Long.valueOf(getURIFragment(sfEvent.getTarget())));
				}
				
				if (event instanceof AddToEReferenceEvent || event instanceof AddToEAttributeEvent || event instanceof AddToResourceEvent) {
					rawEvent.getClass().getMethod("setPosition", int.class).invoke(rawEvent, event.getPosition());
				}
				if (event instanceof FromPositionEvent) {
					rawEvent.getClass().getMethod("setFromPosition", int.class).invoke(rawEvent, ((FromPositionEvent)event).getFromPosition());
					rawEvent.getClass().getMethod("setToPosition", int.class).invoke(rawEvent, event.getPosition());
				}
				
				if (event instanceof EObjectValuesEvent) {
					List<Long> ids = new ArrayList<>();
					List<String> xrefs = new ArrayList<>();
					for (EObject eObject : ((EObjectValuesEvent) event).getValues()) {
						if (eObject == null) continue;
						final String uriFragment = getURIFragment(eObject);
						try {
							ids.add(Long.valueOf(uriFragment));
						} catch (NumberFormatException ex) {
							xrefs.add(uriFragment);
						}
					}
					rawEvent.getClass().getMethod("setIds", List.class).invoke(rawEvent, ids);
					rawEvent.getClass().getMethod("setXrefs", List.class).invoke(rawEvent, xrefs);
				}
				else if (event instanceof EAttributeEvent) {
					List<String> values = new ArrayList<>();
					for (Object object : ((EAttributeEvent) event).getValues()) {
						values.add(object + "");
					}
					 rawEvent.getClass().getMethod("setValues", List.class).invoke(rawEvent, values);
				}
				
				chEvent.write(protocol);
			}

			persistedEvents = getChangeEvents().size();
		} catch (TException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
			throw new IOException(ex);
		} finally {
			transport.close();
		}
	}

	protected void addValues(final Collection<EObject> values, final List<Long> ids, final List<String> xrefs) {
		for (EObject value : values) {
			String fragment = eObjectToIdMap.get(value);
			try {
				ids.add(Long.valueOf(fragment));
			} catch (NumberFormatException ex) {
				xrefs.add(fragment);
			}
		}
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		changeEventAdapter.setEnabled(false);
		eObjectToIdMap.clear();
		getChangeEvents().clear();

		final TIOStreamTransport transport = new TIOStreamTransport(inputStream);
		final TProtocol protocol = new TTupleProtocol(transport);

		try {
			while (transport.peek()) {
				final TChangeEvent chEvent = new TChangeEvent();
				chEvent.read(protocol);
				doLoad(chEvent);
				// do not do anything after here!
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

	@SuppressWarnings("unchecked")
	private void doLoad(TChangeEvent chEvent) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object rawEvent = chEvent.getFieldValue();
		ChangeEvent<?> event = buildEvent(chEvent);

		if (event instanceof EStructuralFeatureEvent<?>) {
			long id = (long) rawEvent.getClass().getMethod("getTarget").invoke(rawEvent);
			String name = (String) rawEvent.getClass().getMethod("getName").invoke(rawEvent);
			EObject target = getEObject(id + "");
			EStructuralFeature eStructuralFeature = target.eClass().getEStructuralFeature(name);
			((EStructuralFeatureEvent<?>) event).setEStructuralFeature(eStructuralFeature);
			((EStructuralFeatureEvent<?>) event).setTarget(target);
		}
		else if (event instanceof ResourceEvent) {
			((ResourceEvent) event).setResource(this);
		}

		if (event instanceof AddToEAttributeEvent || event instanceof AddToEReferenceEvent || event instanceof AddToResourceEvent) {
			final int position = (int) rawEvent.getClass().getMethod("getPosition").invoke(rawEvent);
			event.setPosition(position);
		}
		if (event instanceof FromPositionEvent) {
			final int from = (int) rawEvent.getClass().getMethod("getFromPosition").invoke(rawEvent);
			final int to = (int) rawEvent.getClass().getMethod("getToPosition").invoke(rawEvent);
			event.setPosition(to);
			((FromPositionEvent)event).setFromPosition(from);
		}

		if (event instanceof EObjectValuesEvent) {
			EObjectValuesEvent valuesEvent = (EObjectValuesEvent) event;

			// TODO go back to string ids always in case we have a mix, or use a union?
			List<Long> ids = (List<Long>) rawEvent.getClass().getMethod("getIds").invoke(rawEvent);
			List<String> xrefs = (List<String>) rawEvent.getClass().getMethod("getXrefs").invoke(rawEvent);
			for (Long id : ids) {
				valuesEvent.getValues().add(getEObject(id + ""));
			}
			for (String xref : xrefs) {
				valuesEvent.getValues().add(resolveXRef(xref));
			}
		}
		else if (event instanceof EAttributeEvent) {
			EAttributeEvent eAttributeEvent = (EAttributeEvent) event;
			List<String> values = (List<String>) rawEvent.getClass().getMethod("getValues").invoke(rawEvent);
			for (String value : values) {
				eAttributeEvent.getValues().add(getLiteralValue(eAttributeEvent.getTarget(), eAttributeEvent.getEStructuralFeature(), value));
			}
		}
		
		event.replay();
		getChangeEvents().add(event);
	}

	protected ChangeEvent<?> buildEvent(TChangeEvent change) {
		if (change.isSetRegisterEPackage()) {
			TRegisterEPackageEvent ev = change.getRegisterEPackage();
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(ev.getEPackage());
			return new RegisterEPackageEvent(ePackage, changeEventAdapter);
		}
		else if (change.isSetCreateEObject()) {
			TCreateEObjectEvent ev = change.getCreateEObject();
			EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(ev.getEPackage());
			EClass eClass = (EClass) ePackage.getEClassifier(ev.getEClass());
			return new CreateEObjectEvent(eClass, this, ev.getId() + "");
		}
		else if (change.isSetAddToResource())
			return new AddToResourceEvent();
		else if (change.isSetRemoveFromResource())
			return new RemoveFromResourceEvent();
		else if (change.isSetAddToEReference())
			return new AddToEReferenceEvent();
		else if (change.isSetRemoveFromEReference())
			return new RemoveFromEReferenceEvent();
		else if (change.isSetSetEAttribute())
			return new SetEAttributeEvent();
		else if (change.isSetSetEReference())
			return new SetEReferenceEvent();
		else if (change.isSetUnsetEAttribute())
			return new UnsetEAttributeEvent();
		else if (change.isSetUnsetEReference())
			return new UnsetEReferenceEvent();
		else if (change.isSetAddToEAttribute())
			return new AddToEAttributeEvent();
		else if (change.isSetRemoveFromEAttribute())
			return new RemoveFromEAttributeEvent();
		else if (change.isSetMoveWithinEAttribute())
			return new MoveWithinEAttributeEvent();
		else if (change.isSetMoveWithinEReference())
			return new MoveWithinEReferenceEvent();

		return null;
	}

	protected Object getLiteralValue(EObject eObject, EStructuralFeature eStructuralFeature, String value) {
		EDataType eDataType = ((EDataType) eStructuralFeature.getEType());
		return eDataType.getEPackage().getEFactoryInstance().createFromString(eDataType, value);
	}

}
