package com.techyourchance.unittesting.questions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

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

    String QUESTION_ID = "question_id";
    String QUESTION_TITLE = "question_title";
    String QUESTION_BODY = "question_body";

    @Mock
    FetchQuestionDetailsEndpoint mHttpEndpointMock;
    @Mock
    FetchQuestionDetailsUseCase.Listener mockListener1;
    @Mock
    FetchQuestionDetailsUseCase.Listener mockListener2;
    @Captor
    ArgumentCaptor<QuestionDetails> argumentCaptor;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchQuestionDetailsUseCase(mHttpEndpointMock);
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() throws Exception {
        // Arrange
        success();
        // Act
        SUT.registerListener(mockListener1);
        SUT.registerListener(mockListener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        // Assert
        verify(mockListener1).onQuestionDetailsFetched(argumentCaptor.capture());
        verify(mockListener2).onQuestionDetailsFetched(argumentCaptor.capture());
        List<QuestionDetails> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(getQuestionDetails()));
        assertThat(captures.get(1), is(getQuestionDetails()));
    }


    @Test
    public void fetchQuestionDetailsAndNotify_failure_listenersNotifiedOfFailrue() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.registerListener(mockListener1);
        SUT.registerListener(mockListener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        // Assert
        verify(mockListener1).onQuestionDetailsFetchFailed();
        verify(mockListener2).onQuestionDetailsFetchFailed();
    }


    private void success() {
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            FetchQuestionDetailsEndpoint.Listener listener = (FetchQuestionDetailsEndpoint.Listener) args[1];
            listener.onQuestionDetailsFetched(new QuestionSchema(QUESTION_TITLE, QUESTION_ID, QUESTION_BODY));
            return null;
        }).when(mHttpEndpointMock).fetchQuestionDetails(eq(QUESTION_ID), any(FetchQuestionDetailsEndpoint.Listener.class));
    }

    private void failure() {
        doAnswer( invocation -> {
            Object[] arguments = invocation.getArguments();
            FetchQuestionDetailsEndpoint.Listener listener = (FetchQuestionDetailsEndpoint.Listener) arguments[1];
            listener.onQuestionDetailsFetchFailed();
            return null;
        }).when(mHttpEndpointMock).fetchQuestionDetails(eq(QUESTION_ID), any(FetchQuestionDetailsEndpoint.Listener.class));
    }

    private QuestionDetails getQuestionDetails() {
        return new QuestionDetails(QUESTION_ID, QUESTION_TITLE, QUESTION_BODY);
    }


}