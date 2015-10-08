/*
 * Copyright 2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.rx.client;

import com.mongodb.async.SingleResultCallback;
import rx.Observable;
import rx.Subscriber;

import java.util.List;

abstract class SingleResultListOnSubscribeAdapter<TResult> implements Observable.OnSubscribe<TResult> {

    @Override
    public void call(final Subscriber<? super TResult> subscriber) {
        execute(getCallback(subscriber));
    }


    SingleResultCallback<List<TResult>> getCallback(final Subscriber<? super TResult> subscriber) {
        return new SingleResultCallback<List<TResult>>() {
            @Override
            public void onResult(final List<TResult> results, final Throwable t) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                if (t != null) {
                    subscriber.onError(t);
                } else {
                    if (results != null) {
                        for (TResult result : results) {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(result);
                            }
                        }
                    }
                    subscriber.onCompleted();
                }
            }
        };
    }

    abstract void execute(SingleResultCallback<List<TResult>> callback);

    SingleResultListOnSubscribeAdapter() {
    }
}