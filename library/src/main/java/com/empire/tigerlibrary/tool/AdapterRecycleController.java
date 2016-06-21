package com.empire.tigerlibrary.tool;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * managing adapter of ListView's recycling for preventing Out of Memory
 * Created by lordvader on 2016. 3. 18..
 */
public class AdapterRecycleController {
    private final boolean IS_SHOW_LOG = true;
    private final int DEFAULT_QUEUE_SIZE = 10;
    private final int QUEUE_SIZE;
    private LinkedList<Integer> mItemList = new LinkedList<Integer>();

    public AdapterRecycleController(int size) {
        QUEUE_SIZE = size;
    }

    public AdapterRecycleController() {
        QUEUE_SIZE = DEFAULT_QUEUE_SIZE;

    }

    /**
     * [temp] show log message
     *
     * @param message
     */
    private void LOG(String message) {
        if (IS_SHOW_LOG) {
            Log.v("VV", message);
        }
    }

    /**
     * enqueue item to last (if size is full, dequeue first item)
     *
     * @param item
     * @return
     */
    private Integer enqueueToRear(Integer item) {
        LOG("[BidirectionQueue] enqueueToRear / item = " + item);
        LOG("[BidirectionQueue] enqueueToRear / mItemList.size() = " + mItemList.size());

        Integer dequeueItem = null;

        if (mItemList.size() >= QUEUE_SIZE) {
            dequeueItem = mItemList.removeFirst();
        }

        mItemList.add(item);

        // temp
        if (IS_SHOW_LOG) {
            tempShowAllItem();
        }

        return dequeueItem;
    }

    /**
     * enqueue item to firat (if size is full, dequeue last item)
     *
     * @param item
     * @return
     */
    private Integer enqueueToFront(Integer item) {
        LOG("[BidirectionQueue] enqueueToFront / item = " + item);
        LOG("[BidirectionQueue] enqueueToFront / mItemList.size() = " + mItemList.size());

        Integer dequeueItem = null;

        if (mItemList.size() >= QUEUE_SIZE) {
            dequeueItem = mItemList.removeLast();
        }

        mItemList.addFirst(item);

        // temp
        if (IS_SHOW_LOG) {
            tempShowAllItem();
        }

        return dequeueItem;
    }

    /**
     * [temp] show all item for checking validation
     */
    private void tempShowAllItem() {
        int itemSize = mItemList.size();
        Integer item;

        for (int i = 0; i < itemSize; i++) {
            item = (Integer) mItemList.get(i);

            LOG("[BidirectionQueue] tempShowAllItem / index = [" + i + "] item = " + item);
        }
    }

    /**
     * buffering item
     *
     * @param item
     * @return
     */
    public Integer bufferingItem(Integer item) {
        LOG("[BidirectionQueue] / bufferingItem / item = " + item);

        Integer dequeueItem = null;

        if (!mItemList.contains(item)) {
            if (mItemList.size() > 0) {
                Integer lastItem = mItemList.getLast();

                if (lastItem != null) {
                    dequeueItem = lastItem <= item ? enqueueToRear(item) : enqueueToFront(item);
                } else {
                    dequeueItem = enqueueToRear(item);
                }
            } else {
                dequeueItem = enqueueToRear(item);
            }
        }

        return dequeueItem;
    }

    /**
     * adjust item when composor is changed
     *
     * @param pivotItem
     */
    public void adjustItem(Integer pivotItem) {
        LOG("[BidirectionQueue] / adjustItem / pivotItem = " + pivotItem);

        ArrayList<Integer> removeTargetItemList = new ArrayList<Integer>();
        Integer item = null;

        // search remove target index
        for (int i = 0; i < mItemList.size(); i++) {
            item = mItemList.get(i);

            if (item >= pivotItem) {
                removeTargetItemList.add(item);
            }
        }

        // remove item
        for (Integer removeTargetItem : removeTargetItemList) {
            mItemList.remove(removeTargetItem);
        }

        // temp
        if (IS_SHOW_LOG) {
            tempShowAllItem();
        }
    }
}

