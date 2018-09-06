package com.mrizkypk.umsdaily.other;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

public class NoAnimationItemAnimator extends SimpleItemAnimator {
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        dispatchRemoveFinished(holder);

        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        dispatchAddFinished(holder);

        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        dispatchMoveFinished(holder);

        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        dispatchChangeFinished(oldHolder, true);
        dispatchChangeFinished(newHolder, false);

        return false;
    }

    @Override
    public void runPendingAnimations() {
        // stub
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        // stub
    }

    @Override
    public void endAnimations() {
        // stub
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
