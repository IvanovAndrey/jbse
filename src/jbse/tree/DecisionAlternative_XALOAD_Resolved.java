package jbse.tree;

import jbse.mem.Util;
import jbse.val.Expression;
import jbse.val.ReferenceSymbolic;
import jbse.val.Value;

/**
 * {@link DecisionAlternative_XALOAD} for the case a read access to an array
 * returned a resolved {@link ReferenceSymbolic} or a numeric value,  
 * either concrete or symbolic.
 * 
 * @author Pietro Braione
 */
public final class DecisionAlternative_XALOAD_Resolved extends DecisionAlternative_XALOAD implements DecisionAlternative_XYLOAD_GETX_Resolved {
	private static final int RESOLVED_BN = 1;
	
	private final Value valueToLoad;
	private final boolean fresh;
    private final boolean isTrivial;
	private final boolean isConcrete;

	/**
	 * Constructor, nonconcrete.
	 * 
	 * @param arrayAccessExpression The array access {@link Expression}.
	 * @param valueToLoad The {@link Value} loaded from the array.
	 * @param fresh {@code true} iff {@code valToLoad} is fresh, i.e., 
	 *        is not stored in the array and, therefore, must be written
	 *        back to the array.
	 */
	public DecisionAlternative_XALOAD_Resolved(Expression arrayAccessExpression, Value valueToLoad, boolean fresh) {
		super(ALT_CODE + "R", arrayAccessExpression, RESOLVED_BN);
		this.valueToLoad = valueToLoad;
		this.fresh = fresh;
        this.isTrivial = (arrayAccessExpression == null);
		this.isConcrete = this.isTrivial && !Util.isSymbolicReference(valueToLoad);
	}

	/**
	 * Constructor, concrete.
	 * 
	 * @param valueToLoad The {@link Value} loaded from the array.
	 * @param fresh {@code true} iff {@code valToLoad} is fresh, i.e., 
	 *        is not stored in the array and, therefore, must be written
	 *        back to the array.
	 */
	public DecisionAlternative_XALOAD_Resolved(Value valueToLoad, boolean fresh) {
		this(null, valueToLoad, fresh);
	}
	
	public boolean isValueFresh() {
		return this.fresh;
	}
	
	@Override
	public void accept(VisitorDecisionAlternative_XALOAD v) throws Exception {
		v.visitDecisionAlternative_XALOAD_Resolved(this);
	}

	@Override
	public boolean concrete() {
		return this.isConcrete;
	}

	@Override
	public boolean trivial() {
		return this.isTrivial;
	}

	@Override
	public Value getValueToLoad() {
		return this.valueToLoad;
	}
	//TODO equals, hashCode, toString
}
