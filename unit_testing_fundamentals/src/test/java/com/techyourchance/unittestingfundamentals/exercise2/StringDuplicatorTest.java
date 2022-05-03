package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {

    StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        String result = SUT.duplicate("");
        assertThat(result, is(""));
    }

    @Test
    public void duplicate_includingSpace_returnStringIncludingSpace() {
        String result = SUT.duplicate(" ");
        assertThat(result, is("  "));
    }

    @Test
    public void duplicate_singleString_returnDoubleString() {
        String result = SUT.duplicate("dup");
        assertThat(result, is("dupdup"));
    }

}