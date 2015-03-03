package jbse.algo;

import static jbse.algo.Util.ensureClassCreatedAndInitialized;
import static jbse.algo.Util.lookupMethodImpl;
import static jbse.algo.Util.throwNew;
import static jbse.algo.Util.throwVerifyError;
import static jbse.bc.Offsets.INVOKEDYNAMICINTERFACE_OFFSET;
import static jbse.bc.Offsets.INVOKESPECIALSTATICVIRTUAL_OFFSET;
import static jbse.bc.Signatures.ABSTRACT_METHOD_ERROR;
import static jbse.bc.Signatures.ILLEGAL_ACCESS_ERROR;
import static jbse.bc.Signatures.INCOMPATIBLE_CLASS_CHANGE_ERROR;
import static jbse.bc.Signatures.NO_CLASS_DEFINITION_FOUND_ERROR;
import static jbse.bc.Signatures.NO_SUCH_METHOD_ERROR;
import static jbse.bc.Signatures.NULL_POINTER_EXCEPTION;

import jbse.algo.exc.CannotManageStateException;
import jbse.algo.exc.InterruptException;
import jbse.bc.ClassFile;
import jbse.bc.ClassHierarchy;
import jbse.bc.Signature;
import jbse.bc.exc.BadClassFileException;
import jbse.bc.exc.ClassFileNotFoundException;
import jbse.bc.exc.IncompatibleClassFileException;
import jbse.bc.exc.InvalidIndexException;
import jbse.bc.exc.MethodAbstractException;
import jbse.bc.exc.MethodCodeNotFoundException;
import jbse.bc.exc.MethodNotAccessibleException;
import jbse.bc.exc.MethodNotFoundException;
import jbse.bc.exc.NullMethodReceiverException;
import jbse.common.Util;
import jbse.common.exc.ClasspathException;
import jbse.common.exc.UnexpectedInternalException;
import jbse.dec.exc.DecisionException;
import jbse.jvm.exc.FailureException;
import jbse.mem.State;
import jbse.mem.exc.ContradictionException;
import jbse.mem.exc.InvalidProgramCounterException;
import jbse.mem.exc.InvalidSlotException;
import jbse.mem.exc.OperandStackEmptyException;
import jbse.mem.exc.ThreadStackEmptyException;
import jbse.val.Reference;
import jbse.val.Value;

final class Algo_INVOKEX implements Algorithm {
    boolean isInterface;
    boolean isSpecial;
    boolean isStatic;
    
