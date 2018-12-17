package jbse.algo;

import static jbse.bc.Opcodes.*;
import static jbse.common.Type.BYTE;
import static jbse.common.Type.CHAR;
import static jbse.common.Type.DOUBLE;
import static jbse.common.Type.FLOAT;
import static jbse.common.Type.INT;
import static jbse.common.Type.LONG;
import static jbse.common.Type.REFERENCE;
import static jbse.common.Type.SHORT;
import static jbse.val.Operator.EQ;
import static jbse.val.Operator.GE;
import static jbse.val.Operator.GT;
import static jbse.val.Operator.LE;
import static jbse.val.Operator.LT;
import static jbse.val.Operator.NE;

import jbse.bc.Dispatcher;
import jbse.common.exc.UnexpectedInternalException;

/**
 * A {@link Dispatcher} returning the {@link Algorithm}s to process 
 * every bytecode of the symbolic execution. 
 * 
 * @author Pietro Braione
 *
 */
public class DispatcherBytecodeAlgorithm extends Dispatcher<Byte, Algorithm<?, ?, ?, ?, ?>> {
    private final Action_PREINIT      action_PREINIT       = new Action_PREINIT();
    private final Action_INIT         action_INIT          = new Action_INIT();
    private final Algo_NOTALLOWED     algo_NOTALLOWED      = new Algo_NOTALLOWED();
    private final Algo_ACONST_NULL    algo_ACONST_NULL     = new Algo_ACONST_NULL();
    private final Algo_ANEWARRAY      algo_ANEWARRAY       = new Algo_ANEWARRAY();
    private final Algo_ARRAYLENGTH    algo_ARRAYLENGTH     = new Algo_ARRAYLENGTH();
    private final Algo_ATHROW         algo_ATHROW          = new Algo_ATHROW();
    private final Algo_BIPUSH         algo_BIPUSH          = new Algo_BIPUSH();
    private final Algo_CHECKCAST      algo_CHECKCAST       = new Algo_CHECKCAST();
    private final Algo_XCONST_Y       algo_DCONST_0        = new Algo_XCONST_Y(DOUBLE, 0);
    private final Algo_XCONST_Y       algo_DCONST_1        = new Algo_XCONST_Y(DOUBLE, 1);
    private final Algo_X2Y            algo_D2F             = new Algo_X2Y(DOUBLE, FLOAT);
    private final Algo_X2Y            algo_D2I             = new Algo_X2Y(DOUBLE, INT);
    private final Algo_X2Y            algo_D2L             = new Algo_X2Y(DOUBLE, LONG);
    private final Algo_DUPX_Y         algo_DUP_X1          = new Algo_DUPX_Y(true, true);
    private final Algo_DUPX_Y         algo_DUP_X2          = new Algo_DUPX_Y(true, false);
    private final Algo_DUPX_Y         algo_DUP2_X1         = new Algo_DUPX_Y(false, true);
    private final Algo_DUPX_Y         algo_DUP2_X2         = new Algo_DUPX_Y(false, false);
    private final Algo_DUPX           algo_DUP             = new Algo_DUPX(true);
    private final Algo_DUPX           algo_DUP2            = new Algo_DUPX(false);
    private final Algo_X2Y            algo_F2D             = new Algo_X2Y(FLOAT, DOUBLE);
    private final Algo_X2Y            algo_F2I             = new Algo_X2Y(FLOAT, INT);
    private final Algo_X2Y            algo_F2L             = new Algo_X2Y(FLOAT, LONG);
    private final Algo_XCONST_Y       algo_FCONST_0        = new Algo_XCONST_Y(FLOAT, 0);
    private final Algo_XCONST_Y       algo_FCONST_1        = new Algo_XCONST_Y(FLOAT, 1);
    private final Algo_XCONST_Y       algo_FCONST_2        = new Algo_XCONST_Y(FLOAT, 2);
    private final Algo_GETFIELD       algo_GETFIELD        = new Algo_GETFIELD();
    private final Algo_GETSTATIC      algo_GETSTATIC       = new Algo_GETSTATIC();
    private final Algo_GOTOX          algo_GOTO            = new Algo_GOTOX(false);
    private final Algo_GOTOX          algo_GOTO_W          = new Algo_GOTOX(true);
    private final Algo_X2Y            algo_I2B             = new Algo_X2Y(INT, BYTE);
    private final Algo_X2Y            algo_I2C             = new Algo_X2Y(INT, CHAR);
    private final Algo_X2Y            algo_I2S             = new Algo_X2Y(INT, SHORT);
    private final Algo_XCONST_Y       algo_ICONST_M1       = new Algo_XCONST_Y(INT, -1);
    private final Algo_XCONST_Y       algo_ICONST_0        = new Algo_XCONST_Y(INT, 0);
    private final Algo_XCONST_Y       algo_ICONST_1        = new Algo_XCONST_Y(INT, 1);
    private final Algo_XCONST_Y       algo_ICONST_2        = new Algo_XCONST_Y(INT, 2);
    private final Algo_XCONST_Y       algo_ICONST_3        = new Algo_XCONST_Y(INT, 3);
    private final Algo_XCONST_Y       algo_ICONST_4        = new Algo_XCONST_Y(INT, 4);
    private final Algo_XCONST_Y       algo_ICONST_5        = new Algo_XCONST_Y(INT, 5);
    private final Algo_X2Y            algo_I2D             = new Algo_X2Y(INT, DOUBLE);
    private final Algo_X2Y            algo_I2F             = new Algo_X2Y(INT, FLOAT);
    private final Algo_X2Y            algo_I2L             = new Algo_X2Y(INT, LONG);
    private final Algo_IF_ACMPX_XNULL algo_IF_ACMPEQ       = new Algo_IF_ACMPX_XNULL(false, true);
    private final Algo_IF_ACMPX_XNULL algo_IF_ACMPNE       = new Algo_IF_ACMPX_XNULL(false, false);
    private final Algo_IF_ACMPX_XNULL algo_IFNONNULL       = new Algo_IF_ACMPX_XNULL(true, false);
    private final Algo_IF_ACMPX_XNULL algo_IFNULL          = new Algo_IF_ACMPX_XNULL(true, true);
    private final Algo_IFX            algo_IFEQ            = new Algo_IFX(true, EQ);
    private final Algo_IFX            algo_IFGE            = new Algo_IFX(true, GE);
    private final Algo_IFX            algo_IFGT            = new Algo_IFX(true, GT);
    private final Algo_IFX            algo_IFLE            = new Algo_IFX(true, LE);
    private final Algo_IFX            algo_IFLT            = new Algo_IFX(true, LT);
    private final Algo_IFX            algo_IFNE            = new Algo_IFX(true, NE);
    private final Algo_IFX            algo_IF_ICMPEQ       = new Algo_IFX(false, EQ);
    private final Algo_IFX            algo_IF_ICMPGE       = new Algo_IFX(false, GE);
    private final Algo_IFX            algo_IF_ICMPGT       = new Algo_IFX(false, GT);
    private final Algo_IFX            algo_IF_ICMPLE       = new Algo_IFX(false, LE);
    private final Algo_IFX            algo_IF_ICMPLT       = new Algo_IFX(false, LT);
    private final Algo_IFX            algo_IF_ICMPNE       = new Algo_IFX(false, NE);
    private final Algo_IINC           algo_IINC            = new Algo_IINC();
    private final Algo_INSTANCEOF     algo_INSTANCEOF      = new Algo_INSTANCEOF();
    private final Algo_UNSUPPORTED    algo_INVOKEDYNAMIC   = new Algo_UNSUPPORTED("INVOKEDYNAMIC"); 
    //private final Algo_INVOKEHANDLE   algo_INVOKEHANDLE    = new Algo_INVOKEHANDLE();
    private final Algo_INVOKEX        algo_INVOKEINTERFACE = new Algo_INVOKEX(true, false, false);
    private final Algo_INVOKEX        algo_INVOKESPECIAL   = new Algo_INVOKEX(false, true, false);
    private final Algo_INVOKEX        algo_INVOKESTATIC    = new Algo_INVOKEX(false, false, true);
    private final Algo_INVOKEX        algo_INVOKEVIRTUAL   = new Algo_INVOKEX(false, false, false);
    private final Algo_JSRX           algo_JSR             = new Algo_JSRX(false);
    private final Algo_JSRX           algo_JSR_W           = new Algo_JSRX(true);
    private final Algo_XCONST_Y       algo_LCONST_0        = new Algo_XCONST_Y(LONG, 0);
    private final Algo_XCONST_Y       algo_LCONST_1        = new Algo_XCONST_Y(LONG, 1);
    private final Algo_X2Y            algo_L2D             = new Algo_X2Y(LONG, DOUBLE);
    private final Algo_X2Y            algo_L2F             = new Algo_X2Y(LONG, FLOAT);
    private final Algo_X2Y            algo_L2I             = new Algo_X2Y(LONG, INT);
    private final Algo_LDCX_Y         algo_LDC             = new Algo_LDCX_Y(false, true);
    private final Algo_LDCX_Y         algo_LDC_W           = new Algo_LDCX_Y(true, true);
    private final Algo_LDCX_Y         algo_LDC2_W          = new Algo_LDCX_Y(true, false);
    private final Algo_XSWITCH        algo_LOOKUPSWITCH    = new Algo_XSWITCH(false);
    private final Algo_MONITORX       algo_MONITORX        = new Algo_MONITORX();
    private final Algo_MULTIANEWARRAY algo_MULTIANEWARRAY  = new Algo_MULTIANEWARRAY();
    private final Algo_NEW            algo_NEW             = new Algo_NEW();
    private final Algo_NEWARRAY       algo_NEWARRAY        = new Algo_NEWARRAY();
    private final Algo_NOP            algo_NOP             = new Algo_NOP();
    private final Algo_POPX           algo_POP             = new Algo_POPX(true);
    private final Algo_POPX           algo_POP2            = new Algo_POPX(false);
    private final Algo_PUTFIELD       algo_PUTFIELD        = new Algo_PUTFIELD();
    private final Algo_PUTSTATIC      algo_PUTSTATIC       = new Algo_PUTSTATIC();
    private final Algo_RET            algo_RET             = new Algo_RET();
    private final Algo_RETURN         algo_RETURN          = new Algo_RETURN();
    private final Algo_SIPUSH         algo_SIPUSH          = new Algo_SIPUSH();
    private final Algo_SWAP           algo_SWAP            = new Algo_SWAP();
    private final Algo_XSWITCH        algo_TABLESWITCH     = new Algo_XSWITCH(true);
    private final Algo_WIDE           algo_WIDE            = new Algo_WIDE();
    private final Algo_XADD           algo_XADD            = new Algo_XADD();
    private final Algo_XALOAD         algo_XALOAD          = new Algo_XALOAD();
    private final Algo_XAND           algo_XAND            = new Algo_XAND();
    private final Algo_XASTORE        algo_XASTORE         = new Algo_XASTORE();
    private final Algo_XCMPY          algo_XCMPY           = new Algo_XCMPY();
    private final Algo_XDIV           algo_XDIV            = new Algo_XDIV();
    private final Algo_XLOAD          algo_XLOAD           = new Algo_XLOAD();
    private final Algo_XLOAD_Y        algo_XLOAD_0         = new Algo_XLOAD_Y(0);
    private final Algo_XLOAD_Y        algo_XLOAD_1         = new Algo_XLOAD_Y(1);
    private final Algo_XLOAD_Y        algo_XLOAD_2         = new Algo_XLOAD_Y(2);
    private final Algo_XLOAD_Y        algo_XLOAD_3         = new Algo_XLOAD_Y(3);
    private final Algo_XNEG           algo_XNEG            = new Algo_XNEG();
    private final Algo_XOR            algo_XOR             = new Algo_XOR();
    private final Algo_XMUL           algo_XMUL            = new Algo_XMUL();
    private final Algo_XREM           algo_XREM            = new Algo_XREM();
    private final Algo_XRETURN        algo_ARETURN         = new Algo_XRETURN(REFERENCE);
    private final Algo_XRETURN        algo_DRETURN         = new Algo_XRETURN(DOUBLE);
    private final Algo_XRETURN        algo_FRETURN         = new Algo_XRETURN(FLOAT);
    private final Algo_XRETURN        algo_IRETURN         = new Algo_XRETURN(INT);
    private final Algo_XRETURN        algo_LRETURN         = new Algo_XRETURN(LONG);
    private final Algo_XSHL           algo_XSHL            = new Algo_XSHL();
    private final Algo_XSHR           algo_XSHR            = new Algo_XSHR();
    private final Algo_XSTORE         algo_XSTORE          = new Algo_XSTORE();
    private final Algo_XSTORE_Y       algo_XSTORE_0        = new Algo_XSTORE_Y(0);
    private final Algo_XSTORE_Y       algo_XSTORE_1        = new Algo_XSTORE_Y(1);
    private final Algo_XSTORE_Y       algo_XSTORE_2        = new Algo_XSTORE_Y(2);
    private final Algo_XSTORE_Y       algo_XSTORE_3        = new Algo_XSTORE_Y(3);
    private final Algo_XSUB           algo_XSUB            = new Algo_XSUB();
    private final Algo_XUSHR          algo_XUSHR           = new Algo_XUSHR();
    private final Algo_XXOR           algo_XXOR            = new Algo_XXOR();

