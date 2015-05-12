package jbse.algo;

import jbse.mem.State;

public final class BytecodeData_0 extends BytecodeData {
    @Override
    protected void read(State state) throws InterruptException {
        //nothing to do
    }
    
    /**
     * Do not instantiate!
     */
    private BytecodeData_0() {
        //nothing to do
    }
    
    public static BytecodeData_0 get() {
        return new BytecodeData_0();
    }
}
