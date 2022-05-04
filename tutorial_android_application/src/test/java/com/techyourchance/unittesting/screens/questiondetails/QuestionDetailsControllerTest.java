package com.techyourchance.unittesting.screens.questiondetails;

import static org.mockito.Mockito.verify;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionDetailsTestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {

    private static final QuestionDetails QUESTION_DETAILS = QuestionDetailsTestData.getQuestionDetails1();
    private static final String QUESTION_ID = QUESTION_DETAILS.getId();

    private QuestionDetailsController SUT;

    @Mock
    FetchQuestionDetailsUseCase questionDetailsUseCaseMock;

    @Mock
    ScreensNavigator screensNavigatorMock;

    @Mock
    ToastsHelper toastsHelperMock;

    @Mock
    QuestionDetailsViewMvc questionDetailsViewMvcMock;

    @Before
    public void setUp() throws Exception {
        SUT = new QuestionDetailsController(questionDetailsUseCaseMock, screensNavigatorMock, toastsHelperMock);
        SUT.bindView(questionDetailsViewMvcMock);
        SUT.bindQuestionId(QUESTION_ID);
    }

    @After
    public void tearDown() throws Exception {
        SUT = null;
    }

    @Test
    public void onStart_listenersRegistered() throws Exception {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).registerListener(SUT);
        verify(questionDetailsUseCaseMock).registerListener(SUT);
    }

    @Test
    public void onStop_listenersUnregistered() throws Exception {
        // Arrange
        SUT.onStart();
        // Act
        SUT.onStop();
        // Assert
        verify(questionDetailsViewMvcMock).unregisterListener(SUT);
        verify(questionDetailsUseCaseMock).unregisterListener(SUT);
    }

    @Test
    public void onStart_success_questionDetailsBoundToView() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).bindQuestion(QUESTION_DETAILS);
    }

    @Test
    public void onStart_failure_errorToastShown() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(toastsHelperMock).showUseCaseError();
    }


    @Test
    public void onStart_progressIndicationShown() throws Exception {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).showProgressIndication();
    }

    @Test
    public void onStart_success_progressIndicationHidden() throws Exception {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onStart_failure_progressIndicationsHidden() throws Exception {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvcMock).hideProgressIndication();
    }

    @Test
    public void onNavigationUpClicked_navigatedUp() throws Exception {
        // Arrange
        // Act
        SUT.onNavigateUpClicked();
        // Assert
        verify(screensNavigatorMock).navigateUp();
    }

    private void success() {
        SUT.onQuestionDetailsFetched(QUESTION_DETAILS);
    }

    private void failure() {
        SUT.onQuestionDetailsFetchFailed();
    }

}