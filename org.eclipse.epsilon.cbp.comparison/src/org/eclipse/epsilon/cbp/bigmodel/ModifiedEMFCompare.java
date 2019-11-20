package org.eclipse.epsilon.cbp.bigmodel;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.ComparisonCanceledException;
import org.eclipse.emf.compare.Conflict;
import org.eclipse.emf.compare.ConflictKind;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompareMessages;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.ResourceAttachmentChange;
import org.eclipse.emf.compare.conflict.IConflictDetector;
import org.eclipse.emf.compare.conflict.MatchBasedConflictDetector;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.DiffBuilder;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.equi.DefaultEquiEngine;
import org.eclipse.emf.compare.equi.IEquiEngine;
import org.eclipse.emf.compare.internal.spec.ComparisonSpec;
import org.eclipse.emf.compare.internal.utils.SafeSubMonitor;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.IMatchEngine.Factory.Registry;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.merge.ResourceChangeAdapter;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.postprocessor.PostProcessorDescriptorRegistryImpl;
import org.eclipse.emf.compare.req.DefaultReqEngine;
import org.eclipse.emf.compare.req.IReqEngine;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.compare.uml2.internal.AssociationChange;
import org.eclipse.emf.compare.uml2.internal.DirectedRelationshipChange;
import org.eclipse.emf.compare.uml2.internal.MultiplicityElementChange;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;

import com.google.common.collect.Iterators;

public class ModifiedEMFCompare extends EMFCompare {

    /**
     * The value for diagnostics coming from EMF compare.
     * 
     * @since 3.2
     */
    public static final String DIAGNOSTIC_SOURCE = "org.eclipse.emf.compare"; //$NON-NLS-1$

    /** Constant for logging. */
    private static final String START = " - START"; //$NON-NLS-1$

    /** Constant for logging. */
    private static final String FINISH = " - FINISH"; //$NON-NLS-1$

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(EMFCompare.class);

    /** The registry we'll use to create a match engine for this comparison. */
    private final IMatchEngine.Factory.Registry matchEngineFactoryRegistry;

    /** The IDiffEngine to use to compute comparison. */
    private final IDiffEngine diffEngine;

    /** The IReqEngine to use to compute comparison. */
    private final IReqEngine reqEngine;

    /** The IEquiEngine to use to compute comparison. */
    private final IEquiEngine equiEngine;

    /** The IConflictDetector to use to compute comparison. */
    private final IConflictDetector conflictDetector;

    /** The PostProcessorRegistry to use to find an IPostProcessor. */
    private final IPostProcessor.Descriptor.Registry<?> postProcessorDescriptorRegistry;

    private List<String> emfcConflicts = new ArrayList<>();
    private long matchTime = 0;
    private long diffTime = 0;
    private long conflictTime = 0;
    private long comparisonTime = 0;
    private long matchMemory = 0;
    private long diffMemory = 0;
    private long conflictMemory = 0;
    private long comparisonMemory = 0;
    private Comparison comparison = null;

    protected ModifiedEMFCompare(Registry matchEngineFactoryRegistry, IDiffEngine diffEngine, IReqEngine reqEngine, IEquiEngine equiEngine, IConflictDetector conflictDetector,
	    org.eclipse.emf.compare.postprocessor.IPostProcessor.Descriptor.Registry<?> postProcessorFactoryRegistry) {
	super(matchEngineFactoryRegistry, diffEngine, reqEngine, equiEngine, conflictDetector, postProcessorFactoryRegistry);
	this.matchEngineFactoryRegistry = checkNotNull(matchEngineFactoryRegistry);
	this.diffEngine = checkNotNull(diffEngine);
	this.reqEngine = checkNotNull(reqEngine);
	this.equiEngine = checkNotNull(equiEngine);
	this.conflictDetector = conflictDetector;
	this.postProcessorDescriptorRegistry = checkNotNull(postProcessorFactoryRegistry);
    }

    @Override
    public Comparison compare(IComparisonScope scope) {
	return compare(scope, new BasicMonitor());
    }

    @Override
    public Comparison compare(IComparisonScope scope, final Monitor monitor) {
	checkNotNull(scope);
	checkNotNull(monitor);

	// Used to compute the time spent in the method
	long startTime = System.currentTimeMillis();
	long startInterval = 0;
	long endInterval = 0;
	long startMemory = 0;
	long endMemory = 0;

	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("compare() - START"); //$NON-NLS-1$
	}

	Comparison comparison = null;
	try {
	    Monitor subMonitor = new SafeSubMonitor(monitor);
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("compare() - starting step: MATCH"); //$NON-NLS-1$
	    }

