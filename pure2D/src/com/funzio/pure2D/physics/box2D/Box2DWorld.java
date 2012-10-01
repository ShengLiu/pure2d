/**
 * 
 */
package com.funzio.pure2D.physics.box2D;

import android.os.Handler;
import android.os.Looper;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;

import com.funzio.pure2D.physics.PhysicsWorld;

/**
 * @author long
 */
public class Box2DWorld extends World implements PhysicsWorld {
    private static int SEQ_ID = 0;
    public static final String THREAD_NAME = "Box2D";
    public static final int TARGET_FPS = 60;
    public static final float MAX_FRAME_TIME = 1f; // max frame time to avoid Spiral of death
    public static final float TIME_STEP = (1f / TARGET_FPS);
    public static final int TIME_STEP_MS = (1000 / TARGET_FPS);

    protected float mDt = TIME_STEP;
    protected int mVelocityIterations = 1;
    protected int mPositionIterations = 1;
    protected boolean mPlaying = false;
    protected float mPixelsPerMeter = 64;

    private long mStartTime;
    private float mAccumulatedTime;

    private Body mGroundBody;

    protected Handler mHandler;
    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            // next
            if (mPlaying) {
                update();
                // loop
                mHandler.postDelayed(mUpdateRunnable, TIME_STEP_MS);
            }
        }
    };
    private final Thread mThread = new Thread(new Runnable() {

        @Override
        public void run() {
            // prepare
            Looper.prepare();

            // the main handler
            mHandler = new Handler();

            // check and play
            if (mPlaying) {
                mHandler.post(mUpdateRunnable);
            }

            // start loop
            Looper.loop();
        }
    }, THREAD_NAME + "_" + (++SEQ_ID));

    public Box2DWorld(final Vec2 gravity, final boolean doSleep) {
        super(gravity, doSleep);

        // for smooth rendering with interpolation
        setAutoClearForces(false);

        mGroundBody = createBody(new BodyDef());
    }

    public void start() {
        mStartTime = System.nanoTime();
        mAccumulatedTime = 0;
        mPlaying = true;

        if (!mThread.isAlive()) {
            mThread.start();
        } else {
            mHandler.post(mUpdateRunnable);
        }
    }

    public void stop() {
        mPlaying = false;
    }

    public void setStepParams(final float dt, final int velocityIterations, final int positionIterations) {
        mDt = dt;
        mVelocityIterations = velocityIterations;
        mPositionIterations = positionIterations;
    }

    /**
     * Handle fixed time-step
     */
    protected void update() {
        // delta time
        final long now = System.nanoTime();
        final float delta = ((now - mStartTime) / 1000000000f); // seconds
        mAccumulatedTime += (delta > MAX_FRAME_TIME) ? MAX_FRAME_TIME : delta;
        mStartTime = now;

        // too fast?
        if (mAccumulatedTime < mDt) {
            return;
        }

        while (mAccumulatedTime >= mDt) {
            render(1f);
            step(mDt, mVelocityIterations, mPositionIterations);
            mAccumulatedTime -= mDt;
        }

        // for smooth rendering with interpolation
        clearForces();

        // correct the visual
        if (mAccumulatedTime > 0) {
            render(mAccumulatedTime / mDt);
        }
    }

    protected void render(final float alpha) {
        // TODO
    }

    public void post(final Runnable r) {
        mHandler.post(r);
    }

    public float p2M(final float pixels) {
        return pixels / mPixelsPerMeter;
    }

    public float m2P(final float meters) {
        return meters * mPixelsPerMeter;
    }

    public Body getGroundBody() {
        return mGroundBody;
    }

    public Fixture getFixtureAt(final float x, final float y) {
        final AABB aabb = new AABB();
        aabb.lowerBound.set(new Vec2(x - 0.001f, y - 0.001f));
        aabb.upperBound.set(new Vec2(x + 0.001f, y + 0.001f));
        final Vec2 point = new Vec2(x, y);

        final TestQueryCallback callback = new TestQueryCallback(point);
        queryAABB(callback, aabb);

        return callback.getFixture();
    }

    public class TestQueryCallback implements QueryCallback {
        private final Vec2 mPoint;
        private Fixture mFixture;

        public TestQueryCallback(final Vec2 point) {
            mPoint = point;
        }

        @Override
        public boolean reportFixture(final Fixture fixture) {
            final Shape shape = fixture.getShape();
            if (shape.testPoint(fixture.getBody().getTransform(), mPoint)) {
                mFixture = fixture;
                return true;
            }

            return false;
        }

        public Fixture getFixture() {
            return mFixture;
        }
    }
}