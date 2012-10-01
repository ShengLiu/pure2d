/**
 * 
 */
package com.funzio.pure2D.animators;

import java.util.Vector;

/**
 * @author long
 */
public class ActionQueue extends BaseAnimator {
    private Vector<Action> mActions = new Vector<Action>();
    private Action mCurrentAction;
    private int mCurrentIndex = -1;
    private int mNumActions = 0;

    // some default values, do not change this
    private boolean mAutoStart = false;
    private boolean mAutoRemove = true;

    private Action mDelayAction = new Action() {
        @Override
        public void run() {
            // nothing here, just delay
        }
    };

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.Manipulator#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (super.update(deltaTime)) {

            if (mCurrentAction != null && mElapsedTime >= mCurrentAction.mDelay) {
                mCurrentAction.run();
                mElapsedTime -= mCurrentAction.mDelay;

                // if the current action has a duration
                if (mElapsedTime >= mCurrentAction.mDuration) {
                    mElapsedTime -= mCurrentAction.mDuration;
                    // has next?
                    if (next() == null) {
                        end();
                    }
                } else {
                    // add delay
                    mDelayAction.mDelay = mCurrentAction.mDuration;
                    mCurrentAction = mDelayAction;
                }
            }

            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#start()
     */
    @Override
    public void start() {
        super.start();

        next();
    }

    protected Action next() {
        if (mNumActions > 0) {
            if (mAutoRemove) {
                mCurrentIndex = 0;
                mCurrentAction = mActions.remove(mCurrentIndex);
                mNumActions--;
            } else {
                mCurrentIndex = (++mCurrentIndex) % mNumActions;
                mCurrentAction = mActions.get(mCurrentIndex);
            }
        } else {
            mCurrentIndex = -1;
            mCurrentAction = null;
        }

        return mCurrentAction;
    }

    public void add(final Action action) {
        mActions.add(action);
        mNumActions++;

        // auto start
        if (mAutoStart && !mRunning) {
            start();
        }
    }

    public void remove(final Action action) {
        mActions.remove(action);
        mNumActions--;
    }

    public void clear() {
        mActions.clear();
        mNumActions = 0;
        mCurrentIndex = -1;
        mCurrentAction = null;
    }

    /**
     * @return the autoStart
     */
    public boolean isAutoStart() {
        return mAutoStart;
    }

    /**
     * @param autoStart the autoStart to set
     */
    public void setAutoStart(final boolean autoStart) {
        mAutoStart = autoStart;
    }

    public boolean isAutoRemove() {
        return mAutoRemove;
    }

    public void setAutoRemove(final boolean autoRemove) {
        mAutoRemove = autoRemove;
    }

    public abstract static class Action {
        public int mDelay = 0;
        public int mDuration = 0;

        public Action() {
        }

        public abstract void run();

        public Action(final int delay) {
            mDelay = delay;
        }

        public Action(final int delay, final int duration) {
            mDelay = delay;
            mDuration = duration;
        }
    }

}