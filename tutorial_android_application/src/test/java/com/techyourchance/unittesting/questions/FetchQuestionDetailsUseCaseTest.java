package com.techyourchance.unittesting.questions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.util.Log;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.testdata.QuestionDetailsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    FetchQuestionDetailsUseCase SUT;

    // region constants ----------------------------------------------------------------------------
    private static final long CACHE_TIMEOUT = 60000;
    private static final QuestionDetails QUESTION_DETAILS_1 = QuestionDetailsTestData.getQuestionDetails1();
    private static final String QUESTION_ID_1 = QUESTION_DETAILS_1.getId();
    private static final QuestionDetails QUESTION_DETAILS_2 = QuestionDetailsTestData.getQuestionDetails2();
    private static final String QUESTION_ID_2 = QUESTION_DETAILS_2.getId();
    // endregion constants -------------------------------------------------------------------------

    @Mock
    FetchQuestionDetailsEndpoint mHttpEndpointMock;
    @Mock
    FetchQuestionDetailsUseCase.Listener mockListener1;
    @Mock
    FetchQuestionDetailsUseCase.Listener mockListener2;
    @Mock
    TimeProvider timeProviderMock;
    @Captor
    ArgumentCaptor<QuestionDetails> argumentCaptor;

    private int mEndpointCallsCount;


    @Before
    public void setUp() throws Exception {
        SUT = new FetchQuestionDetailsUseCase(mHttpEndpointMock, timeProviderMock);
        SUT.registerListener(mockListener1);
        SUT.registerListener(mockListener2);
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() throws Exception {
        // Arrange
        success();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        verify(mockListener1).onQuestionDetailsFetched(argumentCaptor.capture());
        verify(mockListener2).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures.get(1), is(QUESTION_DETAILS_1));
    }


    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedOfFailure() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        verify(mockListener1).onQuestionDetailsFetchFailed();
        verify(mockListener2).onQuestionDetailsFetchFailed();
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeImmediatelyAfterSuccess_listenersNotifiedWithDataFromCache() throws Exception {
        // Arrange
        success();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        verify(mockListener1, times(2)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures.get(1), is(QUESTION_DETAILS_1));

        verify(mockListener2, times(2)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures2 = argumentCaptor.getAllValues();
        assertThat(captures2.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures2.get(1), is(QUESTION_DETAILS_1));

        assertThat(mEndpointCallsCount, is(1));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeRightBeforeTimeoutAfterSuccess_listenersNotifiedWithDataFromCache() throws Exception {
        // Arrange
        success();
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT-1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        verify(mockListener1, times(2)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures.get(1), is(QUESTION_DETAILS_1));

        verify(mockListener2, times(2)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures2 = argumentCaptor.getAllValues();
        assertThat(captures2.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures2.get(1), is(QUESTION_DETAILS_1));
        assertThat(mEndpointCallsCount, is(1));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeRightAfterTimeoutAfterSuccess_listenersNotifiedWithDataFromEndpoint() throws Exception {
        // Arrange
        success();
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        verify(mockListener1, times(2)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures.get(1), is(QUESTION_DETAILS_1));

        verify(mockListener2, times(2)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures2 = argumentCaptor.getAllValues();
        assertThat(captures2.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures2.get(1), is(QUESTION_DETAILS_1));
        assertThat(mEndpointCallsCount, is(2));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_secondTimeWithDifferentIdAfterSuccess_listenersNotifiedWithDataFromEndpoint() throws Exception {
        // Arrange
        success();
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        // Assert
        verify(mockListener1, times(2)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures.get(1), is(QUESTION_DETAILS_2));

        verify(mockListener2, times(2)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures2 = argumentCaptor.getAllValues();
        assertThat(captures2.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures2.get(1), is(QUESTION_DETAILS_2));
        assertThat(mEndpointCallsCount, is(2));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_afterTwoDifferentQuestionsAtDifferentTimesFirstQuestionRightBeforeTimeout_listenersNotifiedWithDataFromCache() throws Exception {
        // Arrange
        success();
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT / 2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT - 1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        // Assert
        verify(mockListener1, times(3)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures.get(1), is(QUESTION_DETAILS_2));
        assertThat(captures.get(2), is(QUESTION_DETAILS_1));

        verify(mockListener2, times(3)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures2 = argumentCaptor.getAllValues();
        assertThat(captures2.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures2.get(1), is(QUESTION_DETAILS_2));
        assertThat(captures2.get(2), is(QUESTION_DETAILS_1));
        assertThat(mEndpointCallsCount, is(2));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_afterTwoDifferentQuestionsAtDifferentTimesSecondQuestionRightBeforeTimeout_listenersNotifiedWithDataFromCache() throws Exception {
        // Arrange
        success();
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(0L);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_1);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT / 2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        when(timeProviderMock.getCurrentTimestamp()).thenReturn(CACHE_TIMEOUT + (CACHE_TIMEOUT / 2) - 1);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2);
        // Assert
        verify(mockListener1, times(3)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures.get(1), is(QUESTION_DETAILS_2));
        assertThat(captures.get(2), is(QUESTION_DETAILS_2));

        verify(mockListener2, times(3)).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures2 = argumentCaptor.getAllValues();
        assertThat(captures2.get(0), is(QUESTION_DETAILS_1));
        assertThat(captures2.get(1), is(QUESTION_DETAILS_2));
        assertThat(captures2.get(2), is(QUESTION_DETAILS_2));
        assertThat(mEndpointCallsCount, is(2));
    }

    private void success() {
        doAnswer(invocation -> {
            mEndpointCallsCount++;

            Object[] args = invocation.getArguments();
            String questionId = (String) args[0];
            FetchQuestionDetailsEndpoint.Listener listener = (FetchQuestionDetailsEndpoint.Listener) args[1];
            QuestionSchema response;

            if(questionId.equals(QUESTION_ID_1)) {
                response = new QuestionSchema(QUESTION_DETAILS_1.getTitle(), QUESTION_DETAILS_1.getId(), QUESTION_DETAILS_1.getBody());
            }else if (questionId.equals(QUESTION_ID_2)) {
                response = new QuestionSchema(QUESTION_DETAILS_2.getTitle(), QUESTION_DETAILS_2.getId(), QUESTION_DETAILS_2.getBody());
            } else {
                throw new RuntimeException("unhandled question id: " + questionId);
            }

            listener.onQuestionDetailsFetched(response);
            return null;
        }).when(mHttpEndpointMock).fetchQuestionDetails(any(String.class), any(FetchQuestionDetailsEndpoint.Listener.class));
    }

    private void failure() {
        doAnswer(invocation -> {
            mEndpointCallsCount++;

            Object[] arguments = invocation.getArguments();
            FetchQuestionDetailsEndpoint.Listener listener = (FetchQuestionDetailsEndpoint.Listener) arguments[1];
            listener.onQuestionDetailsFetchFailed();
            return null;
        }).when(mHttpEndpointMock).fetchQuestionDetails(any(String.class), any(FetchQuestionDetailsEndpoint.Listener.class));
    }


}