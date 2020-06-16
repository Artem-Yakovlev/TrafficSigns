package com.example.mlroadsigndetection;

import org.junit.Test;

import io.reactivex.Flowable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.subscribers.TestSubscriber;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        BehaviorProcessor<Integer> processor = BehaviorProcessor.create();

        final Integer EMPTY = Integer.MIN_VALUE;

        Flowable<Integer> flowable = processor.filter(v -> v != EMPTY);

        TestSubscriber<Integer> ts1 = flowable.test();

        processor.onNext(1);
        // this will "clear" the cache
        processor.onNext(EMPTY);

        TestSubscriber<Integer> ts2 = flowable.test();

        processor.onNext(2);
        processor.onComplete();

        // ts1 received both non-empty items
        ts1.assertResult(1, 2);

        // ts2 received only 2 even though the current item was EMPTY
        // when it got subscribed
        ts2.assertResult(2);

        // Subscribers coming after the processor was terminated receive
        // no items and only the onComplete event in this case.
        flowable.test().assertResult();
    }
}