    public DispatcherBytecodeAlgorithm() {
        //implemented bytecodes (sometimes with limited support)
        setCase(OP_NOP,             () -> this.algo_NOP);
        setCase(OP_ACONST_NULL,     () -> this.algo_ACONST_NULL);
        setCase(OP_ICONST_M1,       () -> this.algo_ICONST_M1);
        setCase(OP_ICONST_0,        () -> this.algo_ICONST_0);
        setCase(OP_ICONST_1,        () -> this.algo_ICONST_1);
        setCase(OP_ICONST_2,        () -> this.algo_ICONST_2);
        setCase(OP_ICONST_3,        () -> this.algo_ICONST_3);
        setCase(OP_ICONST_4,        () -> this.algo_ICONST_4);
        setCase(OP_ICONST_5,        () -> this.algo_ICONST_5);
        setCase(OP_LCONST_0,        () -> this.algo_LCONST_0);
        setCase(OP_LCONST_1,        () -> this.algo_LCONST_1);
        setCase(OP_FCONST_0,        () -> this.algo_FCONST_0);
        setCase(OP_FCONST_1,        () -> this.algo_FCONST_1);
        setCase(OP_FCONST_2,        () -> this.algo_FCONST_2);
        setCase(OP_DCONST_0,        () -> this.algo_DCONST_0);
        setCase(OP_DCONST_1,        () -> this.algo_DCONST_1);
        setCase(OP_BIPUSH,          () -> this.algo_BIPUSH);
        setCase(OP_SIPUSH,          () -> this.algo_SIPUSH);
        setCase(OP_LDC,             () -> this.algo_LDC);
        setCase(OP_LDC_W,           () -> this.algo_LDC_W);
        setCase(OP_LDC2_W,          () -> this.algo_LDC2_W);
        setCase(OP_ILOAD,           () -> this.algo_XLOAD);
        setCase(OP_LLOAD,           () -> this.algo_XLOAD);
        setCase(OP_FLOAD,           () -> this.algo_XLOAD);
        setCase(OP_DLOAD,           () -> this.algo_XLOAD);
        setCase(OP_ALOAD,           () -> this.algo_XLOAD);
        setCase(OP_ILOAD_0,         () -> this.algo_XLOAD_0);
        setCase(OP_ILOAD_1,         () -> this.algo_XLOAD_1);
        setCase(OP_ILOAD_2,         () -> this.algo_XLOAD_2);
        setCase(OP_ILOAD_3,         () -> this.algo_XLOAD_3);
        setCase(OP_LLOAD_0,         () -> this.algo_XLOAD_0);
        setCase(OP_LLOAD_1,         () -> this.algo_XLOAD_1);
        setCase(OP_LLOAD_2,         () -> this.algo_XLOAD_2);
        setCase(OP_LLOAD_3,         () -> this.algo_XLOAD_3);
        setCase(OP_FLOAD_0,         () -> this.algo_XLOAD_0);
        setCase(OP_FLOAD_1,         () -> this.algo_XLOAD_1);
        setCase(OP_FLOAD_2,         () -> this.algo_XLOAD_2);
        setCase(OP_FLOAD_3,         () -> this.algo_XLOAD_3);
        setCase(OP_DLOAD_0,         () -> this.algo_XLOAD_0);
        setCase(OP_DLOAD_1,         () -> this.algo_XLOAD_1);
        setCase(OP_DLOAD_2,         () -> this.algo_XLOAD_2);
        setCase(OP_DLOAD_3,         () -> this.algo_XLOAD_3);
        setCase(OP_ALOAD_0,         () -> this.algo_XLOAD_0);
        setCase(OP_ALOAD_1,         () -> this.algo_XLOAD_1);
        setCase(OP_ALOAD_2,         () -> this.algo_XLOAD_2);
        setCase(OP_ALOAD_3,         () -> this.algo_XLOAD_3);
        setCase(OP_IALOAD,          () -> this.algo_XALOAD);
        setCase(OP_LALOAD,          () -> this.algo_XALOAD);
        setCase(OP_FALOAD,          () -> this.algo_XALOAD);
        setCase(OP_DALOAD,          () -> this.algo_XALOAD);
        setCase(OP_AALOAD,          () -> this.algo_XALOAD);
        setCase(OP_BALOAD,          () -> this.algo_XALOAD);
        setCase(OP_CALOAD,          () -> this.algo_XALOAD);
        setCase(OP_SALOAD,          () -> this.algo_XALOAD);
        setCase(OP_ISTORE,          () -> this.algo_XSTORE);
        setCase(OP_LSTORE,          () -> this.algo_XSTORE);
        setCase(OP_FSTORE,          () -> this.algo_XSTORE);
        setCase(OP_DSTORE,          () -> this.algo_XSTORE);
        setCase(OP_ASTORE,          () -> this.algo_XSTORE);
        setCase(OP_ISTORE_0,        () -> this.algo_XSTORE_0);
        setCase(OP_ISTORE_1,        () -> this.algo_XSTORE_1);
        setCase(OP_ISTORE_2,        () -> this.algo_XSTORE_2);
        setCase(OP_ISTORE_3,        () -> this.algo_XSTORE_3);
        setCase(OP_LSTORE_0,        () -> this.algo_XSTORE_0);
        setCase(OP_LSTORE_1,        () -> this.algo_XSTORE_1);
        setCase(OP_LSTORE_2,        () -> this.algo_XSTORE_2);
        setCase(OP_LSTORE_3,        () -> this.algo_XSTORE_3);
        setCase(OP_FSTORE_0,        () -> this.algo_XSTORE_0);
        setCase(OP_FSTORE_1,        () -> this.algo_XSTORE_1);
        setCase(OP_FSTORE_2,        () -> this.algo_XSTORE_2);
        setCase(OP_FSTORE_3,        () -> this.algo_XSTORE_3);
        setCase(OP_DSTORE_0,        () -> this.algo_XSTORE_0);
        setCase(OP_DSTORE_1,        () -> this.algo_XSTORE_1);
        setCase(OP_DSTORE_2,        () -> this.algo_XSTORE_2);
        setCase(OP_DSTORE_3,        () -> this.algo_XSTORE_3);
        setCase(OP_ASTORE_0,        () -> this.algo_XSTORE_0);
        setCase(OP_ASTORE_1,        () -> this.algo_XSTORE_1);
        setCase(OP_ASTORE_2,        () -> this.algo_XSTORE_2);
        setCase(OP_ASTORE_3,        () -> this.algo_XSTORE_3);
        setCase(OP_IASTORE,         () -> this.algo_XASTORE);
        setCase(OP_LASTORE,         () -> this.algo_XASTORE);
        setCase(OP_FASTORE,         () -> this.algo_XASTORE);
        setCase(OP_DASTORE,         () -> this.algo_XASTORE);
        setCase(OP_AASTORE,         () -> this.algo_XASTORE);
        setCase(OP_BASTORE,         () -> this.algo_XASTORE);
        setCase(OP_CASTORE,         () -> this.algo_XASTORE);
        setCase(OP_SASTORE,         () -> this.algo_XASTORE);
        setCase(OP_POP,             () -> this.algo_POP);
        setCase(OP_POP2,            () -> this.algo_POP2);
        setCase(OP_DUP,             () -> this.algo_DUP);
        setCase(OP_DUP_X1,          () -> this.algo_DUP_X1);
        setCase(OP_DUP_X2,          () -> this.algo_DUP_X2);
        setCase(OP_DUP2,            () -> this.algo_DUP2);
        setCase(OP_DUP2_X1,         () -> this.algo_DUP2_X1);
        setCase(OP_DUP2_X2,         () -> this.algo_DUP2_X2);
        setCase(OP_SWAP,            () -> this.algo_SWAP);
        setCase(OP_IADD,            () -> this.algo_XADD);
        setCase(OP_LADD,            () -> this.algo_XADD);
        setCase(OP_FADD,            () -> this.algo_XADD);
        setCase(OP_DADD,            () -> this.algo_XADD);
        setCase(OP_ISUB,            () -> this.algo_XSUB);
        setCase(OP_LSUB,            () -> this.algo_XSUB);
        setCase(OP_FSUB,            () -> this.algo_XSUB);
        setCase(OP_DSUB,            () -> this.algo_XSUB);
        setCase(OP_IMUL,            () -> this.algo_XMUL);
        setCase(OP_LMUL,            () -> this.algo_XMUL);
        setCase(OP_FMUL,            () -> this.algo_XMUL);
        setCase(OP_DMUL,            () -> this.algo_XMUL);
        setCase(OP_IDIV,            () -> this.algo_XDIV);
        setCase(OP_LDIV,            () -> this.algo_XDIV);
        setCase(OP_FDIV,            () -> this.algo_XDIV);
        setCase(OP_DDIV,            () -> this.algo_XDIV);
        setCase(OP_IREM,            () -> this.algo_XREM);
        setCase(OP_LREM,            () -> this.algo_XREM);
        setCase(OP_FREM,            () -> this.algo_XREM);
        setCase(OP_DREM,            () -> this.algo_XREM);
        setCase(OP_INEG,            () -> this.algo_XNEG);
        setCase(OP_LNEG,            () -> this.algo_XNEG);
        setCase(OP_FNEG,            () -> this.algo_XNEG);
        setCase(OP_DNEG,            () -> this.algo_XNEG);
        setCase(OP_ISHL,            () -> this.algo_XSHL);
        setCase(OP_LSHL,            () -> this.algo_XSHL);
        setCase(OP_ISHR,            () -> this.algo_XSHR);
        setCase(OP_LSHR,            () -> this.algo_XSHR);
        setCase(OP_IUSHR,           () -> this.algo_XUSHR);
        setCase(OP_LUSHR,           () -> this.algo_XUSHR);
        setCase(OP_IAND,            () -> this.algo_XAND);
        setCase(OP_LAND,            () -> this.algo_XAND);
        setCase(OP_IOR,             () -> this.algo_XOR);
        setCase(OP_LOR,             () -> this.algo_XOR);
        setCase(OP_IXOR,            () -> this.algo_XXOR);
        setCase(OP_LXOR,            () -> this.algo_XXOR);
        setCase(OP_IINC,            () -> this.algo_IINC);
        setCase(OP_I2L,             () -> this.algo_I2L);
        setCase(OP_I2F,             () -> this.algo_I2F);
        setCase(OP_I2D,             () -> this.algo_I2D);
        setCase(OP_L2I,             () -> this.algo_L2I);
        setCase(OP_L2F,             () -> this.algo_L2F);
        setCase(OP_L2D,             () -> this.algo_L2D);
        setCase(OP_F2I,             () -> this.algo_F2I);
        setCase(OP_F2L,             () -> this.algo_F2L);
        setCase(OP_F2D,             () -> this.algo_F2D);
        setCase(OP_D2I,             () -> this.algo_D2I);
        setCase(OP_D2L,             () -> this.algo_D2L);
        setCase(OP_D2F,             () -> this.algo_D2F);
        setCase(OP_I2B,             () -> this.algo_I2B);
        setCase(OP_I2C,             () -> this.algo_I2C);
        setCase(OP_I2S,             () -> this.algo_I2S);
        setCase(OP_LCMP,            () -> this.algo_XCMPY);
        setCase(OP_FCMPL,           () -> this.algo_XCMPY);
        setCase(OP_FCMPG,           () -> this.algo_XCMPY);
        setCase(OP_DCMPL,           () -> this.algo_XCMPY);
        setCase(OP_DCMPG,           () -> this.algo_XCMPY);
        setCase(OP_IFEQ,            () -> this.algo_IFEQ);
        setCase(OP_IFNE,            () -> this.algo_IFNE);
        setCase(OP_IFLT,            () -> this.algo_IFLT);
        setCase(OP_IFGE,            () -> this.algo_IFGE);
        setCase(OP_IFGT,            () -> this.algo_IFGT);
        setCase(OP_IFLE,            () -> this.algo_IFLE);
        setCase(OP_IF_ICMPEQ,       () -> this.algo_IF_ICMPEQ);
        setCase(OP_IF_ICMPNE,       () -> this.algo_IF_ICMPNE);
        setCase(OP_IF_ICMPLT,       () -> this.algo_IF_ICMPLT);
        setCase(OP_IF_ICMPGE,       () -> this.algo_IF_ICMPGE);
        setCase(OP_IF_ICMPGT,       () -> this.algo_IF_ICMPGT);
        setCase(OP_IF_ICMPLE,       () -> this.algo_IF_ICMPLE);
        setCase(OP_IF_ACMPEQ,       () -> this.algo_IF_ACMPEQ);
        setCase(OP_IF_ACMPNE,       () -> this.algo_IF_ACMPNE);
        setCase(OP_GOTO,            () -> this.algo_GOTO);
        setCase(OP_JSR,             () -> this.algo_JSR);
        setCase(OP_RET,             () -> this.algo_RET);
        setCase(OP_TABLESWITCH,     () -> this.algo_TABLESWITCH);
        setCase(OP_LOOKUPSWITCH,    () -> this.algo_LOOKUPSWITCH);
        setCase(OP_IRETURN,         () -> this.algo_IRETURN);
        setCase(OP_LRETURN,         () -> this.algo_LRETURN);
        setCase(OP_FRETURN,         () -> this.algo_FRETURN);
        setCase(OP_DRETURN,         () -> this.algo_DRETURN);
        setCase(OP_ARETURN,         () -> this.algo_ARETURN);
        setCase(OP_RETURN,          () -> this.algo_RETURN);
        setCase(OP_GETSTATIC,       () -> this.algo_GETSTATIC);
        setCase(OP_PUTSTATIC,       () -> this.algo_PUTSTATIC);
        setCase(OP_GETFIELD,        () -> this.algo_GETFIELD);
        setCase(OP_PUTFIELD,        () -> this.algo_PUTFIELD);
        setCase(OP_INVOKEVIRTUAL,   () -> this.algo_INVOKEVIRTUAL);
        setCase(OP_INVOKESPECIAL,   () -> this.algo_INVOKESPECIAL);
        setCase(OP_INVOKESTATIC,    () -> this.algo_INVOKESTATIC);
        setCase(OP_INVOKEINTERFACE, () -> this.algo_INVOKEINTERFACE);
        setCase(OP_INVOKEDYNAMIC,   () -> this.algo_INVOKEDYNAMIC);
        setCase(OP_NEW,             () -> this.algo_NEW);
        setCase(OP_NEWARRAY,        () -> this.algo_NEWARRAY);
        setCase(OP_ANEWARRAY,       () -> this.algo_ANEWARRAY);
        setCase(OP_ARRAYLENGTH,     () -> this.algo_ARRAYLENGTH);
        setCase(OP_ATHROW,          () -> this.algo_ATHROW);
        setCase(OP_CHECKCAST,       () -> this.algo_CHECKCAST);
        setCase(OP_INSTANCEOF,      () -> this.algo_INSTANCEOF);
        setCase(OP_MONITORENTER,    () -> this.algo_MONITORX);
        setCase(OP_MONITOREXIT,     () -> this.algo_MONITORX);
        setCase(OP_WIDE,            () -> this.algo_WIDE);
        setCase(OP_MULTIANEWARRAY,  () -> this.algo_MULTIANEWARRAY);
        setCase(OP_IFNULL,          () -> this.algo_IFNULL);
        setCase(OP_IFNONNULL,       () -> this.algo_IFNONNULL);
        setCase(OP_GOTO_W,          () -> this.algo_GOTO_W);
        setCase(OP_JSR_W,           () -> this.algo_JSR_W);
        setCase(OP_BREAKPOINT,      () -> this.algo_NOP);
        //setCase(OP_INVOKEHANDLE,    () -> this.algo_INVOKEHANDLE);
        setCase(OP_IMPDEP1,         () -> this.algo_NOP);
        setCase(OP_IMPDEP2,         () -> this.algo_NOP);

        //the remaining bytecodes should never appear in a classfile
        setDefault(() -> this.algo_NOTALLOWED);
    }

    public Action_PREINIT selectPreInit() {
        return this.action_PREINIT;
    }

    public Action_INIT selectInit() {
        return this.action_INIT;
    }

    @Override
    public Algorithm<?, ?, ?, ?, ?> select(Byte bytecode) {
        final Algorithm<?, ?, ?, ?, ?> retVal;
        try {
            retVal = super.select(bytecode);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            //this should never happen
            throw new UnexpectedInternalException(e);
        }
        return retVal;
    }
}