    @Override
    public void exec(State state, ExecutionContext ctx)
    throws CannotManageStateException, ThreadStackEmptyException, 
    DecisionException, ContradictionException, 
    FailureException, ClasspathException, InterruptException {
        //gets index  
        final int index;
        try {
            final byte tmp1 = state.getInstruction(1);
            final byte tmp2 = state.getInstruction(2);
            index = Util.byteCat(tmp1, tmp2);
        } catch (InvalidProgramCounterException e) {
            throwVerifyError(state);
            return;
        }

        final String currentClassName = state.getCurrentMethodSignature().getClassName();
        final ClassHierarchy hier = state.getClassHierarchy();

        //gets the signature of the method to be invoked
        //and calculates the program counter offset
        final Signature methodSignature;
        final int pcOffset;
        try {
            if (this.isInterface) {
                methodSignature = hier.getClassFile(currentClassName).getInterfaceMethodSignature(index);
                pcOffset = INVOKEDYNAMICINTERFACE_OFFSET;
            } else {
                methodSignature = hier.getClassFile(currentClassName).getMethodSignature(index);
                pcOffset = INVOKESPECIALSTATICVIRTUAL_OFFSET;
            }
        } catch (InvalidIndexException e) {
            throwVerifyError(state);
            return;
        } catch (BadClassFileException e) {
            //this should never happen
            throw new UnexpectedInternalException(e);
        }
        
        //performs method resolution
        final Signature methodSignatureResolved;
        try {
            methodSignatureResolved = hier.resolveMethod(currentClassName, methodSignature, this.isInterface);
        } catch (ClassFileNotFoundException e) {
            throwNew(state, NO_CLASS_DEFINITION_FOUND_ERROR);
            return;
        } catch (IncompatibleClassFileException e) {
            throwNew(state, INCOMPATIBLE_CLASS_CHANGE_ERROR);
            return;
        } catch (MethodAbstractException e) {
            throwNew(state, ABSTRACT_METHOD_ERROR);
            return;
        } catch (MethodNotFoundException e) {
            throwNew(state, NO_SUCH_METHOD_ERROR);
            return;
        } catch (MethodNotAccessibleException e) {
            throwNew(state, ILLEGAL_ACCESS_ERROR);
            return;
        } catch (BadClassFileException e) {
            throwVerifyError(state);
            return;
        }
        
        //checks the resolved method; note that more checks 
        //are done later, by the last call to state.pushFrame
        //TODO this check is ok for invoke[interface/static/virtual], which checks should we do for invokespecial, if any?
        try {
            final ClassFile classFileResolved = hier.getClassFile(methodSignatureResolved.getClassName());
            if (classFileResolved.isMethodStatic(methodSignatureResolved) != this.isStatic) {
                throwNew(state, INCOMPATIBLE_CLASS_CHANGE_ERROR);
                return;
            }
        } catch (BadClassFileException | MethodNotFoundException e) {
            //this should never happen after resolution
            throw new UnexpectedInternalException(e);
        }
        
        //possibly creates and initializes the class of the resolved method
        if (this.isStatic) { //TODO should we initialize the class also in the invoke[interface/special/virtual] cases, or at least check?
            try {
                ensureClassCreatedAndInitialized(state, methodSignatureResolved.getClassName(), ctx.decisionProcedure);
            } catch (BadClassFileException e) {
                //this should never happen after resolution 
                throw new UnexpectedInternalException(e);
            }
        }
        
        //looks for the method implementation
        ClassFile classMethodImpl;
        boolean isNative;
        try {
            final boolean isVirtualInterface = !this.isStatic && !this.isSpecial;
            final String receiverClassName;
            if (isVirtualInterface) {
                final Reference thisRef = state.peekReceiverArg(methodSignatureResolved);
                if (state.isNull(thisRef)) {
                    throwNew(state, NULL_POINTER_EXCEPTION);
                    return;
                }
                receiverClassName = state.getObject(thisRef).getType();
            } else {
                receiverClassName = null;
            }
            classMethodImpl = lookupMethodImpl(state, methodSignatureResolved, this.isStatic, this.isSpecial, receiverClassName);
            isNative = classMethodImpl.isMethodNative(methodSignatureResolved);
        } catch (IncompatibleClassFileException e) {
            //TODO is it ok?
            throwNew(state, INCOMPATIBLE_CLASS_CHANGE_ERROR);
            return;
        } catch (NullPointerException | BadClassFileException e) {
            throwVerifyError(state);
            return;
        } catch (MethodNotFoundException e) {
            //it is still possible that the method
            //is meta-overridden
            classMethodImpl = null;
            isNative = false;
        }
        
        //builds a signature for the method implementation;
        //falls back to the signature of the resolved method
        //if there is no implementation (just for the case of
        //a meta-level implementation)
        final Signature methodSignatureImpl = (classMethodImpl == null ? methodSignatureResolved : 
            new Signature(classMethodImpl.getClassName(), methodSignatureResolved.getDescriptor(), methodSignatureResolved.getName()));
        
        //looks for a meta-level implementation, and in case 
        //delegates the responsibility to the dispatcherMeta
        try {
            if (ctx.dispatcherMeta.isMeta(hier, methodSignatureImpl)) {
                final Algorithm algo = ctx.dispatcherMeta.select(methodSignatureImpl);
                algo.exec(state, ctx);
                return;
            }
        } catch (BadClassFileException | MethodNotFoundException e) {
            //this should never happen after resolution 
            throw new UnexpectedInternalException(e);
        }
        
        //if the method has no implementation, raises AbstractMethodError
        try {
            if (classMethodImpl == null || classMethodImpl.isMethodAbstract(methodSignatureImpl)) {
                throwNew(state, ABSTRACT_METHOD_ERROR);
                return;
            }
        } catch (MethodNotFoundException e) {
            //this should never happen after resolution 
            throw new UnexpectedInternalException(e);
        }     
        
        //pops the args from the operand stack        
        final Value[] args;
        try {
            args = state.popMethodCallArgs(methodSignatureImpl, this.isStatic);
        } catch (OperandStackEmptyException e) {
            throwVerifyError(state);
            return;
        }
        
        //if the method is native, delegates the responsibility 
        //to the native invoker
        if (isNative) {
            ctx.nativeInvoker.doInvokeNative(state, methodSignatureResolved, args, pcOffset);
            return;
        }
          
        //pushes the frame
        try {
            state.pushFrame(methodSignatureImpl, false, pcOffset, args);
        } catch (NullMethodReceiverException e) {
            //this should never happen after method implementation lookup
            throw new UnexpectedInternalException(e);
        } catch (InvalidProgramCounterException | InvalidSlotException e) {
            //TODO is it ok?
            throwVerifyError(state);
        } catch (BadClassFileException | MethodNotFoundException | 
                 MethodCodeNotFoundException e) {
            //this should never happen
            throw new UnexpectedInternalException(e);
        }
    }
}