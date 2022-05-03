package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    @Test
    public void isOverlap_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(1, 3);
        Interval interval2 = new Interval(5, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isOverlap_interval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(5, 8);
        Interval interval2 = new Interval(1, 3);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isOverlap_interval1BeforeAdjacentInterval2_trueReturned() {
        Interval interval1 = new Interval(1, 3);
        Interval interval2 = new Interval(3, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    @Test
    public void isOverlap_interval1AfterAdjacentInterval2_trueReturned() {
        Interval interval1 = new Interval(5, 8);
        Interval interval2 = new Interval(1, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    @Test
    public void isOverlap_interval1ContainsInterval2_falseReturned() {
        Interval interval1 = new Interval(-1, 9);
        Interval interval2 = new Interval(1, 3);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isOverlap_interval1ContainedWithInterval2_falseReturned() {
        Interval interval1 = new Interval(6, 10);
        Interval interval2 = new Interval(5, 32);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_interval1OverlapsInterval2OnStart_falseReturned() throws Exception {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(3, 12);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, CoreMatchers.is(false));
    }

    @Test
    public void isAdjacent_interval1EqualsInterval2_falseReturned() throws Exception {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-1, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, CoreMatchers.is(false));
    }

    @Test
    public void isAdjacent_interval1OverlapsInterval2OnEnd_falseReturned() throws Exception {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-4, 4);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, CoreMatchers.is(false));
    }

}