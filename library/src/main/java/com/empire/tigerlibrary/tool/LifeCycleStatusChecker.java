package com.empire.tigerlibrary.tool;

/**
 * check each card's life cycle
 * Created by lordvader on 2016. 3. 9..
 */
public class LifeCycleStatusChecker {
    public static final int FLAG_ON_ATTACH = 0x1;
    public static final int FLAG_ON_CREATE = 0x2;
    public static final int FLAG_ON_START = 0x04;
    public static final int FLAG_ON_RESUME = 0x8;

    public static final int FLAG_ON_PAUSE = 0x8;
    public static final int FLAG_ON_STOP = 0x4;
    public static final int FLAG_ON_DESTROY = 0x2;
    public static final int FLAG_ON_DETACH = 0x1;

    private int mCheckFlag = 0;

    /**
     * check current life cycle
     *
     * @param flag
     */
    public void setStatus(StatusFlag flag) {
        if (flag.isLiveCycle) {
            mCheckFlag |= flag.flagCode;
        } else {
            mCheckFlag ^= flag.flagCode;
        }
    }

    /**
     * check whether such life cycle is executed previously
     *
     * @param flag
     * @return
     */
    public boolean isExecuted(StatusFlag flag) {
        if (flag.isLiveCycle) {
            return (mCheckFlag & flag.flagCode) == flag.flagCode;
        } else {
            return !((mCheckFlag & flag.flagCode) == flag.flagCode);
        }
    }

    /**
     * define status flag
     *
     * @author lordvader
     */
    private enum StatusFlag {
        onAttach(0x1, true),
        onCreate(0x2, true),
        onStart(0x4, true),
        onResume(0x8, true),
        onPause(0x8, false),
        onStop(0x4, false),
        onDestory(0x2, false),
        onDetach(0x1, false);

        public final int flagCode;
        public final boolean isLiveCycle;

        private StatusFlag(int flagCode, boolean isLiveCycle) {
            this.flagCode = flagCode;
            this.isLiveCycle = isLiveCycle;
        }
    }
}