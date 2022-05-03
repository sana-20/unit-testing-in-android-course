package com.techyourchance.unittesting.testdata;

import com.techyourchance.unittesting.questions.Question;
import com.techyourchance.unittesting.questions.QuestionDetails;

import java.util.LinkedList;
import java.util.List;

public class QuestionDetailsTestData {

    public static QuestionDetails getQuestionDetails() {
        return new QuestionDetails("id", "title", "body");
    }
}
