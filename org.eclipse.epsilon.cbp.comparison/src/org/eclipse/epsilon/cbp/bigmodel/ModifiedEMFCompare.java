package org.eclipse.epsilon.cbp.bigmodel;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.ComparisonCanceledException;
import org.eclipse.emf.compare.Conflict;
import org.eclipse.emf.compare.ConflictKind;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompareMessages;
import org.eclipse.emf.compare.Match;
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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

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
		endInterval = System.nanoTime();
		endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		this.setDiffTime(endInterval - startInterval);
		this.setDiffMemory(endMemory - startMemory);

		this.comparisonTime = this.matchTime + this.diffTime;
		this.comparisonMemory = this.matchMemory + this.diffMemory;

		// System.out.println("Total Diffs = " +
		// comparison.getDifferences().size());

		// For now, we only the match and diff steps
		// monitor.worked(1);
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: POST-DIFF with "
		// //$NON-NLS-1$
		// + postProcessors.size() + " post-processors"); //$NON-NLS-1$
		// }
		// postDiff(comparison, postProcessors, subMonitor);
		// monitor.worked(1);
		//
		// if (!hasToStop(comparison, monitor)) {
		// if (LOGGER.isInfoEnabled()) {
		// LOGGER.info("compare() - starting step: REQUIREMENTS");
		// //$NON-NLS-1$
		// }
		// reqEngine.computeRequirements(comparison, subMonitor);
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
		// //$NON-NLS-1$
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
		if (LOGGER.isInfoEnabled()) {
		    LOGGER.info("compare() - starting step: CONFLICT");
		    // $NON-NLS-1$
		}
		System.gc();
		startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		startInterval = System.nanoTime();
		detectConflicts(comparison, postProcessors, subMonitor);
		endInterval = System.nanoTime();
		endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		this.setConflictTime(endInterval - startInterval);
		System.gc();
		this.setConflictMemory(endMemory - startMemory);

		this.comparisonTime = this.matchTime + this.diffTime + this.conflictTime;
		this.comparisonMemory = this.matchMemory + this.diffMemory + this.conflictMemory;
		
		
//		monitor.worked(1);
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
//	Iterator<Conflict> iterator = comparison.getConflicts().iterator();
//	while(iterator.hasNext()) {
//	    if (iterator.next().getKind() ==  ConflictKind.PSEUDO) {
//		iterator.remove();
//	    }
//	}
	return comparison.getConflicts();
    }
    
    public List<Conflict> getRealConflicts() {
	List<Conflict> conflicts = new ArrayList(comparison.getConflicts());
	Iterator<Conflict> iterator = conflicts.iterator();
	while(iterator.hasNext()) {
	    if (iterator.next().getKind() ==  ConflictKind.PSEUDO) {
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

    
}
