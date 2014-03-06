/*
 * Copyright (C) 2014 V.Shcryabets (vshcryabets@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.v2soft.AndLib.streams;

/**
 * TODO write description.
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 */
public class ResourceTimeController {
    private static final long TIME_SLOT_MS = 100;
    protected long mRemainedResources;
    protected long mAllowedConsuptionPerSecond;
    protected long mNextTime;

    public ResourceTimeController(long allowedConsumptionPerSecond) {
        mAllowedConsuptionPerSecond = allowedConsumptionPerSecond;
        updateStat();
    }

    private void updateStat() {
        mNextTime = System.currentTimeMillis()+ TIME_SLOT_MS;
        mRemainedResources = (int) (mAllowedConsuptionPerSecond * TIME_SLOT_MS/1000);
    }

    public void checkRemainedResources() throws InterruptedException {
        if ( mRemainedResources < 1 ) {
            waitTimeSlot();
        }
    }

    private void waitTimeSlot() throws InterruptedException {
        long current = System.currentTimeMillis();
        if ( mNextTime > current ) {
            Thread.sleep(mNextTime-current);
        }
        updateStat();
    }

    public long requestResources(long resourceCount) {
        if ( resourceCount > mRemainedResources ) {
            resourceCount = mRemainedResources;
        }
        return resourceCount;
    }

    public void decrementResources(long resourceCount) {
        mRemainedResources -= resourceCount;
    }
}