	    System.gc();
	    startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	    startInterval = System.nanoTime();
	    comparison = matchEngineFactoryRegistry.getHighestRankingMatchEngineFactory(scope).getMatchEngine().match(scope, subMonitor);
	    this.comparison = comparison;
	    endInterval = System.nanoTime();
	    endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	    this.setMatchTime(endInterval - startInterval);
	    this.setMatchMemory(endMemory - startMemory);

	    installResourceChangeAdapter(comparison, scope);

	    monitor.worked(1);
	    List<IPostProcessor> postProcessors = postProcessorDescriptorRegistry.getPostProcessors(scope);

	    // CHECKSTYLE:OFF Yes, I want to have ifs here and no constant for
	    // "post-processor".
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("compare() - starting step: POST-MATCH with " + postProcessors.size() //$NON-NLS-1$
			+ " post-processors"); //$NON-NLS-1$
	    }
	    postMatch(comparison, postProcessors, subMonitor);
	    monitor.worked(1);

	    if (!hasToStop(comparison, monitor)) {
		if (LOGGER.isInfoEnabled()) {
		    LOGGER.info("compare() - starting step: DIFF"); //$NON-NLS-1$
		}
		System.gc();
		startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		startInterval = System.nanoTime();
		diffEngine.diff(comparison, subMonitor);
		postDiff(comparison, postProcessors, subMonitor);
		endInterval = System.nanoTime();
		endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		this.setDiffTime(endInterval - startInterval);
		this.setDiffMemory(endMemory - startMemory);

		this.comparisonTime = this.matchTime + this.diffTime;
		this.comparisonMemory = this.matchMemory + this.diffMemory;

		// System.out.println("Total Diffs = " +
		// comparison.getDifferences().size());

		// // For now, we only the match and diff steps
		// monitor.worked(1);
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: POST-DIFF with "
		// // $NON-NLS-1$
		// + postProcessors.size() + " post-processors"); //$NON-NLS-1$
		// }
		// postDiff(comparison, postProcessors, subMonitor);
		// monitor.worked(1);
		//
		// if (!hasToStop(comparison, monitor)) {
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: REQUIREMENTS");
		// // $NON-NLS-1$
		// }

		// monitor.worked(1);
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: POST-REQUIREMENTS
		// with " //$NON-NLS-1$
		// + postProcessors.size() + " post-processors"); //$NON-NLS-1$
		// }
		// postRequirements(comparison, postProcessors, subMonitor);
		// monitor.worked(1);
		//
		// if (!hasToStop(comparison, monitor)) {
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: EQUIVALENCES");
		// // $NON-NLS-1$
		// }
		// equiEngine.computeEquivalences(comparison, subMonitor);
		// monitor.worked(1);
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: POST-EQUIVALENCES
		// with " //$NON-NLS-1$
		// + postProcessors.size() + " post-processors"); //$NON-NLS-1$
		// }
		// postEquivalences(comparison, postProcessors, subMonitor);
		// monitor.worked(1);
		//
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: CONFLICT");
		// // $NON-NLS-1$
		// }
		System.gc();
		startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		startInterval = System.nanoTime();
		// reqEngine.computeRequirements(comparison, subMonitor);
		// postRequirements(comparison, postProcessors, subMonitor);
		// equiEngine.computeEquivalences(comparison, subMonitor);
		// postEquivalences(comparison, postProcessors, subMonitor);
		detectConflicts(comparison, postProcessors, subMonitor);
		// postComparison(comparison, postProcessors, subMonitor);
		endInterval = System.nanoTime();
		endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		this.setConflictTime(endInterval - startInterval);
		System.gc();
		this.setConflictMemory(endMemory - startMemory);

		this.comparisonTime = this.matchTime + this.diffTime + this.conflictTime;
		this.comparisonMemory = this.matchMemory + this.diffMemory + this.conflictMemory;
		//
		// monitor.worked(1);
		//
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: POST-COMPARISON with
		// " //$NON-NLS-1$
		// + postProcessors.size() + " post-processors"); //$NON-NLS-1$
		// }
		// // CHECKSTYLE:ON
		// postComparison(comparison, postProcessors, subMonitor);
		// }
		// }
	    }
	} catch (ComparisonCanceledException e) {
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("compare() - Comparison has been canceled"); //$NON-NLS-1$
	    }
	    if (comparison == null) {
		comparison = new ComparisonSpec();
	    }
	    BasicDiagnostic cancelledDiag = new BasicDiagnostic(Diagnostic.CANCEL, DIAGNOSTIC_SOURCE, 0, EMFCompareMessages.getString("ComparisonCancelled"), null); //$NON-NLS-1$
	    Diagnostic diag = comparison.getDiagnostic();
	    if (diag != null && diag instanceof DiagnosticChain) {
		((DiagnosticChain) diag).merge(cancelledDiag);
	    } else {
		comparison.setDiagnostic(cancelledDiag);
	    }
	} finally {
	    monitor.done();
	}

	if (LOGGER.isInfoEnabled()) {
	    logEndOfComparison(comparison, startTime);
	}

	// Add scope to the comparison's adapters to make it available
	// throughout the framework
	if (scope instanceof Adapter) {
	    comparison.eAdapters().add((Adapter) scope);
	}

	return comparison;
    }

    private void postMatch(final Comparison comparison, List<IPostProcessor> postProcessors, final Monitor monitor) {
	Iterator<IPostProcessor> processorsIterator = postProcessors.iterator();
	while (!hasToStop(comparison, monitor) && processorsIterator.hasNext()) {
	    final IPostProcessor iPostProcessor = processorsIterator.next();
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postMatch with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + START);
	    }
	    iPostProcessor.postMatch(comparison, monitor);
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postMatch with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + FINISH);
	    }
	}
    }

    private static boolean hasToStop(Comparison comparison, Monitor monitor) {
	return monitor.isCanceled() || (comparison.getDiagnostic() != null && comparison.getDiagnostic().getSeverity() >= Diagnostic.ERROR);
    }

    private void installResourceChangeAdapter(Comparison comparison, IComparisonScope scope) {
	if (scope.getLeft() instanceof ResourceSet && scope.getRight() instanceof ResourceSet) {
	    Adapter existingAdapter = EcoreUtil.getExistingAdapter(comparison, ResourceChangeAdapter.class);
	    if (existingAdapter == null) {
		ResourceChangeAdapter adapter = new ResourceChangeAdapter(comparison, scope);
		comparison.eAdapters().add(adapter);
		ResourceSet left = (ResourceSet) scope.getLeft();
		left.eAdapters().add(adapter);
		for (Resource r : left.getResources()) {
		    r.eAdapters().add(adapter);
		}
		ResourceSet right = (ResourceSet) scope.getRight();
		right.eAdapters().add(adapter);
		for (Resource r : right.getResources()) {
		    r.eAdapters().add(adapter);
		}
	    }
	}
    }

    private void postRequirements(final Comparison comparison, List<IPostProcessor> postProcessors, final Monitor monitor) {
	Iterator<IPostProcessor> processorsIterator = postProcessors.iterator();
	while (!hasToStop(comparison, monitor) && processorsIterator.hasNext()) {
	    final IPostProcessor iPostProcessor = processorsIterator.next();
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postRequirements with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + START);
	    }
	    iPostProcessor.postRequirements(comparison, monitor);
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postRequirements with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + FINISH);
	    }
	}
    }

    private void postDiff(final Comparison comparison, List<IPostProcessor> postProcessors, final Monitor monitor) {
	Iterator<IPostProcessor> processorsIterator = postProcessors.iterator();
	while (!hasToStop(comparison, monitor) && processorsIterator.hasNext()) {
	    final IPostProcessor iPostProcessor = processorsIterator.next();
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postDiff with post-processor: " + iPostProcessor.getClass().getName() //$NON-NLS-1$
			+ START);
	    }
	    iPostProcessor.postDiff(comparison, monitor);
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postDiff with post-processor: " + iPostProcessor.getClass().getName() //$NON-NLS-1$
			+ FINISH);
	    }
	}
    }

    private void postComparison(final Comparison comparison, List<IPostProcessor> postProcessors, final Monitor monitor) {
	Iterator<IPostProcessor> processorsIterator = postProcessors.iterator();
	while (!hasToStop(comparison, monitor) && processorsIterator.hasNext()) {
	    final IPostProcessor iPostProcessor = processorsIterator.next();
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postComparison with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + START);
	    }
	    iPostProcessor.postComparison(comparison, monitor);
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postComparison with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + FINISH);
	    }
	}
    }

    private void postEquivalences(final Comparison comparison, List<IPostProcessor> postProcessors, final Monitor monitor) {
	Iterator<IPostProcessor> processorsIterator = postProcessors.iterator();
	while (!hasToStop(comparison, monitor) && processorsIterator.hasNext()) {
	    final IPostProcessor iPostProcessor = processorsIterator.next();
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postEquivalences with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + START);
	    }
	    iPostProcessor.postEquivalences(comparison, monitor);
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postEquivalences with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + FINISH);
	    }
	}
    }

    private void detectConflicts(final Comparison comparison, List<IPostProcessor> postProcessors, final Monitor monitor) {
	if (!hasToStop(comparison, monitor) && comparison.isThreeWay() && conflictDetector != null) {
	    conflictDetector.detect(comparison, monitor);
	    postConflicts(comparison, postProcessors, monitor);
	}
    }

    private void logEndOfComparison(Comparison comparison, long start) {
	long duration = System.currentTimeMillis() - start;
	int diffQuantity = comparison.getDifferences().size();
	int conflictQuantity = comparison.getConflicts().size();
	int matchQuantity = 0;
	EList<Match> matches = comparison.getMatches();
	for (Match match : matches) {
	    matchQuantity++;
	    matchQuantity += Iterators.size(match.getAllSubmatches().iterator());
	}
	LOGGER.info("compare() - FINISH - " + matchQuantity + " matches, " + diffQuantity + " diffs and " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		+ conflictQuantity + " conflicts found in " + duration + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void postConflicts(final Comparison comparison, List<IPostProcessor> postProcessors, final Monitor monitor) {
	Iterator<IPostProcessor> processorsIterator = postProcessors.iterator();
	while (!hasToStop(comparison, monitor) && processorsIterator.hasNext()) {
	    final IPostProcessor iPostProcessor = processorsIterator.next();
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postConflicts with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + START);
	    }
	    iPostProcessor.postConflicts(comparison, monitor);
	    if (LOGGER.isInfoEnabled()) {
		LOGGER.info("postConflicts with post-processor: " //$NON-NLS-1$
			+ iPostProcessor.getClass().getName() + FINISH);
	    }
	}
    }

    public static ModifiedEMFCompare.ModifiedBuilder modifiedBuilder() {
	return new ModifiedBuilder();
    }

    /**
     * A Builder pattern to instantiate EMFCompare objects.
     * 
     * @author <a href="mailto:mikael.barbero@obeo.fr">Mikael Barbero</a>
     */
    public static class ModifiedBuilder {

	/**
	 * The registry we'll use to create a match engine for this comparison.
	 */
	protected IMatchEngine.Factory.Registry matchEngineFactoryRegistry;

	/** The IReqEngine to use to compute comparison. */
	protected IReqEngine reqEngine;

	/** The IDiffEngine to use to compute comparison. */
	protected IDiffEngine diffEngine;

	/** The IEquiEngine to use to compute comparison. */
	protected IEquiEngine equiEngine;

	/** The IConflictDetector to use to compute conflicts. */
	protected IConflictDetector conflictDetector;

	/** The PostProcessorRegistry to use to find an IPostProcessor. */
	protected IPostProcessor.Descriptor.Registry<?> registry;

	/**
	 * Creates a new builder object.
	 */
	protected ModifiedBuilder() {
	}

	/**
	 * Sets the IMatchEngine.Factory.Registry to be used to find a match
	 * engine factory to compute comparison.
	 * 
	 * @param mefr
	 *            the IMatchEngine.Factory.Registry to be used to find a
	 *            match engine factory to compute comparison.
	 * @return this same builder to allow chained call.
	 */
	public ModifiedBuilder setMatchEngineFactoryRegistry(IMatchEngine.Factory.Registry mefr) {
	    this.matchEngineFactoryRegistry = checkNotNull(mefr);
	    return this;
	}

	/**
	 * Sets the IDiffEngine to be used to compute Diff.
	 * 
	 * @param de
	 *            the IDiffEngine to be used to compute Diff.
	 * @return this same builder to allow chained call.
	 */
	public ModifiedBuilder setDiffEngine(IDiffEngine de) {
	    this.diffEngine = checkNotNull(de);
	    return this;
	}

	/**
	 * Sets the IReqEngine to be used to compute dependencies between Diff.
	 * 
	 * @param re
	 *            the IReqEngine to be used to compute dependencies between
	 *            Diff.
	 * @return this same builder to allow chained call.
	 */
	public ModifiedBuilder setRequirementEngine(IReqEngine re) {
	    this.reqEngine = checkNotNull(re);
	    return this;
	}

	/**
	 * Sets the IEquiEngine to be used to compute equivalences between Diff.
	 * 
	 * @param ee
	 *            the IEquiEngine to be used to compute equivalences between
	 *            Diff
	 * @return this same builder to allow chained call.
	 */
	public ModifiedBuilder setEquivalenceEngine(IEquiEngine ee) {
	    this.equiEngine = checkNotNull(ee);
	    return this;
	}

	/**
	 * Sets the IEquiEngine to be used to compute conflicts between Diff.
	 * 
	 * @param cd
	 *            the IEquiEngine to be used to compute conflicts between
	 *            Diff.
	 * @return this same builder to allow chained call.
	 */
	public ModifiedBuilder setConflictDetector(IConflictDetector cd) {
	    this.conflictDetector = checkNotNull(cd);
	    return this;
	}

	/**
	 * Sets the PostProcessor to be used to find the post processor of each
	 * comparison steps.
	 * 
	 * @param r
	 *            the PostProcessor to be used to find the post processor of
	 *            each comparison steps.
	 * @return this same builder to allow chained call.
	 */
	public ModifiedBuilder setPostProcessorRegistry(IPostProcessor.Descriptor.Registry<?> r) {
	    this.registry = checkNotNull(r);
	    return this;
	}

	/**
	 * Instantiates and return an EMFCompare object configured with the
	 * previously given engines.
	 * 
	 * @return an EMFCompare object configured with the previously given
	 *         engines
	 */
	public ModifiedEMFCompare build() {
	    if (matchEngineFactoryRegistry == null) {
		matchEngineFactoryRegistry = MatchEngineFactoryRegistryImpl.createStandaloneInstance();
	    }
	    if (diffEngine == null) {
		diffEngine = new DefaultDiffEngine(new DiffBuilder());
	    }
	    if (reqEngine == null) {
		reqEngine = new DefaultReqEngine();
	    }
	    if (equiEngine == null) {
		equiEngine = new DefaultEquiEngine();
	    }
	    if (registry == null) {
		registry = new PostProcessorDescriptorRegistryImpl<Object>();
	    }
	    if (conflictDetector == null) {
		conflictDetector = new MatchBasedConflictDetector();
	    }
	    return new ModifiedEMFCompare(this.matchEngineFactoryRegistry, this.diffEngine, this.reqEngine, this.equiEngine, this.conflictDetector, this.registry);
	}
    }

    public long getMatchTime() {
	return matchTime;
    }

    private void setMatchTime(long matchTime) {
	this.matchTime = matchTime;
    }

    public long getDiffTime() {
	return diffTime;
    }

    private void setDiffTime(long diffTime) {
	this.diffTime = diffTime;
    }

    public long getComparisonTime() {
	return comparisonTime;
    }

    public long getMatchMemory() {
	return matchMemory;
    }

    public void setMatchMemory(long matchMemory) {
	this.matchMemory = matchMemory;
    }

    public long getDiffMemory() {
	return diffMemory;
    }

    public void setDiffMemory(long diffMemory) {
	this.diffMemory = diffMemory;
    }

    public long getComparisonMemory() {
	return comparisonMemory;
    }

    public void setComparisonMemory(long comparisonMemory) {
	this.comparisonMemory = comparisonMemory;
    }

    public EList<Diff> getDiffs() {
	return comparison.getDifferences();
    }

    public EList<Conflict> getConflicts() {
	// Iterator<Conflict> iterator = comparison.getConflicts().iterator();
	// while(iterator.hasNext()) {
	// if (iterator.next().getKind() == ConflictKind.PSEUDO) {
	// iterator.remove();
	// }
	// }
	return comparison.getConflicts();
    }

    public List<Conflict> getRealConflicts() {
	List<Conflict> conflicts = new ArrayList(comparison.getConflicts());
	Iterator<Conflict> iterator = conflicts.iterator();
	while (iterator.hasNext()) {
	    if (iterator.next().getKind() == ConflictKind.PSEUDO) {
		iterator.remove();
	    }
	}
	return conflicts;
    }

    public long getConflictTime() {
	return conflictTime;
    }

    public long getConflictMemory() {
	return conflictMemory;
    }

    public void setConflictTime(long conflictTime) {
	this.conflictTime = conflictTime;
    }

    public void setConflictMemory(long conflictMemory) {
	this.conflictMemory = conflictMemory;
    }

    @SuppressWarnings("unchecked")
    public String diffToString(XMIResource leftModel, XMIResource rightModel, Diff diff) throws FileNotFoundException, IOException {

	String result = "";
	String leftTarget = null;
	String rightTarget = null;
	EObject eLeftTarget = null;
	EObject eRightTarget = null;

	if (diff.getSource() == DifferenceSource.LEFT) {
	    eLeftTarget = diff.getMatch().getLeft();
	    eRightTarget = diff.getMatch().getOrigin();
	} else {
	    eLeftTarget = diff.getMatch().getRight();
	    eRightTarget = diff.getMatch().getOrigin();
	}

	if (eLeftTarget != null) {
	    leftTarget = leftModel.getURIFragment(eLeftTarget);
	}
	if (eRightTarget != null) {
	    rightTarget = rightModel.getURIFragment(eRightTarget);
	}

	if (eLeftTarget == null) {
	    eLeftTarget = diff.getMatch().getOrigin();
	    if (eLeftTarget != null) {
		leftTarget = eLeftTarget.eResource().getURIFragment(eLeftTarget);
	    }
	}
	if (eRightTarget == null) {
	    eRightTarget = diff.getMatch().getOrigin();
	    if (eRightTarget != null) {
		rightTarget = eRightTarget.eResource().getURIFragment(eRightTarget);
	    }
	}

	if (diff.getKind() == DifferenceKind.ADD) {
	    if (diff instanceof AttributeChange) {
		EAttribute eFeature = ((AttributeChange) diff).getAttribute();
		String featureName = eFeature.getName();
		Object value = ((AttributeChange) diff).getValue();
		// if (value instanceof String) {
		// value = "\"" + value + "\"";
		// }
		int index = 0;
		if (eFeature.isMany()) {
		    List<Object> list = (List<Object>) eLeftTarget.eGet(eFeature);
		    index = list.indexOf(value);
		}
		result = "ADD " + value + " TO " + leftTarget + "." + featureName + " AT " + index;
	    } else if (diff instanceof ReferenceChange) {
		EReference eFeature = ((ReferenceChange) diff).getReference();
		String featureName = eFeature.getName();
		EObject eValue = ((ReferenceChange) diff).getValue();
		String value = leftModel.getID(eValue);
		int index = 0;
		if (eFeature.isMany()) {
		    List<Object> list = (List<Object>) eLeftTarget.eGet(eFeature);
		    index = list.indexOf(eValue);
		    result = "ADD " + value + " TO " + leftTarget + "." + featureName + " AT " + index;
		} else {
		    result = "SET " + leftTarget + "." + featureName + " FROM " + null + " TO " + value;
		}
	    } else if (diff instanceof ResourceAttachmentChange) {
		String featureName = "resource";
		String value = leftModel.getID(eLeftTarget);
		int index = leftModel.getContents().indexOf(eLeftTarget);
		result = "ADD " + value + " TO " + featureName + " AT " + index;
	    }
	} else if (diff.getKind() == DifferenceKind.CHANGE) {
	    if (diff instanceof AttributeChange) {
		EAttribute eFeature = ((AttributeChange) diff).getAttribute();
		String featureName = eFeature.getName();
		Object rightValue = eRightTarget.eGet(eFeature);
		Object leftValue = eLeftTarget.eGet(eFeature);
		// if (rightValue instanceof String) {
		// rightValue = "\"" + rightValue + "\"";
		// }
		// if (leftValue instanceof String) {
		// leftValue = "\"" + leftValue + "\"";
		// }
		result = "SET " + leftTarget + "." + featureName + " FROM " + rightValue + " TO " + leftValue;
	    } else if (diff instanceof ReferenceChange) {
		EReference eFeature = ((ReferenceChange) diff).getReference();
		String featureName = eFeature.getName();
		EObject eOldValue = null;
		String oldValue = null;
		if (eRightTarget != null) {
		    eOldValue = (EObject) eRightTarget.eGet(eFeature);
		    oldValue = rightModel.getID(eOldValue);
		} else {
		    System.console();
		}
		EObject eValue = null;
		String value = null;
		if (eLeftTarget != null) {
		    eValue = (EObject) eLeftTarget.eGet(eFeature);
		    value = leftModel.getID(eValue);
		}
		result = "SET " + leftTarget + "." + featureName + " FROM " + oldValue + " TO " + value;
	    }
	} else if (diff.getKind() == DifferenceKind.MOVE) {
	    if (diff instanceof AttributeChange) {
		EAttribute eFeature = ((AttributeChange) diff).getAttribute();
		String featureName = eFeature.getName();
		Object value = ((AttributeChange) diff).getValue();
		String oldPosition = "";
		String position = "";
		EList<EObject> list1 = (EList<EObject>) eRightTarget.eGet(eFeature);
		oldPosition = "" + list1.indexOf(value);
		EList<EObject> list2 = (EList<EObject>) eLeftTarget.eGet(eFeature);
		position = "" + list2.indexOf(value);
		result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO " + position;
	    } else if (diff instanceof ReferenceChange) {
		EReference eFeature = ((ReferenceChange) diff).getReference();
		if (eFeature.isContainment()) {
		    String featureName = eFeature.getName();
		    EObject eValue = ((ReferenceChange) diff).getValue();
		    String value = leftModel.getID(eValue);
		    EObject eOldValue = rightModel.getEObject(value);
		    EReference eOldFeature = (EReference) eOldValue.eContainingFeature();
		    String eOldFeatureName = null;
		    if (eOldFeature != null) {
			eOldFeatureName = eOldFeature.getName();
		    }
		    eRightTarget = eOldValue.eContainer();
		    rightTarget = rightModel.getID(eRightTarget);
		    String oldPosition = "";
		    if (eOldFeature != null && eOldFeature.isMany()) {
			EList<EObject> list = (EList<EObject>) eRightTarget.eGet(eOldFeature);
			oldPosition = "" + list.indexOf(eOldValue);
		    }
		    String position = "";
		    if (eFeature.isMany()) {
			EList<EObject> list = (EList<EObject>) eLeftTarget.eGet(eFeature);
			position = "" + list.indexOf(eValue);
		    }
		    // cross container
		    if (!leftTarget.equals(rightTarget) || !featureName.equals(eOldFeatureName)) {
			// from other container/feature
			if (eRightTarget != null) {
			    if (eOldFeature.isMany()) {
				oldPosition = "." + oldPosition;
			    }
			    if (eFeature.isMany()) {
				position = "." + position;
			    }
			    result = "MOVE " + value + " ACROSS FROM " + rightTarget + "." + eOldFeatureName + oldPosition + " TO " + leftTarget + "." + featureName + position;
			}
			// from resource
			else {
			    eOldFeatureName = "resource";
			    oldPosition = "" + rightModel.getContents().indexOf(eValue);
			    if (eFeature.isMany()) {
				position = "." + position;
			    }
			    result = "MOVE " + value + " ACROSS FROM " + eOldFeatureName + oldPosition + " TO " + leftTarget + "." + featureName + position;
			}
		    }
		    // within container
		    else {
			result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO " + position;
		    }
		} else {
		    String featureName = eFeature.getName();
		    EObject eValue = ((ReferenceChange) diff).getValue();
		    String value = leftModel.getID(eValue);
		    Object eOldValue = rightModel.getEObject(value);
		    String oldPosition = "";
		    String position = "";
		    EList<EObject> list1 = (EList<EObject>) eRightTarget.eGet(eFeature);
		    oldPosition = "" + list1.indexOf(eOldValue);
		    EList<EObject> list2 = (EList<EObject>) eLeftTarget.eGet(eFeature);
		    position = "" + list2.indexOf(eValue);
		    result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO " + position;
		}
	    }

	    else if (diff instanceof ResourceAttachmentChange) {
		String featureName = "resource";
		EObject eValue = eLeftTarget;
		String value = null;
		EObject eOldValue = null;
		if (eValue != null) {
		    value = leftModel.getID(eValue);
		    eOldValue = rightModel.getEObject(value);
		} else {
		    eOldValue = diff.getMatch().getRight();
		    value = rightModel.getID(eValue);
		}

		// from container/feature
		if (eOldValue.eContainer() != null) {
		    eRightTarget = eOldValue.eContainer();
		    rightTarget = rightModel.getID(eRightTarget);
		    EReference eOldFeature = (EReference) eOldValue.eContainingFeature();
		    String eOldFeatureName = eOldFeature.getName();

		    String oldPosition = "";
		    if (eOldFeature.isMany()) {
			EList<Object> list = (EList<Object>) eRightTarget.eGet(eOldFeature);
			oldPosition = "." + list.indexOf(eOldValue);
		    }
		    String position = "." + leftModel.getContents().indexOf(eValue);
		    result = "MOVE " + value + " ACROSS FROM " + rightTarget + "." + eOldFeatureName + oldPosition + " TO " + featureName + position;
		}
		// within resource / root level
		else {
		    String oldPosition = "" + rightModel.getContents().indexOf(eOldValue);
		    String position = "" + leftModel.getContents().indexOf(eValue);
		    result = "MOVE " + value + " IN " + leftTarget + "." + featureName + " FROM " + oldPosition + " TO " + position;
		}
	    }
	} else if (diff.getKind() == DifferenceKind.DELETE) {
	    if (diff instanceof AttributeChange) {
		EAttribute eFeature = ((AttributeChange) diff).getAttribute();
		String featureName = eFeature.getName();
		Object value = ((AttributeChange) diff).getValue();
		// if (value instanceof String) {
		// value = "\"" + value + "\"";
		// }
		int index = 0;
		if (eFeature.isMany()) {
		    List<Object> list = (List<Object>) eRightTarget.eGet(eFeature);
		    index = list.indexOf(value);
		}
		result = "DELETE " + value + " FROM " + rightTarget + "." + featureName + " AT " + index;
	    } else if (diff instanceof ReferenceChange) {
		EReference eFeature = ((ReferenceChange) diff).getReference();
		String featureName = eFeature.getName();
		EObject eValue = ((ReferenceChange) diff).getValue();
		String value = rightModel.getID(eValue);
		int index = 0;
		if (eFeature.isMany()) {
		    List<Object> list = (List<Object>) eRightTarget.eGet(eFeature);
		    index = list.indexOf(eValue);
		}
		result = "DELETE " + value + " FROM " + rightTarget + "." + featureName + " AT " + index;
	    } else if (diff instanceof ResourceAttachmentChange) {
		String featureName = "resource";
		String value = rightModel.getID(eRightTarget);
		int index = rightModel.getContents().indexOf(eRightTarget);
		result = "DELETE " + value + " FROM " + featureName + " AT " + index;
	    }
	}

	return result;

    }

    public List<String> printConflicts(XMIResource left, XMIResource right, XMIResource origin, EList<Conflict> conflicts) throws FileNotFoundException, IOException {
	System.out.println("\nEMF COMPARE CONFLICTS:");
	int conflictCount = 0;
	for (Conflict conflict : conflicts) {
	    String runningLeftString = "";
	    String runningRightString = "";
	    Diff runningLeftDiff = null;
	    Diff runningRightDiff = null;

	    if (conflict.getKind() == ConflictKind.REAL || conflict.getKind() == ConflictKind.PSEUDO) {

		conflictCount++;

		EList<Diff> leftDiffs = conflict.getLeftDifferences();
		EList<Diff> rightDiffs = conflict.getRightDifferences();
		boolean foundConflict = false;

		Iterator<Diff> leftIterator = leftDiffs.iterator();
		Iterator<Diff> rightIterator = rightDiffs.iterator();
		while (leftIterator.hasNext() || rightIterator.hasNext()) {
		    Diff leftDiff = null;
		    if (leftIterator.hasNext()) {
			leftDiff = leftIterator.next();
			runningLeftDiff = leftDiff;
		    }
		    Diff rightDiff = null;
		    if (rightIterator.hasNext()) {
			rightDiff = rightIterator.next();
			runningRightDiff = rightDiff;
		    }

		    String leftString = "";
		    String rightString = "";
		    if (leftDiff instanceof AttributeChange || leftDiff instanceof ReferenceChange || leftDiff instanceof ResourceAttachmentChange) {
			leftString = diffToString(left, origin, leftDiff);
		    }
		    if (rightDiff instanceof AttributeChange || rightDiff instanceof ReferenceChange || rightDiff instanceof ResourceAttachmentChange) {
			rightString = diffToString(right, origin, rightDiff);
		    }

		    System.out.println(conflictCount + ": " + leftString + " <-> " + rightString);

		    if (!leftString.equals("")) {
			if (!runningLeftString.contains("DELETE")) {
			    runningLeftString = leftString;
			}
		    }
		    if (!rightString.equals("")) {
			if (!runningRightString.contains("DELETE")) {
			    runningRightString = rightString;
			}
		    }

		    System.console();
		}

		emfcConflicts.add(runningLeftString.trim() + " <-> " + runningRightString.trim());

		System.console();
	    }
	}
	System.out.println("Conflict count:" + conflictCount);
	if (conflictCount > 0) {
	    System.console();
	}
	return emfcConflicts;
    }

    public List<String> printEMFCompareDiffs(Resource left, Resource right, EList<Diff> diffs) throws FileNotFoundException, IOException {
	Set<String> set = new HashSet<>();
	for (Diff diff : diffs) {
	    String feature = null;
	    String id = null;
	    String value = null;

	    if (diff.getMatch().getLeft() != null) {
		id = left.getURIFragment(diff.getMatch().getLeft());
	    } else {
		id = right.getURIFragment(diff.getMatch().getRight());
	    }

	    if (diff instanceof AttributeChange) {
		feature = ((AttributeChange) diff).getAttribute().getName();
		value = String.valueOf(((AttributeChange) diff).getValue());
	    } else if (diff instanceof ReferenceChange) {
		feature = ((ReferenceChange) diff).getReference().getName();
		EObject eObject = ((ReferenceChange) diff).getValue();
		value = left.getURIFragment(eObject);
		if (value == null || "/-1".equals(value)) {
		    value = right.getURIFragment(eObject);
		}
	    } else if (diff instanceof MultiplicityElementChange) {
		continue;
	    } else if (diff instanceof ResourceAttachmentChange) {
		feature = "resource";
		value = new String(id);
		id = new String(feature);
	    } else if (diff instanceof AssociationChange) {
		continue;
	    } else if (diff instanceof DirectedRelationshipChange) {
		continue;
	    } else {
		System.out.println("UNHANDLED DIFF: " + diff.getClass().getName());
	    }

	    String x = id + "." + feature + "." + value + "." + diff.getKind();
	    set.add(x.trim());
	}
	// System.out.println("Before Merge Diffs: " + diffs.size());

	List<String> list = new ArrayList<>(set);
	// Collections.sort(list);

	// System.out.println("\nEXPORT FOR COMPARISON WITH CBP:");
	for (String item : list) {
	    System.out.println(item);
	}
	System.out.println("Diffs Size: " + list.size());
	return list;
    }

    public List<String> getEmfcConflicts() {
	return emfcConflicts;
    }

}
