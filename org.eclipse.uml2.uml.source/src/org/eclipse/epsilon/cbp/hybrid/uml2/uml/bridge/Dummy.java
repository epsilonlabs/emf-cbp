package org.eclipse.epsilon.cbp.hybrid.uml2.uml.bridge;

import java.util.Map;

import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.cbp.hybrid.uml2.uml.Comment;
import org.eclipse.epsilon.cbp.hybrid.uml2.uml.DirectedRelationship;
import org.eclipse.epsilon.cbp.hybrid.uml2.uml.Element;
import org.eclipse.epsilon.cbp.hybrid.uml2.uml.Model;
import org.eclipse.epsilon.cbp.hybrid.uml2.uml.Package;
import org.eclipse.epsilon.cbp.hybrid.uml2.uml.Relationship;
import org.eclipse.epsilon.cbp.hybrid.uml2.uml.Stereotype;

public class Dummy extends EModelElementImpl implements Element {

	public Dummy() {
		super();
	}

	@Override
	public EList<Element> getOwnedElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Comment> getOwnedComments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comment createOwnedComment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean validateNotOwnSelf(DiagnosticChain diagnostics, Map<Object, Object> context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validateHasOwner(DiagnosticChain diagnostics, Map<Object, Object> context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EList<EObject> getStereotypeApplications() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EObject getStereotypeApplication(Stereotype stereotype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Stereotype> getRequiredStereotypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stereotype getRequiredStereotype(String qualifiedName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Stereotype> getAppliedStereotypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stereotype getAppliedStereotype(String qualifiedName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Stereotype> getAppliedSubstereotypes(Stereotype stereotype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stereotype getAppliedSubstereotype(Stereotype stereotype, String qualifiedName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasValue(Stereotype stereotype, String propertyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getValue(Stereotype stereotype, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(Stereotype stereotype, String propertyName, Object newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EAnnotation createEAnnotation(String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Relationship> getRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Relationship> getRelationships(EClass eClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<DirectedRelationship> getSourceDirectedRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<DirectedRelationship> getSourceDirectedRelationships(EClass eClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<DirectedRelationship> getTargetDirectedRelationships() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<DirectedRelationship> getTargetDirectedRelationships(EClass eClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<String> getKeywords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addKeyword(String keyword) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeKeyword(String keyword) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Package getNearestPackage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Model getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStereotypeApplicable(Stereotype stereotype) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStereotypeRequired(Stereotype stereotype) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStereotypeApplied(Stereotype stereotype) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EObject applyStereotype(Stereotype stereotype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EObject unapplyStereotype(Stereotype stereotype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Stereotype> getApplicableStereotypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stereotype getApplicableStereotype(String qualifiedName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasKeyword(String keyword) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EList<Element> allOwnedElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean mustBeOwned() {
		// TODO Auto-generated method stub
		return false;
	}
}
